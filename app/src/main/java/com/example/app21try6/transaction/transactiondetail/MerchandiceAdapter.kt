package com.example.app21try6.transaction.transactiondetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.R
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.databinding.ItemListVendibleBinding
import com.example.app21try6.databinding.TextItemViewBinding

/*
class MerchandiseAdapter(
    //val clickListener: MerchCheckBoxListener,
    private val onCheckChanged: (() -> Unit)? = null
    ):
    ListAdapter<MerchandiseRetail, MerchandiseAdapter.MyViewHolder>(MerchandiseDiffCallBack())
{
    val checkedMap: MutableMap<Int, Boolean> = mutableMapOf()
    class MyViewHolder private constructor(val binding: TextItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: MerchandiseRetail
           // checkBoxListener: MerchCheckBoxListener,
        ) {
            binding.item = item
            binding.txtNet.text=item.net.toString()
            binding.checkboxNet.setOnCheckedChangeListener { _, checked ->
                checkedMap[item.id] = checked
                onCheckChanged?.invoke()
            }
      //           binding.checkBoxListener = checkBoxListener
           // binding.textMerch.text = item.net.toString()
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): MerchandiseAdapter.MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    TextItemViewBinding.inflate(layoutInflater, parent, false)
                return MerchandiseAdapter.MyViewHolder(binding)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
        // Update background color based on clickedItems
    }
    fun getCheckedItems(): List<MerchandiseRetail> {
        return currentList.filter { checkedMap[it.id] == true }
    }

}




class MerchandiseDiffCallBack: DiffUtil.ItemCallback<MerchandiseRetail>(){
    override fun areItemsTheSame(oldItem: MerchandiseRetail, newItem: MerchandiseRetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MerchandiseRetail, newItem: MerchandiseRetail): Boolean {
        return oldItem == newItem
    }
}
class MerchCheckBoxListener(val checkBoxListener:(view: View, merch:MerchandiseRetail)->Unit){
    fun onCheckBoxClick(view: View, merch: MerchandiseRetail)= checkBoxListener(view,merch)
}

 */








class MerchandiseAdapter(
    private val onCheckChanged: (() -> Unit)? = null
) : ListAdapter<DetailMerchandiseModel, MerchandiseAdapter.ViewHolder>(MerchandiseDiffCallback()) {

    private val checkedMap: MutableMap<Int, Boolean> = mutableMapOf()

    inner class ViewHolder(private val binding: TextItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetailMerchandiseModel, isChecked: Boolean) {
            // Safe manual binding
            binding.item=item
            Log.d("BIND_CHECK", "Binding item: $item")
            binding.checkboxNet.setOnCheckedChangeListener(null)
            binding.checkboxNet.isChecked = isChecked

            binding.checkboxNet.setOnCheckedChangeListener { _, checked ->
                checkedMap[item.id] = checked
                onCheckChanged?.invoke()
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TextItemViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("ADAPTER_ITEM", "Item at $position: $item")
        val isChecked = checkedMap[item.id] ?: false
        holder.bind(item, isChecked)
    }

    fun getCheckedItems(): List<DetailMerchandiseModel> {
        return currentList.filter { checkedMap[it.id] == true }
    }
}

class MerchandiseDiffCallback : DiffUtil.ItemCallback<DetailMerchandiseModel>() {
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

