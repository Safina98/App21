package com.example.app21try6.statement.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.databinding.ItemListReportBinding
import com.example.app21try6.formatRupiah

class ReportAdapter (
    val clickListener: ReportListener
): ListAdapter<BarChartModel, ReportAdapter.MyViewHolder>(ReportDiffCallBack()){
    class MyViewHolder private constructor(val binding: ItemListReportBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: BarChartModel, bcListener: ReportListener){
            binding.item= item
            binding.txtValue.text=formatRupiah(item.value)
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListReportBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }
}
class ReportDiffCallBack: DiffUtil.ItemCallback<BarChartModel>(){
    override fun areItemsTheSame(oldItem: BarChartModel, newItem: BarChartModel): Boolean {
        return oldItem.label ==oldItem.label
    }
    override fun areContentsTheSame(oldItem: BarChartModel, newItem: BarChartModel): Boolean {
        return oldItem==newItem
    }
}

class ReportListener(val clickListener: (bcModel: BarChartModel) -> Unit) {
    fun onClick(bcModel: BarChartModel) = clickListener(bcModel)
}