package com.example.app21try6.stock.inventorylog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.databinding.ItemListLogBinding
import java.util.Locale


class InventoryLogAdapter (
   val updateListener: InventoryLogUpdateListener,
     val   deleteListener: InventoryLogDeleteListener
             )   : ListAdapter<InventoryLog, InventoryLogAdapter.MyViewHolder>(InventoryLogDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListLogBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: InventoryLog, updateListener: InventoryLogUpdateListener,deleteListener: InventoryLogDeleteListener){
            binding.item = item
            binding.updateLisener=updateListener
            binding.deleteLisener=deleteListener
            binding.txtLogDate.text= DETAILED_DATE_FORMATTER.format(item.barangLogDate)
            binding.txtIsi.text=String.format(Locale.US,"%.2f", item.isi)
            binding.txtInventoryName.text=item.detailWarnaRef
            binding.txtBatchCount.text=item.pcs.toString()
            binding.txtKeterangan.text=item.barangLogKet
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListLogBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {
        holder.bind(getItem(position),updateListener,deleteListener )
    }


}

class InventoryLogDiffCallback: DiffUtil.ItemCallback<InventoryLog>(){
    override fun areItemsTheSame(oldItem: InventoryLog, newItem: InventoryLog): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: InventoryLog, newItem: InventoryLog): Boolean {
        return oldItem == newItem
    }

}
class InventoryLogUpdateListener(val clickListener: (inventoryLog: InventoryLog) -> Unit) {
    fun onClick(inventoryLog: InventoryLog) = clickListener(inventoryLog)
}
class InventoryLogDeleteListener(val clickListener: (inventoryLog: InventoryLog) -> Unit) {
    fun onClick(inventoryLog: InventoryLog) = clickListener(inventoryLog)
}
