package com.example.app21try6.statement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.app21try6.database.DiscountTable

import com.example.app21try6.databinding.ItemListSDiscountBinding

class DiscountAdapter(
    val discountListener:DiscountListener,
    val longListener: DiscountLongListener
): ListAdapter<DiscountTable, DiscountAdapter.MyViewHolder>(DiscountDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListSDiscountBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: DiscountTable, discountListener: DiscountListener, longListener: DiscountLongListener){
            //binding.Discount = item

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
        holder.bind(getItem(position),discountListener,longListener)
    }

}
class DiscountDiffCallback: DiffUtil.ItemCallback<DiscountTable>(){
    override fun areItemsTheSame(oldItem: DiscountTable, newItem: DiscountTable): Boolean {
        return oldItem.discountId == newItem.discountId
    }

    override fun areContentsTheSame(oldItem: DiscountTable, newItem: DiscountTable): Boolean {
        return oldItem == newItem
    }

}
class DiscountListener(val clickListener: (discountTable: DiscountTable) -> Unit) {
    fun onClick(discount: DiscountTable) = clickListener(discount)

}
class  DiscountLongListener(val longListener: (discount: DiscountTable) -> Unit){
    fun onLongClick(v: View, discount: DiscountTable): Boolean {
        //logic goes here
        longListener(discount)
        return true}
}