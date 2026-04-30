package com.example.app21try6.grafik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.databinding.ItemListBarCartBinding
import com.example.app21try6.formatRupiah
import java.util.Locale

class BarChartModelRVAdapter(
    val clickListener: BarChartModelRvListener
): ListAdapter<BarChartModel,BarChartModelRVAdapter.MyViewHolder>(BarChartModelRvTransDiffCallBack()){

    class MyViewHolder private constructor(val binding: ItemListBarCartBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: BarChartModel, bcListener: BarChartModelRvListener){
            binding.item= item
            binding.clickListener = bcListener
            if (item.value>10000){
                binding.textValue.text=formatRupiah(item.value)
            }else{
                val i = String.format(Locale.US,"%.2f", item.value)
                binding.textValue.text=i.toString()
            }
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListBarCartBinding.inflate(layoutInflater,parent,false)
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

class BarChartModelRvTransDiffCallBack: DiffUtil.ItemCallback<BarChartModel>(){
    override fun areItemsTheSame(oldItem: BarChartModel, newItem: BarChartModel): Boolean {
        return oldItem.label ==oldItem.label
    }
    override fun areContentsTheSame(oldItem: BarChartModel, newItem: BarChartModel): Boolean {
        return oldItem==newItem
    }
}

class BarChartModelRvListener(val clickListener: (bcModel: BarChartModel) -> Unit) {
    fun onClick(bcModel: BarChartModel) = clickListener(bcModel)
}
