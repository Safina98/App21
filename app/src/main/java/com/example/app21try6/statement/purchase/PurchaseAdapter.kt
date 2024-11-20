package com.example.app21try6.statement.purchase

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.databinding.ItemListTransPurchaseBinding
import com.example.app21try6.formatRupiah


class PurchaseAdapter (val updateListener: UpdateListener,
                       val deleteListener: DeleteListener
):
    ListAdapter<InventoryPurchase, PurchaseAdapter.MyViewHolder>(BookDiffCallback()) {
    class MyViewHolder private constructor(val binding:ItemListTransPurchaseBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: InventoryPurchase,
                 updateListener: UpdateListener,
                 deleteListener: DeleteListener
        ){
            binding.model= item
            //binding.textSubproductV.text =item.categoryName
            binding.textQty.text="${item.batchCount} Roll"
            binding.textNet.text="${item.net} M"
            binding.textPrice.text="${formatRupiah(item.price.toDouble())}"
            binding.textTotalPrice.text="${formatRupiah(item.totalPrice)}"
            binding.updateListener =updateListener
            binding.deleteListener =deleteListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTransPurchaseBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),
            updateListener,deleteListener)
    }

}
class BookDiffCallback : DiffUtil.ItemCallback<InventoryPurchase>() {
    override fun areItemsTheSame(oldItem: InventoryPurchase, newItem: InventoryPurchase): Boolean {
        return oldItem.id== newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: InventoryPurchase, newItem: InventoryPurchase): Boolean {
        return oldItem == newItem
    }
}
class UpdateListener(val clickListener: (model: InventoryPurchase) -> Unit) {
    fun onClick(model: InventoryPurchase) = clickListener(model)

}
class DeleteListener(val clickListener: (model: InventoryPurchase) -> Unit) {
    fun onClick(model: InventoryPurchase) = clickListener(model)

}