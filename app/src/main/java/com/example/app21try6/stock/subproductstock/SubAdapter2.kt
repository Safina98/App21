package com.example.app21try6.stock.subproductstock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.SubProduct
import com.example.app21try6.databinding.VendibleItemListBinding

class SubAdapter2 (val code:Int,val checkBoxListener2: CheckBoxListener2):  ListAdapter<SubProduct,
        SubAdapter2.MyViewHolder>(SubDiffCallback()){
    class MyViewHolder private constructor( val binding: VendibleItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(code:Int,checkBoxListener2: CheckBoxListener2,
                 item: SubProduct){
            binding.subProduct = item
            binding.checkBox.visibility=View.GONE
            binding.textSubproductV.text = item.sub_name
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VendibleItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubAdapter2.MyViewHolder {
       return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(code,checkBoxListener2,getItem(position))
    }


}
class CheckBoxListener2(val checkBoxListener:(view: View, subProduct: SubProduct)->Unit){
    fun onCheckBoxClick(view: View, subProduct: SubProduct)= checkBoxListener(view,subProduct)
}

