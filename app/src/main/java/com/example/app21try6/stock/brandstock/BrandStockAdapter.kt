package com.example.app21try6.stock.brandstock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.Brand

import com.example.app21try6.databinding.BrandItemListBinding

class BrandStockAdapter(
    val brandListener:BrandStockListener,
    val longListener: BrandStockLongListener
):ListAdapter<Brand,BrandStockAdapter.MyViewHolder>(BrandStockDiffCallback()){
    class MyViewHolder private constructor(val binding: BrandItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:Brand,brandListener: BrandStockListener,longListener: BrandStockLongListener){
            binding.brandStock = item
            binding.brandText.text = item.brand_name.toString()
            binding.clickListener = brandListener
            binding.longListener = longListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =BrandItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandStockAdapter.MyViewHolder {
       return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BrandStockAdapter.MyViewHolder, position: Int) {
        holder.bind(getItem(position),brandListener,longListener)
    }

}
class BrandStockDiffCallback:DiffUtil.ItemCallback<Brand>(){
    override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem.brand_id == newItem.brand_id
    }

    override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem == newItem
    }

}
class BrandStockListener(val clickListener: (brand_id: Brand) -> Unit) {
    fun onClick(brand:Brand) = clickListener(brand)

}
class  BrandStockLongListener(val longListener: (brand: Brand) -> Unit){
    fun onLongClick(v: View, brand: Brand): Boolean {
        //logic goes here

        longListener(brand)

        return true}
}