package com.example.app21try6.statement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.app21try6.database.DiscountTable

import com.example.app21try6.databinding.ItemListSDiscountBinding
import com.example.app21try6.formatRupiah

class DiscountAdapter(
    val discountListener:DiscountListener,
    val longListener: DiscountLongListener,
    val delListener: DiscountDelListener
): ListAdapter<DiscountAdapterModel, DiscountAdapter.MyViewHolder>(DiscountDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListSDiscountBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: DiscountAdapterModel, discountListener: DiscountListener, longListener: DiscountLongListener,delListener: DiscountDelListener){
            binding.item=item
            binding.delListener=delListener
            binding.clickListener= discountListener
            binding.txtDiscountName.text = item.discountName ?: item.expense_category_name
            binding.txtMinQty.text = item.minimumQty?.toString() ?: item.expense_name
            binding.txtCustLocation.text = item.custLocation?.toString() ?: item.date.toString() ?: ""
            binding.txtDiscType.text = item.discountType ?: ""  // If no discountType, set empty string

            val value = item.discountValue ?: item.expense_ammount?.toDouble() ?: 0.0
            binding.txtDiscValue.text = formatRupiah(value)
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListSDiscountBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscountAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DiscountAdapter.MyViewHolder, position: Int) {
        holder.bind(getItem(position),discountListener,longListener,delListener)
    }

}
class DiscountDiffCallback: DiffUtil.ItemCallback<DiscountAdapterModel>(){
    override fun areItemsTheSame(oldItem: DiscountAdapterModel, newItem: DiscountAdapterModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DiscountAdapterModel, newItem: DiscountAdapterModel): Boolean {
        return oldItem == newItem
    }

}
class DiscountListener(val clickListener: (discountTable: DiscountAdapterModel) -> Unit) {
    fun onClick(discount: DiscountAdapterModel) = clickListener(discount)

}
class DiscountDelListener(val clickListener: (discountTable: DiscountAdapterModel) -> Unit) {
    fun onClick(discount: DiscountAdapterModel) = clickListener(discount)
}
class  DiscountLongListener(val longListener: (discount: DiscountAdapterModel) -> Unit){
    fun onLongClick(v: View, discount: DiscountAdapterModel): Boolean {

        longListener(discount)
        return true}
}