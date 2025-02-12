package com.example.app21try6.stock.subproductstock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.databinding.ItemListSubDetailBinding
import com.example.app21try6.databinding.ItemListSubproductBinding

class DetailWarnaAdapter (
                  val longListener: DetailWarnaLongListener,
) :
    ListAdapter<DetailWarnaTable,
           DetailWarnaAdapter.MyViewHolder>(DetailWarnaDiffCallback()){
    class MyViewHolder private constructor( val binding: ItemListSubDetailBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
                 item: DetailWarnaTable
        ){
            binding.item = item
            binding.txtNet.text=item.net.toString() +"Meter"
            binding.txtBatchCount.text=item.batchCount.toString() +"Roll"
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListSubDetailBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class DetailWarnaDiffCallback : DiffUtil.ItemCallback<DetailWarnaTable>() {
    override fun areItemsTheSame(oldItem: DetailWarnaTable, newItem: DetailWarnaTable): Boolean {
        return oldItem.detailId == newItem.detailId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DetailWarnaTable, newItem: DetailWarnaTable): Boolean {
        return oldItem == newItem
    }
}
class DetailWarnaLongListener(val longListener:(DetailWarnaTable)->Unit){
    fun onLongClick(v: View, detailWarnaTable: DetailWarnaTable):Boolean{
        longListener(detailWarnaTable)
        return true
    }
}
