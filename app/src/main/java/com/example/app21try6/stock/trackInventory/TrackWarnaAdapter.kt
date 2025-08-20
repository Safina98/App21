package com.example.app21try6.stock.trackInventory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.Constants
import com.example.app21try6.database.models.TracketailWarnaModel
import com.example.app21try6.databinding.ItemListTrackDetailWarnaBinding


class TrackWarnaAdapter(
    val context: Context
): ListAdapter<TracketailWarnaModel, TrackWarnaAdapter.MyViewHolder>(TrackWarnaDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListTrackDetailWarnaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: TracketailWarnaModel, context: Context){
            binding.item=item
            if (item.unit_qty!=1.0){
                binding.txtItemQty.text=String.format("%.2f Roll (%.1f)",item.qty,item.unit_qty)
            }else{
                binding.txtItemQty.text=String.format("Qty: %.2f",item.qty)
            }
            binding.txtDate.text= Constants.DETAILED_DATE_FORMATTER.format(item.tans_detail_date)
            binding.txtKet.text=if(item.sum_note==null)"-" else item.sum_note
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTrackDetailWarnaBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackWarnaAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrackWarnaAdapter.MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(getItem(position),context)
    }

}
class TrackWarnaDiffCallback: DiffUtil.ItemCallback<TracketailWarnaModel>(){
    override fun areItemsTheSame(oldItem: TracketailWarnaModel, newItem: TracketailWarnaModel): Boolean {
        return oldItem.trans_detail_id == newItem.trans_detail_id
    }

    override fun areContentsTheSame(oldItem: TracketailWarnaModel, newItem: TracketailWarnaModel): Boolean {
        return oldItem == newItem
    }

}