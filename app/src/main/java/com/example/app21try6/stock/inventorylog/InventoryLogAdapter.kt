package com.example.app21try6.stock.inventorylog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.database.models.InventoryLogWithSubProduct
import com.example.app21try6.databinding.ItemListLogBinding
import java.util.Locale


class InventoryLogAdapter (
   val updateListener: InventoryLogUpdateListener,
     val   deleteListener: InventoryLogDeleteListener
             )   : ListAdapter<InventoryLogWithSubProduct, InventoryLogAdapter.MyViewHolder>(InventoryLogDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListLogBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: InventoryLogWithSubProduct, updateListener: InventoryLogUpdateListener,deleteListener: InventoryLogDeleteListener){
            binding.item = item
            binding.updateLisener=updateListener
            binding.deleteLisener=deleteListener
            binding.txtLogDate.text= DETAILED_DATE_FORMATTER.format(item.inventoryLog.barangLogDate)
            binding.txtIsi.text=String.format(Locale.US,"%.2f", item.inventoryLog.isi)
            binding.txtInventoryName.text=item.sub_name
            binding.txtBatchCount.text=item.inventoryLog.pcs.toString()
            binding.txtKeterangan.text=item.inventoryLog.barangLogKet
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

class InventoryLogDiffCallback: DiffUtil.ItemCallback<InventoryLogWithSubProduct>(){
    override fun areItemsTheSame(oldItem: InventoryLogWithSubProduct, newItem: InventoryLogWithSubProduct): Boolean {
        return oldItem.inventoryLog.id == newItem.inventoryLog.id
    }

    override fun areContentsTheSame(oldItem: InventoryLogWithSubProduct, newItem: InventoryLogWithSubProduct): Boolean {
        return oldItem == newItem
    }

}
class InventoryLogUpdateListener(val clickListener: (inventoryLog: InventoryLogWithSubProduct) -> Unit) {
    fun onClick(inventoryLog: InventoryLogWithSubProduct) = clickListener(inventoryLog)
}
class InventoryLogDeleteListener(val clickListener: (inventoryLog: InventoryLogWithSubProduct) -> Unit) {
    fun onClick(inventoryLog: InventoryLogWithSubProduct) = clickListener(inventoryLog)
}
