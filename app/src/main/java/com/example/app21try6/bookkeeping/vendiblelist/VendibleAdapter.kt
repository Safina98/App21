package com.example.app21try6.bookkeeping.vendiblelist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.Product

import com.example.app21try6.databinding.ItemListVendibleBinding

class VendibleAdapter (
        val code:Int,
    val checkBoxListener: CheckBoxListener,
    val textListener: TextListener,
    val delLongListenerV:DelLongListenerV
): ListAdapter<Product,
        VendibleAdapter.MyViewHolder>(BookDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListVendibleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(code: Int,
                item:Product,
                 checkBoxListener: CheckBoxListener,
                 textListener:TextListener,
                 delLongListenerV:DelLongListenerV
        ){
                binding.vendible = item
                binding.textSubproductV.text =item.product_name
                binding.checkBoxListener = checkBoxListener
                //binding.checkBox.visibility= View.GONE
                binding.checkbox.isChecked= item.checkBoxBoolean
                binding.textListener = textListener
                binding.delLongListenerV = delLongListenerV
                binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =ItemListVendibleBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(code,
                getItem(position),
                checkBoxListener,
                textListener,
        delLongListenerV)
    }
}
class BookDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.product_price == newItem.product_price
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
class CheckBoxListener(val checkBoxListener:(view:View, product:Product)->Unit){
    fun onCheckBoxClick(view:View, product:Product)= checkBoxListener(view,product)
}
class TextListener(val textListener:(view:View, product:Product)->Unit){
    fun onTextClick(view:View, product:Product)= textListener(view,product)
}
class DelLongListenerV(val delLongListenerV:(product:Product)->Unit){
    fun onLongClick(v: View, product:Product):Boolean{
        delLongListenerV(product)
        return true
    }
}


