package com.example.app21try6.statement.expenses

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.databinding.ItemListCategoryBinding
import com.example.app21try6.stock.brandstock.CategoryModel

class ExpenseCategoryAdapter (val updateListener: UpdateListener,
                       val deleteListener: DeleteListener):ListAdapter<CategoryModel,ExpenseCategoryAdapter.MyViewHolder>(BookDiffCallback()) {
    class MyViewHolder private constructor(val binding: ItemListCategoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item:CategoryModel,
                 updateListener: UpdateListener,
                 deleteListener: DeleteListener
        ){
            binding.categoryModel = item
            binding.textSubproductV.text =item.categoryName
            binding.updateListener =updateListener
            binding.deleteListener =deleteListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =ItemListCategoryBinding.inflate(layoutInflater,parent,false)
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
class BookDiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
    override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
        return oldItem.categoryName== newItem.categoryName
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
        return oldItem == newItem
    }
}
class UpdateListener(val clickListener: (categoryModel:CategoryModel) -> Unit) {
    fun onClick(categoryModel:CategoryModel) = clickListener(categoryModel)

}
class DeleteListener(val clickListener: (categoryModel:CategoryModel) -> Unit) {
    fun onClick(categoryModel:CategoryModel) = clickListener(categoryModel)

}