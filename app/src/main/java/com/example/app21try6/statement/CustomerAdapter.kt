package com.example.app21try6.statement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.CustomerTable
import com.example.app21try6.database.DiscountTable
import com.example.app21try6.databinding.ItemListCustomerBinding
import com.example.app21try6.databinding.ItemListSDiscountBinding

class CustomerAdapter(
    val customerListener:CustomerListener,
    val longListener: CustomerLongListener,
    val delListener: CustomerDelListener
): ListAdapter<CustomerTable, CustomerAdapter.MyViewHolder>(CustomerDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListCustomerBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: CustomerTable, customerListener: CustomerListener, longListener: CustomerLongListener, delListener: CustomerDelListener){
            binding.item=item
            binding.delListener=delListener
            binding.clickListener=customerListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListCustomerBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CustomerAdapter.MyViewHolder, position: Int) {
        holder.bind(getItem(position),customerListener,longListener,delListener)
    }

}
class CustomerDiffCallback: DiffUtil.ItemCallback<CustomerTable>(){
    override fun areItemsTheSame(oldItem: CustomerTable, newItem: CustomerTable): Boolean {
        return oldItem.custId == newItem.custId
    }

    override fun areContentsTheSame(oldItem: CustomerTable, newItem: CustomerTable): Boolean {
        return oldItem == newItem
    }

}
class CustomerListener(val clickListener: (customerTable: CustomerTable) -> Unit) {
    fun onClick(customer: CustomerTable) = clickListener(customer)

}
class CustomerDelListener(val clickListener: (customerTable: CustomerTable) -> Unit) {
    fun onClick(customer: CustomerTable) = clickListener(customer)
}
class  CustomerLongListener(val longListener: (customer: CustomerTable) -> Unit){
    fun onLongClick(v: View, customer: CustomerTable): Boolean {
        //logic goes here
        longListener(customer)
        return true}
}