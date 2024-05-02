package com.example.app21try6.transaction.transactiondetail

import android.Manifest
import android.R.attr.value
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionDetailBinding
import java.io.OutputStream
import java.util.*


class TransactionDetailFragment : Fragment() {
    private lateinit var binding:FragmentTransactionDetailBinding
    private val PERMISSION_REQUEST_CODE = 201
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var printerService: BluetoothPrinterService
    private lateinit var selectedPrinter: BluetoothDevice
    private lateinit var viewModel:TransactionDetailViewModel
    var receiptText=""

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Toast.makeText(context,"permission granted",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"permission denied",Toast.LENGTH_SHORT).show()

            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            com.example.app21try6.R.layout.fragment_transaction_detail,container,false)
        val application= requireNotNull(this.activity).application
        val id= arguments?.let{ TransactionDetailFragmentArgs.fromBundle(it).id}
        Log.e("SUMVM","transum in TransEditFragmnet id is "+id.toString()+"")
        val datasource1 = VendibleDatabase.getInstance(application).transSumDao
        val datasource2 = VendibleDatabase.getInstance(application).transDetailDao
        val viewModelFactory = TransactionDetailViewModelFactory(application,datasource1,datasource2,id!!)
        viewModel =ViewModelProvider(this,viewModelFactory).get(TransactionDetailViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission()
        }
        val adapter = TransactionDetailAdapter(TransDetailClickListener {

        })
        binding.recyclerViewDetailTrans.adapter = adapter
        receiptText = viewModel.generateReceiptTextNew()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


        binding.btnCetak.setOnClickListener {
            printReceipt()
            /*

            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                val printerService = PrinterService(bluetoothAdapter)
                val printerAddress = "DC:0D:30:27:4B:D4" // Replace with your printer's Bluetooth address
                val textToPrint = viewModel.generateReceiptText()
               // printText(bluetoothAdapter,printerAddress,textToPrint)
                //printLongText(bluetoothAdapter,printerAddress,textToPrint)
            } else {
                // Bluetooth is either not supported or not enabled
            }

             */
        }


        viewModel.transDetail.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.sendReceipt.observe(viewLifecycleOwner){
            if (it==true){
                var exportedString=viewModel.generateReceiptText()

                exportTextToWhatsApp(exportedString)
            }
        }
        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                //Toast.makeText(context,id.toString(),Toast.LENGTH_SHORT).show()
                this.findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(id))
                viewModel.onNavigatedToEdit()
                //var exportedString=""
                //exportTextToWhatsApp("Text successfully exported")
            }
        })

        return binding.root
    }
/*
    fun printText(bluetoothAdapter: BluetoothAdapter,printerAddress: String, text: String) {
        val printerDevice: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(printerAddress)

        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
        if (checkPermission())
        {
            val socket: BluetoothSocket? = printerDevice?.createRfcommSocketToServiceRecord(uuid)

            socket?.use { sock ->
                try {
                    sock.connect()
                    val outputStream: OutputStream = sock.outputStream
                    outputStream.write(text.toByteArray())
                    Log.i("PRINTB","try "+text)
                } catch (e: Exception) {
                    Log.i("PRINTB","cathc "+e)
                    // Handle connection errors
                }
            }
        }
        }

 */


    private fun printReceipt() {
        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            // Request user to enable Bluetooth
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
           // startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            // Discover and select printer device
            discoverAndSelectPrinter()
        }
    }

    private fun discoverAndSelectPrinter() {

        if (checkPermission()) {
            val pairedDevices = bluetoothAdapter.bondedDevices
            val printerDevices =
                pairedDevices.filter { it.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.IMAGING }
            if (printerDevices.isEmpty()) {
                Toast.makeText(context, "No printer found", Toast.LENGTH_SHORT).show()
                return
            }
            // For simplicity, let's select the first printer device found
            selectedPrinter = printerDevices.first()
            // Connect to the selected printer
            printerService = BluetoothPrinterService(selectedPrinter)
            printerService.connect()
            // Print the receipt
            val receiptData = generateReceiptData()
            printerService.print(receiptData)

            // Disconnect after printing
           //
        }


    }

    private fun generateReceiptData(): ByteArray {
        // Generate receipt data according to printer's specifications
        // For example, let's assume a simple receipt format with text and line breaks
        receiptText = viewModel.generateReceiptTextNew()
        Log.i("PRINTB",receiptText)
        return receiptText.toByteArray()
    }







    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH_CONNECT)
        val permission2 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH_SCAN)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(requireActivity(),arrayOf<String>(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN), PERMISSION_REQUEST_CODE)
    }


       private fun exportTextToWhatsApp(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            // Set the MIME type
            type = "text/plain"
            // Set the package name of WhatsApp
            setPackage("com.whatsapp")
        }
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(sendIntent)
        }catch (e : Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        printerService.disconnect()
        super.onDestroy()
    }

}