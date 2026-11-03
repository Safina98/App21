package com.example.app21try6.stock.exportprice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.models.ExportStockPriceModel
import com.example.app21try6.databinding.ItemListExportStockPriceBinding

class ExportStockPriceAdapter(
    val clickListener: ESPListener,
    val rollClickListener: ESPRollListener,
): ListAdapter<ExportStockPriceModel, ExportStockPriceAdapter.MyViewHolder>(ESPDiffCallBack()){
    class MyViewHolder private constructor(val binding: ItemListExportStockPriceBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ExportStockPriceModel, clickListener: ESPListener,rollClickListener: ESPRollListener){
            binding.product = item
            binding.clickListener = clickListener
            binding.rollClickListener=rollClickListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListExportStockPriceBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener,rollClickListener)
    }
}

class ESPDiffCallBack: DiffUtil.ItemCallback<ExportStockPriceModel>(){
    override fun areItemsTheSame(oldItem: ExportStockPriceModel, newItem: ExportStockPriceModel): Boolean {
        return oldItem.id ==oldItem.id
    }
    override fun areContentsTheSame(oldItem: ExportStockPriceModel, newItem: ExportStockPriceModel): Boolean {
        return oldItem==newItem
    }
}
class ESPListener(val clickListener: (esp: ExportStockPriceModel) -> Unit) {
    fun onClick(esp: ExportStockPriceModel) = clickListener(esp)
}
class ESPRollListener(val clickListener: (esp: ExportStockPriceModel) -> Unit) {
    fun onClick(esp: ExportStockPriceModel) = clickListener(esp)
}