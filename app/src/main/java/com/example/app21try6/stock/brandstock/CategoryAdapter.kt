package com.example.app21try6.stock.brandstock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.Category
import com.example.app21try6.databinding.CategoryItemListBinding


class CategoryAdapter (val checkBoxListener: CheckBoxListenerDoalog):ListAdapter<Category,CategoryAdapter.MyViewHolder>(BookDiffCallback()) {
    class MyViewHolder private constructor(val binding: CategoryItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item:Category,
                 checkBoxListener: CheckBoxListenerDoalog
        ){
            binding.category = item
            binding.textSubproductV.text =item.category_name
            binding.checkBox.isChecked = item.checkBoxBoolean
            binding.checkBoxListener = checkBoxListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =CategoryItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),
                checkBoxListener)
    }

}
class BookDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.category_name== newItem.category_name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}
class CheckBoxListenerDoalog(val checkBoxListener:(view: View,category:Category)->Unit){
    fun onCheckBoxClick(view:View, category:Category)= checkBoxListener(view,category)
}