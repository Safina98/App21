package com.example.app21try6.stock.brandstock
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.databinding.ItemListBrandBinding

class BrandStockAdapter(
    val brandListener:BrandStockListener,
    val longListener: BrandStockLongListener,
    var selectedItemId:Int?
):ListAdapter<BrandProductModel,BrandStockAdapter.MyViewHolder>(BrandStockDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListBrandBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: BrandProductModel, brandListener: BrandStockListener, longListener: BrandStockLongListener, isSelected:Boolean){
            binding.brandStock = item
            binding.brandText.text = item.name.toString()
            binding.clickListener = brandListener
            binding.longListener = longListener
            binding.brandCv.setBackgroundColor(if (isSelected) Color.LTGRAY else Color.TRANSPARENT)
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =ItemListBrandBinding.inflate(layoutInflater,parent,false)
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
        val item = getItem(position)
        val isSelected = item.id == selectedItemId

        holder.bind(getItem(position),brandListener,longListener,isSelected)
    }

}
class BrandStockDiffCallback:DiffUtil.ItemCallback<BrandProductModel>(){
    override fun areItemsTheSame(oldItem: BrandProductModel, newItem: BrandProductModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BrandProductModel, newItem: BrandProductModel): Boolean {
        return oldItem == newItem
    }

}
class BrandStockListener(val clickListener: (brandProductModel_id: BrandProductModel) -> Unit) {
    fun onClick(brandProductModel: BrandProductModel) = clickListener(brandProductModel)
}
class  BrandStockLongListener(val longListener: (brandProductModel: BrandProductModel) -> Unit){
    fun onLongClick(v: View, brandProductModel: BrandProductModel): Boolean {
        //logic goes here
        longListener(brandProductModel)

        return true}
}