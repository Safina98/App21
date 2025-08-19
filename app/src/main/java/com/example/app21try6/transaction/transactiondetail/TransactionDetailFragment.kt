package com.example.app21try6.transaction.transactiondetail

import android.Manifest
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.databinding.FragmentTransactionDetailBinding
import com.example.app21try6.databinding.PopUpListDialogBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class TransactionDetailFragment : Fragment() {
    private lateinit var binding:FragmentTransactionDetailBinding
    private val PERMISSION_REQUEST_CODE = 201
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var printerService: BluetoothPrinterService
    private lateinit var selectedPrinter: BluetoothDevice
    private lateinit var viewModel:TransactionDetailViewModel
    var receiptText=""

    private object type {
        const val Payment = "Payment"
        const val Discount = "Discount"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,
           R.layout.fragment_transaction_detail,container,false)
        val application= requireNotNull(this.activity).application
        Log.d("DetailFragment", "onCreateView called")
        val id = arguments?.let { TransactionDetailFragmentArgs.fromBundle(it).id }
            ?: throw IllegalArgumentException("ID argument is missing")

        val stockRepositories=StockRepositories(application)
        val transRepositories=TransactionsRepository(application)
        val bookRepository = BookkeepingRepository(application)
        val discountRepository=DiscountRepository(application)

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val viewModelFactory = TransactionDetailViewModelFactory(stockRepositories,bookRepository,transRepositories,discountRepository,application,id)
        viewModel =ViewModelProvider(this,viewModelFactory).get(TransactionDetailViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        val img =  requireActivity().findViewById<ImageView>(R.id.delete_image)
        img?.visibility = View.GONE

        binding.viewModel = viewModel
        viewModel.getSummaryWithNullProductId()
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if (bluetoothManager!=null)
            bluetoothAdapter = bluetoothManager.adapter
        val paymentAdapter = PaymentAdapter(viewModel.transSum.value?.is_paid_off ?: false,
            TransPaymentClickListener {
              showBayarDialog(it,type.Payment)
            },TransPaymentLongListener {
                deleteDialog(it.id!!,type.Payment)
            },
            TransDatePaymentClickListener {
                showDatePickerDialog(it)
            })
        val discAdapter = PaymentAdapter(viewModel.transSum.value?.is_paid_off,
            TransPaymentClickListener {
                showBayarDialog(it,type.Discount)
            },TransPaymentLongListener {
                deleteDialog(it.id!!,type.Discount)
            },
            TransDatePaymentClickListener {
                //showDatePickerDialog(it)
            })
        //Transaction Detail Adapter
        val adapter = TransactionDetailAdapter(
            TransDetailClickListener {item->
                Log.i("MERCHPROBS","clicked")
                viewModel.updateRetailOnClick(item)
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
        binding.recyclerViewDiscount.adapter=discAdapter

        binding.btnPrintNew.setOnClickListener {
            fibrateOnClick()
            printReceipt()
        }
        viewModel.multipleMerch.observe(viewLifecycleOwner){
            if (it!=null){
                showMerchandiseRetailDialog(it)
            }
        }
        viewModel.pickNewItem.observe(viewLifecycleOwner){
            if (it!=null){
                showDetailWarnaDialog(it)
            }
        }

        viewModel.transDetail.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.discSum.observe(viewLifecycleOwner) {}

        //viewModel.transDetailWithProduct.observe(viewLifecycleOwner) { it?.let {} }
        viewModel.discountTransBySumId.observe(viewLifecycleOwner) {
            it?.let {
                discAdapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
        binding.recyclerViewBayar.adapter = paymentAdapter

        viewModel.setUiMode(nightModeFlags)

        viewModel.transSum.observe(viewLifecycleOwner){it?.let{
            viewModel.setTxtNoteValue(it.sum_note)
        } }

        viewModel.isn.observe(viewLifecycleOwner) { isNoteActive -> }

        viewModel.paymentModel.observe(viewLifecycleOwner) {
            it?.let {
                paymentAdapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.transSumDateLongClick.observe(viewLifecycleOwner){
            if (it==true){
                showDatePickerDialog(null)
            }
        }
        viewModel.sendReceipt.observe(viewLifecycleOwner){
            if (it==true){
                val exportedString=viewModel.generateReceiptTextWa()
                exportTextToWhatsApp(exportedString)
                viewModel.onKirimBtnClicked()
            }
        }
        viewModel.isBtnpaidOff.observe(this.viewLifecycleOwner) {
            if (it == true) {
                (binding.recyclerViewDetailTrans.adapter as TransactionDetailAdapter).isActive(it)
            } else {
                (binding.recyclerViewDetailTrans.adapter as TransactionDetailAdapter).deActivate()
            }
            adapter.notifyDataSetChanged()

        }

        viewModel.isCardViewShow.observe(viewLifecycleOwner) {}
        viewModel.isTxtNoteClick.observe(viewLifecycleOwner) {}
        viewModel.isBtnBayarCLicked.observe(viewLifecycleOwner) {
            if (it == true) {
                showBayarDialog(
                    PaymentModel(null, null, null, null, null, null, "Bayar:", null),
                    type.Payment
                )
                viewModel.onBtnBayarClicked()
            }
        }
        viewModel.isDiscClicked.observe(viewLifecycleOwner) {
            if (it == true) {
                showBayarDialog(
                    PaymentModel(null, null, null, null, null, null, null, null),
                    type.Discount
                )
                viewModel.onBtnDiscClicked()
            }
        }

        viewModel.uiMode.observe(viewLifecycleOwner) {}

        viewModel.navigateToEdit.observe(viewLifecycleOwner) {
            it?.let {
                this.findNavController().navigate(
                    TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(
                        id
                    )
                )
                viewModel.onNavigatedToEdit()
            }
        }

        return binding.root
    }

    private fun showBayarDialog(paymentModel: PaymentModel, typem:String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Bayar")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update_bayar, null)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textUpdatePrice)
        val textNameOrDate=view.findViewById<TextInputEditText>(R.id.textUpdateDate)
        val ilDate=view.findViewById<TextInputLayout>(R.id.ilUpdateDate)
        val ilPrice=view.findViewById<TextInputLayout>(R.id.il_harga)

        if (paymentModel.payment_ammount!=null){
            textPrice.setText(paymentModel.payment_ammount.toString())
        }
        if (typem==type.Payment){
            textNameOrDate.visibility=View.GONE
            ilDate.visibility=View.GONE
        //textNameOrDate.setText(SIMPLE_DATE_FORMATTER.format(paymentModel.payment_date ?: Date()))
        }else{
            textNameOrDate.setText(paymentModel.name?:"")
            ilDate.hint="Nama Diskon"
        }
        ilPrice.hint="Jumlah"

        textPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        textPrice.requestFocus()

        builder.setView(view)
        builder.setPositiveButton("Ok") { dialog, which ->
            if(textPrice.text.toString().toIntOrNull()!=null){
                paymentModel.payment_ammount = textPrice.text.toString().toInt()
                if (typem==type.Payment) {
                    viewModel.bayar(paymentModel)
                }
                else{
                    paymentModel.name = textNameOrDate.text.toString().trim().uppercase()
                    viewModel.updateDiscount(paymentModel)
                }
            }
        }
        builder.setNegativeButton("No") { dialog, which ->

        }

        val alert = builder.create()
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))

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
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.datePickerStart)
        val datePickerEnd = dialogView.findViewById<DatePicker>(R.id.datePickerEnd)
        datePickerEnd.visibility = View.GONE

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Get the selected date
                val calendar = Calendar.getInstance().apply {
                    set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
                }
                // Show Time Picker Dialog
                TimePickerDialog(requireContext(), { _, hour, minute ->
                    // Set selected time
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    val selectedDateTime = calendar.time // Convert to Date object

                    // Update PaymentModel or ViewModel
                    if (paymentModel != null) {
                        paymentModel.payment_date = selectedDateTime
                        viewModel.bayar(paymentModel)
                    } else {
                        viewModel.updateLongClickedDate(selectedDateTime)
                    }
                }, 12, 0, true).show() // Default time is 12:00, 24-hour format enabled
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Customize button colors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))

    }

    private fun deleteDialog(p_id:Int,typem:String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                if (typem==type.Payment)
                viewModel.deletePayment(p_id)
                else {
                    viewModel.deleteDiscount(p_id)
                }
                Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
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

    private fun showMerchandiseRetailDialog(trans:TransactionDetail) {
        val binding = PopUpListDialogBinding.inflate(layoutInflater)
        val recyclerView = binding.recyclerViewVendibleDialog
        val resultText = binding.txtTotal
        resultText.text = String.format(Locale.getDefault(), "Remaining : %.2f", trans.qty)//"Remaining ${trans.qty}"

        lateinit var adapter: MerchandiseAdapter
        var remaining=trans.qty
        var extra=0.0
        adapter = MerchandiseAdapter () {
            val selectedSum = adapter.getCheckedItems().sumOf { it.net }
            if((remaining-selectedSum)>=0)
                remaining = trans.qty +extra - selectedSum
            else{
                remaining=0.0
            }
            resultText.text = String.format(Locale.getDefault(), "Remaining : %.2f", remaining)//"Remaining: $remaining"
        }

       binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
           extra= when (checkedId) {
                R.id.rd_kain->   0.2
                R.id.rd_busa ->  0.1
                else -> 0.0
            }
           resultText.text = "Current value: %.2f".format((remaining+extra))
           Log.i("RDP","extra :${extra}")
        }
        //recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        viewModel.retailMerchList.observe(viewLifecycleOwner){
            if (it!=null){
                adapter.submitList(it.toList())
            }
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(trans.trans_item_name)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (remaining==0.0){
                    val selectedItems = adapter.getCheckedItems()
                    viewModel.onMerchSelected(selectedItems,extra)
                    dialog.dismiss()
                }else{

                    Toast.makeText(context,"net tidak cukup",Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.setOnDismissListener {
            viewModel.setValuesToNull()
            dialog.dismiss()
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }

    private fun showDetailWarnaDialog(itemName:String) {
        val binding = PopUpListDialogBinding.inflate(layoutInflater)
        val recyclerView = binding.recyclerViewVendibleDialog
        binding.txtTotal.visibility=View.INVISIBLE


        lateinit var adapter: MerchandiseAdapter


        adapter = MerchandiseAdapter () {
            val selectedSum = adapter.getCheckedItems().sumOf { it.net }
        }

        binding.radioGroup.visibility=View.INVISIBLE
        //recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        viewModel.retailMerchList.observe(viewLifecycleOwner){
            if (it!=null){
                adapter.submitList(it.toList())
            }
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Stok Utuh $itemName")
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val selectedItems = adapter.getCheckedItems()
                viewModel.onMerchSelected(selectedItems,0.0)
                dialog.dismiss()
            }
        }
        dialog.setOnDismissListener {
            viewModel.setValuesToNull()
            dialog.dismiss()
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }
}