package com.example.app21try6.transaction.transactiondetail

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionDetailBinding
import com.example.app21try6.transaction.transactionactive.TransactionActiveAdapter
import com.example.app21try6.transaction.transactionedit.Code
import com.example.app21try6.transaction.transactionedit.TransactionEditViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
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
        val datasource3 = VendibleDatabase.getInstance(application).summaryDbDao
        val datasource4 = VendibleDatabase.getInstance(application).paymentDao
        val datasource5 = VendibleDatabase.getInstance(application).subProductDao
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val viewModelFactory = TransactionDetailViewModelFactory(application,datasource1,datasource2,datasource3,datasource4,datasource5,id!!)
        viewModel =ViewModelProvider(this,viewModelFactory).get(TransactionDetailViewModel::class.java)
        binding.lifecycleOwner = this
        val img =  requireActivity().findViewById<ImageView>(R.id.delete_image)
        img.visibility = View.GONE
        binding.viewModel = viewModel
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val paymentAdapter = PaymentAdapter(viewModel.transSum.value?.is_paid_off,
            TransPaymentClickListener {
              showBayarDialog(it)
            },TransPaymentLongListener {
                deleteDialog(it.id!!)
            },
            TransDatePaymentClickListener {
                showDatePickerDialog(it)
            })
        //Transaction Detail Adapter
        val adapter = TransactionDetailAdapter(
            TransDetailClickListener {
            },
            TransDetailLongListener {it->
                it.is_prepared = it.is_prepared.not()
                viewModel.updateTransDetail(it)
            }
        )

        if (checkPermission()) {
            //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission()
        }

        binding.recyclerViewDetailTrans.adapter = adapter

        binding.btnPrintNew.setOnClickListener {
           fibrateOnClick()
            printReceipt()
        }
        viewModel.transDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        binding.recyclerViewBayar.adapter = paymentAdapter

        viewModel.setUiMode(nightModeFlags)

        viewModel.transSum.observe(viewLifecycleOwner){
            viewModel.setTxtNoteValue(it.sum_note)
        }

        viewModel.isn.observe(this, Observer { isNoteActive -> })

        viewModel.paymentModel.observe(viewLifecycleOwner, Observer {
            it?.let{
                paymentAdapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.transSumDateLongClick.observe(viewLifecycleOwner){
            if (it==true){
                showDatePickerDialog(null)
            }
        }
        viewModel.sendReceipt.observe(viewLifecycleOwner){
            if (it==true){
                val exportedString=viewModel.generateReceiptText()
                exportTextToWhatsApp(exportedString)
                viewModel.onKirimBtnClicked()
            }
        }

        viewModel.isBtnpaidOff.observe(this.viewLifecycleOwner, Observer {
            if (it == true) {
                (binding.recyclerViewDetailTrans.adapter as TransactionDetailAdapter).isActive(it)
            } else {
                (binding.recyclerViewDetailTrans.adapter as TransactionDetailAdapter).deActivate()
            }
            adapter.notifyDataSetChanged()

        })

        viewModel.isCardViewShow.observe(viewLifecycleOwner, Observer {})
        viewModel.isTxtNoteClick.observe(viewLifecycleOwner, Observer {})
        viewModel.isBtnBayarCLicked.observe(viewLifecycleOwner, Observer {
            if (it==true){
                showBayarDialog(PaymentModel(null,null,null,null,null,null,null))
                viewModel.onBtnBayarClicked()
            }
        })

        viewModel.uiMode.observe(viewLifecycleOwner) {}

        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(id))
                viewModel.onNavigatedToEdit()
            }
        })

        return binding.root
    }

    private fun showBayarDialog(paymentModel: PaymentModel){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Bayar")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textUpdateKet)

        if (paymentModel.payment_ammount!=null){
            textPrice.setText(paymentModel.payment_ammount.toString())
        }


        textPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        textPrice.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        builder.setView(view)
        builder.setPositiveButton("Ok") { dialog, which ->
            if(textPrice.text.toString().toIntOrNull()!=null){
                paymentModel.payment_ammount = textPrice.text.toString().toInt()
                viewModel.bayar(paymentModel)
            }
        }
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

    private fun showDatePickerDialog(paymentModel: PaymentModel?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.date_picker)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startDate = Calendar.getInstance().apply {
                    set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
                }.time
                if (paymentModel!=null) {
                    paymentModel.payment_date = startDate
                    viewModel.bayar(paymentModel)
                }
                else viewModel.updateLongClickedDate(startDate)

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
    private fun deleteDialog(p_id:Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                viewModel.deletePayment(p_id)
                Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
    fun fibrateOnClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31 and above
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator

            if (vibrator.hasVibrator()) {
                val vibrationEffect = VibrationEffect.createOneShot(200, 255)
                vibrator.vibrate(vibrationEffect)
            }
        }
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 500)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 300)
    }

    override fun onDestroy() {
       // printerService?.let { it.disconnect() }
        super.onDestroy()
    }

}