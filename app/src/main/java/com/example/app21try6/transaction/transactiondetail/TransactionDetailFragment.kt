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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            com.example.app21try6.R.layout.fragment_transaction_detail,container,false)
        val application= requireNotNull(this.activity).application
        val id= arguments?.let{ TransactionDetailFragmentArgs.fromBundle(it).id}
        Log.i("SUMIDPROB","TransactionDetailFragment argument id $id")
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

        }, TransDetailLongListener {it->

            it.is_prepared = it.is_prepared.not()
            Log.i("BOOLPROB","fragamet${it.is_prepared.toString()}")
            viewModel.updateTransDetail(it)
        }
        )
        binding.recyclerViewDetailTrans.adapter = adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        binding.btnCetak.setOnClickListener {
            printReceipt()
        }
        viewModel.transDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.trans_sum.observe(viewLifecycleOwner){
            Log.i("SUMIDPROB","TransactionDetailFragment Trans_sum observer id $it")
        }
        viewModel.sendReceipt.observe(viewLifecycleOwner){
            if (it==true){
                var exportedString=viewModel.generateReceiptText()
                exportTextToWhatsApp(exportedString)
            }
        }
        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.i("SUMIDPROB","TransactionDetailFragment Navigate to edit oberver $it")
                this.findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(id))
                viewModel.onNavigatedToEdit()
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
           Toast.makeText(context,"BLUETOOTH IS DISCONNECTED", Toast.LENGTH_SHORT).show()
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
        //printerService.disconnect()
        super.onDestroy()
    }

}