package com.example.app21try6.bookkeeping.editdetail

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.Summary
import com.example.app21try6.databinding.FragmentBookKeepeingBinding
import com.google.android.material.textfield.TextInputEditText


class BookKeepeingFragment : Fragment() {
    private lateinit var binding: FragmentBookKeepeingBinding
    private val bookKeepingViewModel  :BookkeepingViewModel by activityViewModels { BookkeepingViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_book_keepeing,container,false)

        //val application = requireNotNull(this.activity).application
        //val dataSource = VendibleDatabase.getInstance(application).summaryDbDao
        //val dataSource2 = VendibleDatabase.getInstance(application).productDao

        val date= arguments?.let { BookKeepeingFragmentArgs.fromBundle(it).date }
        binding.lifecycleOwner = this
        binding.bookViewModel = bookKeepingViewModel
        val adapter = BookkeepingAdapter(PlusBookListener {
           // Toast.makeText(context,(it.item_sold+1).toString(),Toast.LENGTH_SHORT).show()
            bookKeepingViewModel.addBtnClicked(it)
        }, SubsBookListener {
            //Toast.makeText(context,(it.item_sold-1).toString(),Toast.LENGTH_SHORT).show()
            bookKeepingViewModel.subsBtnClicked(it)
        }, PlusBookLongListener {
            //Toast.makeText(context,"plus long clicked",Toast.LENGTH_SHORT).show()
            updateDialog(it,1,bookKeepingViewModel)
        }, SubsBookLongListener {
           // val details = arrayOf(it.id_m.toString(),it.item_sold.toString())
            //Toast.makeText(context,"subs long clicked",Toast.LENGTH_SHORT).show()
            updateDialog(it,2,bookKeepingViewModel)
        }, LongListener {
            updateDialog(it,3,bookKeepingViewModel)
            //Toast.makeText(context,i.toString(),Toast.LENGTH_SHORT).show()
        }, DelLongListener {
            deleteDialog(it,bookKeepingViewModel)
        })
        binding.recyclerViewBook.adapter = adapter

        bookKeepingViewModel.daylySells.observe(viewLifecycleOwner, Observer {
            //Toast.makeText(context,bookKeepingViewModel.dayly_sells.value.toString(),Toast.LENGTH_SHORT).show()
            it?.let {
                adapter.submitList(it.sortedBy { it.item_name})
                adapter.notifyDataSetChanged()
            }
        })
        bookKeepingViewModel.all_item_from_db.observe(viewLifecycleOwner, Observer {

        })
        bookKeepingViewModel.selectedMonth.observe(viewLifecycleOwner){
        }
        bookKeepingViewModel.date.observe(viewLifecycleOwner) {}
        bookKeepingViewModel.navigateToVendible.observe(viewLifecycleOwner, Observer {
            if (it==true) {
                this.findNavController().navigate(BookKeepeingFragmentDirections.actionBookKeepeingFragmentToVendibleFragment(date!!))
                bookKeepingViewModel.onNavigatedToVendivle()
            }

        })
       bookKeepingViewModel.addItem.observe(viewLifecycleOwner, Observer {
           if (it==true){
               bookKeepingViewModel.onItemAdded()
           }
        })

        return binding.root
    }
    private fun updateDialog(summary: Summary, text_number: Int, bookKeepingViewModel: BookkeepingViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val v = textKet.text.toString().uppercase().trim()
            bookKeepingViewModel.btnLongClick(summary,v.toDouble(),text_number)
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }
    private fun deleteDialog(summary: Summary,viewModel:BookkeepingViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    viewModel.deleteItemSummary(summary)
                    Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

}