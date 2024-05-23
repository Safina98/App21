package com.example.app21try6.transaction.transactiondetail

import android.Manifest
import android.R.attr.value
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
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
import com.example.app21try6.R
import com.example.app21try6.database.SummaryDatabase
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionDetailBinding
import com.example.app21try6.transaction.transactionactive.TransactionActiveAdapter
import com.google.android.material.textfield.TextInputEditText
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
           R.layout.fragment_transaction_detail,container,false)
        val application= requireNotNull(this.activity).application
        val id= arguments?.let{ TransactionDetailFragmentArgs.fromBundle(it).id}
        val datasource1 = VendibleDatabase.getInstance(application).transSumDao
        val datasource2 = VendibleDatabase.getInstance(application).transDetailDao
        val datasource3 = SummaryDatabase.getInstance(application).summaryDbDao
        val datasource4 = VendibleDatabase.getInstance(application).paymentDao
        val datasource5 = VendibleDatabase.getInstance(application).subProductDao

        val viewModelFactory = TransactionDetailViewModelFactory(application,datasource1,datasource2,datasource3,datasource4,datasource5,id!!)
        viewModel =ViewModelProvider(this,viewModelFactory).get(TransactionDetailViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission()
        }
        val adapter = TransactionDetailAdapter(
            viewModel.trans_sum.value?.is_paid_off,
            TransDetailClickListener {

        }, TransDetailLongListener {it->
            it.is_prepared = it.is_prepared.not()
            viewModel.updateTransDetail(it)
        }
        )
        binding.recyclerViewDetailTrans.adapter = adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        binding.btnPrintNew.setOnClickListener {
            printReceipt()
        }
        viewModel.transDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })
        val paymentAdapter = PaymentAdapter(viewModel.trans_sum.value?.is_paid_off,TransPaymentClickListener {  },TransPaymentLongListener {  })
        binding.recyclerViewBayar.adapter = paymentAdapter
        viewModel.trans_sum.observe(viewLifecycleOwner){
        }
        viewModel.paymentModel.observe(viewLifecycleOwner, Observer {
            it?.let{
                paymentAdapter.submitList(it)
                Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.sendReceipt.observe(viewLifecycleOwner){
            if (it==true){
                var exportedString=viewModel.generateReceiptText()
                exportTextToWhatsApp(exportedString)
            }
        }
        viewModel.isBtnpaidOff.observe(this.viewLifecycleOwner, Observer {
            Log.i("HIDEDATE","before ${it}")
            if (it == true) {
                Log.i("HIDEDATE","if ${it}")
                (binding.recyclerViewDetailTrans.adapter as TransactionDetailAdapter).isActive(it)
            } else {
                Log.i("HIDEDATE","else ${it}")
                (binding.recyclerViewDetailTrans.adapter as TransactionDetailAdapter).deActivate()
            }
            Log.i("HIDEDATE","after ${it}")
            adapter.notifyDataSetChanged()
        })

        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(id))
                viewModel.onNavigatedToEdit()
            }
        })
        viewModel.isBtnBayarCLicked.observe(viewLifecycleOwner, Observer {
            if (it==true){
                showBayarDialog()
                viewModel.onBtnBayarClicked()
            }
        })
        return binding.root
    }

    private fun showBayarDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            if(textPrice.text.toString().toIntOrNull()!=null){
                viewModel.bayar(textPrice.text.toString().toInt())
            }            }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
    }
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