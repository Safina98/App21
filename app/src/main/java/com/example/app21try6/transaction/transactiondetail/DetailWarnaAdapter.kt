package com.example.app21try6.transaction.transactiondetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.databinding.ItemListSelectDetailWarnaBatchBinding
import com.example.app21try6.databinding.TextItemViewBinding
import com.example.app21try6.transaction.transactionselect.TransSelectModel

class DetailWarnaAdapter(val plusListener:PlusDetailListener,val subsListener: SubsDetailListener
) : ListAdapter<DetailMerchandiseModel,DetailWarnaAdapter.ViewHolder>(DetailWarnaDiffCallback()) {

    private val checkedMap: MutableMap<Int, Boolean> = mutableMapOf()

    inner class ViewHolder(private val binding: ItemListSelectDetailWarnaBatchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetailMerchandiseModel,plusListener: PlusDetailListener,subsListener: SubsDetailListener) {
            // Safe manual binding
            binding.item=item
            binding.plusListener=plusListener
            binding.subsListener=subsListener
            binding.tvId.text= String.format(" %.0f Roll, Net: %.2f", item.batchCount, item.net)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListSelectDetailWarnaBatchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("ADAPTER_ITEM", "Item at $position: $item")
        holder.bind(item,plusListener,subsListener)
    }

}

class DetailWarnaDiffCallback : DiffUtil.ItemCallback<DetailMerchandiseModel>() {
    override fun areItemsTheSame(oldItem: DetailMerchandiseModel, newItem: DetailMerchandiseModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DetailMerchandiseModel, newItem: DetailMerchandiseModel): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.net == newItem.net &&  // Ensure `net` is compared
                oldItem.ref == newItem.ref &&
                oldItem.date == newItem.date
    }
}
class PlusDetailListener(val plusListener:(dmModel:DetailMerchandiseModel)->Unit){
    fun onPlusButtonClick(dmModel:DetailMerchandiseModel)= plusListener(dmModel)
}
class SubsDetailListener(val subsListener:(dmModel:DetailMerchandiseModel)->Unit){
    fun onSubsButtonClick(dmModel:DetailMerchandiseModel)= subsListener(dmModel)
}
