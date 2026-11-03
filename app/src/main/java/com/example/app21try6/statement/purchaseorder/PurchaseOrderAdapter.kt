package com.example.app21try6.statement.purchaseorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.models.PurchaseOrderModel
import com.example.app21try6.databinding.ItemListPurchaseOrderBinding

class PurchaseOrderAdapter(
    val clickListener: POModelListener
): ListAdapter<PurchaseOrderModel, PurchaseOrderAdapter.MyViewHolder>(PurchaseOrderDiffCallBack()) {

    class MyViewHolder private constructor(val binding: ItemListPurchaseOrderBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: PurchaseOrderModel, clickListener: POModelListener){
            binding.model= item
            binding.clickListener = clickListener

            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListPurchaseOrderBinding.inflate(layoutInflater,parent,false)
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
class PurchaseOrderDiffCallBack: DiffUtil.ItemCallback<PurchaseOrderModel>(){
    override fun areItemsTheSame(oldItem: PurchaseOrderModel, newItem: PurchaseOrderModel): Boolean {
        return oldItem.subId ==oldItem.subId
    }
    override fun areContentsTheSame(oldItem: PurchaseOrderModel, newItem: PurchaseOrderModel): Boolean {
        return oldItem==newItem
    }
}
class POModelListener(val clickListener: (pOModel: PurchaseOrderModel) -> Unit) {
    fun onClick(pOModel: PurchaseOrderModel) = clickListener(pOModel)
}
