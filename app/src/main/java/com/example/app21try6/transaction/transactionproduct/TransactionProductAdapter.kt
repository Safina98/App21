package com.example.app21try6.transaction.transactionproduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.Product

import com.example.app21try6.databinding.ItemListTransactionProductBinding
import com.example.app21try6.stock.productstock.ProductStockListener
import com.example.app21try6.stock.productstock.ProductStockLongListener

class TransactionProductAdapter (
    val clickListener:ProductTransListener
        ): ListAdapter<Product,TransactionProductAdapter.MyViewHolder>(ProductTransDiffCallBack()){

    class MyViewHolder private constructor(val binding: ItemListTransactionProductBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Product, produtListener: ProductTransListener){
            binding.product = item
            binding.clickListener = produtListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTransactionProductBinding.inflate(layoutInflater,parent,false)
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
class ProductTransDiffCallBack: DiffUtil.ItemCallback<Product>(){
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.product_id==oldItem.product_id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem==newItem
    }

}
class ProductTransListener(val clickListener: (product_id: Product) -> Unit) {
    fun onClick(product: Product) = clickListener(product)

}