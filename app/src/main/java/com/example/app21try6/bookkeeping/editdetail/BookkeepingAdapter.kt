package com.example.app21try6.bookkeeping.editdetail

import android.annotation.SuppressLint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.app21try6.database.Summary
//import com.example.app21try6.database.Vendible


import com.example.app21try6.databinding.BookkeepingItemListBinding
import com.example.app21try6.formatRupiah

class  BookkeepingAdapter(
        val plusListener: PlusBookListener,
        val subsListener : SubsBookListener,
        val plusBookLongListener: PlusBookLongListener,
        val subsBookLongListener: SubsBookLongListener,
        val longListener: LongListener,
        val delLongListener: DelLongListener
) :
        ListAdapter<Summary,
                BookkeepingAdapter.MyViewHolder>(BookDiffCallback()){
    class MyViewHolder private constructor(val binding: BookkeepingItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: Summary,
            plusListener: PlusBookListener,
            subsListener : SubsBookListener,
            plusBookLongListener: PlusBookLongListener,
            subsBookLongListener: SubsBookLongListener,
            longListener: LongListener,
            delLongListener: DelLongListener){
            //binding.item = item
            binding.book = item
            //binding.txtProductB.text = item.product.toString()
            binding.txtProductB.text = item.item_name.toString()
            binding.textSellsB.text = item.item_sold.toString()
            binding.textSumsB.text = formatRupiah(item.total_income).toString()
            binding.txtPrice.text = formatRupiah(item.price).toString()
            binding.plusListener = plusListener
            binding.subsListener = subsListener
            binding.plusLongListener = plusBookLongListener
            binding.subsLongListener = subsBookLongListener
            binding.longListener = longListener
            binding.delLongListener = delLongListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =BookkeepingItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),plusListener, subsListener,plusBookLongListener,subsBookLongListener,longListener,delLongListener)

    }

}
class BookDiffCallback : DiffUtil.ItemCallback<Summary>() {
    override fun areItemsTheSame(oldItem: Summary, newItem: Summary): Boolean {
        return oldItem.price == newItem.price
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Summary, newItem: Summary): Boolean {
        return oldItem == newItem
    }
}
class PlusBookListener(val plusListener:(summary: Summary)->Unit){
    fun onPlusButtonClick(summary: Summary)= plusListener(summary)
}
class SubsBookListener(val subsListener:(summary: Summary)->Unit){
    fun onSubsButtonClick(summary: Summary)= subsListener(summary)
}
class PlusBookLongListener(val longListener:(summary:Summary)->Unit){
    fun onLongClick(v: View, summary:Summary):Boolean{
        longListener(summary)
        return true
    }
}
class SubsBookLongListener(val longListener:(summary:Summary)->Unit){
    fun onLongClick(v: View, summary:Summary):Boolean{
        longListener(summary)
        return true
    }
}
class LongListener(val longListener:(summary:Summary)->Unit){
    fun onLongClick(v: View, summary:Summary):Boolean{
        longListener(summary)
        return true
    }
}
class DelLongListener(val delLongListener:(summary:Summary)->Unit){
    fun onLongClick(v: View, summary:Summary):Boolean{
        delLongListener(summary)
        return true
    }
}