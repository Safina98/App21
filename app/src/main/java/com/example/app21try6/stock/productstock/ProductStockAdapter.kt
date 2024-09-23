package com.example.app21try6.stock.productstock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.Product
import com.example.app21try6.databinding.ItemListProductBinding


class ProductStockAdapter(
    val clickListener: ProductStockListener,
    val longListener: ProductStockLongListener)
    :ListAdapter<Product,ProductStockAdapter.MyViewHolder>(ProductStockDiffCallBack()){
    class MyViewHolder private constructor(val binding: ItemListProductBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Product, produtListener: ProductStockListener, longListener: ProductStockLongListener){
            binding.productStock = item
            binding.brandText.text = item.product_name
            binding.clickListener = produtListener
            binding.longListener = longListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListProductBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductStockAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductStockAdapter.MyViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener,longListener)
    }

}
class ProductStockDiffCallBack:DiffUtil.ItemCallback<Product>(){
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.product_id==oldItem.product_id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem==newItem
    }

}
class ProductStockListener(val clickListener: (product_id: Product) -> Unit) {
    fun onClick(product: Product) = clickListener(product)

}
class  ProductStockLongListener(val longListener: (product: Product) -> Unit){
    fun onLongClick(v: View,product: Product): Boolean {
        //logic goes here

        longListener(product)

        return true}
}