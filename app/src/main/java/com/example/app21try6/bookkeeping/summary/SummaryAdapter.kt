package com.example.app21try6.bookkeeping.summary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.app21try6.database.Summary
import com.example.app21try6.databinding.ItemListBookkeepingBinding
import com.example.app21try6.databinding.ItemListSumaryBinding

import com.example.app21try6.formatRupiah

class SummaryAdapter(
        val clickListener: SummaryListener
):
        ListAdapter<ListModel, SummaryAdapter.MyViewHolder>(ProductDiffCallback())
{
    class MyViewHolder private constructor( val binding: ItemListSumaryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: SummaryListener, item: ListModel){
           //if (item.day_n!=0)
           //{
             //  binding.monthTxt.text = item.day_n.toString()
           //}
            //else{
            if (item.nama =="Januari" || item.nama == "Februari"|| item.nama == "Maret"|| item.nama =="April"|| item.nama =="Mei"|| item.nama =="Juni"|| item.nama =="Juli"|| item.nama =="Agustus"|| item.nama =="September"|| item.nama =="Oktober"|| item.nama =="November"|| item.nama =="Desember"){
                binding.monthTxt.text = item.nama.toString()}
            else{
                binding.monthTxt.text = item.nama.toString()+" "+item.day_name
            }

           //}
            //binding.txtYear.text = item.year.toString()
            binding.txtIncome.text = formatRupiah(item.total).toString()
            binding.txtProfit.text = formatRupiah(item.monthly_profit).toString()
            binding.summary = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListSumaryBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyViewHolder{
        return MyViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(clickListener,getItem(position))
    }
}

class ProductDiffCallback: DiffUtil.ItemCallback<ListModel>() {
    override fun areItemsTheSame(oldItem: ListModel, newItem: ListModel): Boolean {
        return oldItem.total == newItem.total
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ListModel, newItem: ListModel): Boolean {
        return oldItem == newItem
    }


}

class SummaryListener (val clickListener:(product: ListModel)->Unit){
    fun onClick(product: ListModel) = clickListener(product)
}
class SummaryLongListener(val longListener:(product: ListModel)->Unit ){
    fun onLongClick(v: View, product: ListModel):Boolean{
        longListener(product)
        return true
    }

}