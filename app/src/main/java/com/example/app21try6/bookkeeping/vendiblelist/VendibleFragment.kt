package com.example.app21try6.bookkeeping.vendiblelist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentVendibleBinding
import com.google.android.material.textfield.TextInputEditText

class VendibleFragment : Fragment() {
    private lateinit var binding: FragmentVendibleBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_vendible,container,false)
        val application = requireNotNull(this.activity).application

        val repository = StockRepositories(application)
        val sumRepo = BookkeepingRepository(application)
        val date= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }
        val datee  = date!!.toMutableList()
        val viewModelFactory = VendibleViewModelFactory(sumRepo,repository, date,application)
        binding.lifecycleOwner =this
        val vendibleViewModel =ViewModelProvider(this,viewModelFactory)
            .get(VendibleViewModel::class.java)
        binding.vendibleViewModel = vendibleViewModel
        val adapter = VendibleAdapter(1, CheckBoxListener { view: View, product: Product ->
            val cb = view as CheckBox
            product.checkBoxBoolean = cb.isChecked
            Toast.makeText(context, product.checkBoxBoolean.toString(), Toast.LENGTH_SHORT).show()

            vendibleViewModel.onCheckBoxClicked(product, cb.isChecked)
        }, TextListener { view, vendible ->

            datee.add(0, vendible.product_id.toString())
            Toast.makeText(context, vendible.product_id.toString() + " " + vendible.product_name + " date[0] " + datee[0], Toast.LENGTH_SHORT).show()
            datee.add(1, vendible.brand_code.toString())
            datee.add(2, vendible.cath_code.toString())
            datee.add(3, date[0])
            datee.add(4, date[1])
            //vendibleViewModel.onNavigateToSub(date)
            showUpdateDialog(vendible,vendibleViewModel)
        }, DelLongListenerV {
            deleteDialog(it, vendibleViewModel)
        })


        binding.recyclerViewVendible.adapter =adapter
        var h =0
        vendibleViewModel.itemCathPosition.observe(viewLifecycleOwner, Observer {t->
            vendibleViewModel.cathList.observe(viewLifecycleOwner, Observer {})
            h = t
            vendibleViewModel.all_item_from_db.observe(viewLifecycleOwner, Observer {
                it?.let { if (h==t){
                    adapter.submitList(it.sortedBy { it.product_name})
                    adapter.notifyDataSetChanged()
                }}
            })
        })

        vendibleViewModel.navigateToEditDetail.observe(viewLifecycleOwner, Observer {
            if (it==true){
                //this.findNavController().navigate(VendibleFragmentDirections.actionVendibleFragmentToBookKeepeingFragment3(date))
                requireActivity().onBackPressed()
                vendibleViewModel.onNavigatedToEditThings()
            }
        })
        vendibleViewModel.navigateToSub.observe(viewLifecycleOwner, Observer {
            it?.let{
                this.findNavController().navigate(VendibleFragmentDirections.actionVendibleFragmentToSubProductStockFragment(datee.toTypedArray()))
               vendibleViewModel.onNavigatedToSub()
                //requireActivity().onBackPressed()
                //vendibleViewModel.onNavigatedToEditThings()
            }
        })
        vendibleViewModel.addItem.observe(viewLifecycleOwner, Observer {
            if (it==true){
                showAddDialog(vendibleViewModel)
                vendibleViewModel.onItemAdded()
            }
        })
        return binding.root
    }

    private fun showUpdateDialog(product: Product, vendibleViewModel: VendibleViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tambah Item")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_add_vendible, null)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textaddPrice)
        val textProduct = view.findViewById<TextInputEditText>(R.id.textaddProduct)
        val textBrand = view.findViewById<TextInputEditText>(R.id.textaddBrand)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox2)

        textProduct.setText(product.product_name.toString())
        textPrice.setText(product.product_price.toString())
        checkBox.isChecked = product.bestSelling
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            product.product_price = textPrice.text.toString().toInt()
            product.product_name = textProduct.text.toString().toUpperCase()
            //product.brand_code = textBrand.text.toString().toUpperCase()
            product.bestSelling = checkBox.isChecked
            vendibleViewModel.updateVendible(product)

        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }

    fun showAddDialog(vendibleViewModel: VendibleViewModel){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tambah Item")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_add_vendible, null)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textaddPrice)
        val textProduct = view.findViewById<TextInputEditText>(R.id.textaddProduct)
        val textBrand = view.findViewById<TextInputEditText>(R.id.textaddBrand)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox2)
        val textCath  = view.findViewById<TextInputEditText>(R.id.textaddCath)
        var product = Product()
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val cath = textCath.text.toString().toUpperCase().trim()
            if(textPrice.text.toString().toIntOrNull()!=null){
                product.product_price = textPrice.text.toString().toInt()}
            product.product_name = textProduct.text.toString().toUpperCase().trim()
            val brand = textBrand.text.toString().toUpperCase().trim()
           product.bestSelling = checkBox.isChecked
            //vendibleViewModel.setCathName(cath)
           vendibleViewModel.insertVendible(cath,brand,product)
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }
    private fun deleteDialog(product: Product, vendibleViewModel: VendibleViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    vendibleViewModel.deleteItemVendible(product.product_id)
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
}