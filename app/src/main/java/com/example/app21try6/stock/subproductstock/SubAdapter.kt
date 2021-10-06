package com.example.app21try6.stock.subproductstock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.Product
import com.example.app21try6.database.SubProduct
import com.example.app21try6.databinding.SubProductItemListBinding

class SubAdapter (val code:Int,
                  val checkBoxListenerSub: CheckBoxListenerSub,
    val longListener: SubStokLongListener,
    val plusListener: PlusStokListener,
    val subsListener : SubsStokListener,
    val warnaListener: WarnaStokListener,
    val ketListener:KetStokListener,
    val subProductListener:SubListener
) :
    ListAdapter<SubProduct,
            SubAdapter.MyViewHolder>(SubDiffCallback()){
    class MyViewHolder private constructor( val binding: SubProductItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(code: Int,longListener :SubStokLongListener,
                 plusListener:PlusStokListener,
                 subsListener : SubsStokListener,
                 warnaListener: WarnaStokListener,
                 ketListener: KetStokListener,
                 subProductListener:SubListener,
                 checkBoxListenerSub: CheckBoxListenerSub,

                 item: SubProduct){
            binding.subProduct = item
            binding.subProductTxt.text = item.sub_name
            if (code==0){
                binding.stokTxt.text = item.roll_u.toString()
                binding.colorTxt.text = item.warna
                binding.ketTxt.text = item.ket
                binding.textStokToko.text = item.roll_bt.toString()+"B + "+item.roll_st+"S + "+item.roll_kt+"K"
                binding.textStokGudang.text = item.roll_bg.toString()+"BG + "+item.roll_sg+"SG + "+item.roll_kg+"KG"
                binding.checkBox.visibility=View.GONE
                binding.longListener = longListener
                binding.plusListener = plusListener
                binding.subssListener = subsListener
                binding.warnaListener = warnaListener
                binding.ketListener = ketListener
                binding.subProductListener = subProductListener
            }else
            {
                binding.textStokGudang.visibility = View.GONE
                binding.addNStockBtn.visibility = View.GONE
                binding.subsNStockBtn.visibility = View.GONE
                binding.stokTxt.visibility = View.GONE
                binding.textView2.visibility = View.GONE
                binding.textView3.visibility = View.GONE
                binding.textStokToko.visibility = View.GONE
                binding.ketTxt.visibility = View.GONE
                binding.subCheckBoxListener = checkBoxListenerSub

                binding.checkBox.isChecked = item.is_cheked

            }

            binding.executePendingBindings()

        }
        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SubProductItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(code,longListener,plusListener,subsListener, warnaListener,ketListener,subProductListener,
                checkBoxListenerSub,getItem(position))
    }


}

class SubDiffCallback : DiffUtil.ItemCallback<SubProduct>() {
    override fun areItemsTheSame(oldItem: SubProduct, newItem:SubProduct): Boolean {
        return oldItem.sub_name == newItem.sub_name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SubProduct, newItem: SubProduct): Boolean {
        return oldItem == newItem
    }
}
class SubStokLongListener(val longListener:(SubProduct)->Unit){
    fun onLongClick(v: View, subProduct: SubProduct):Boolean{
        longListener(subProduct)
        return true
    }
}

class PlusStokListener(val plusListener:(subProduct: SubProduct)->Unit){
    fun onPlusButtonClick(subProduct: SubProduct)= plusListener(subProduct)
}
class SubsStokListener(val subsListener:(subProduct: SubProduct)->Unit){
    fun onSubsButtonClick(subProduct: SubProduct)= subsListener(subProduct)
}
class KetStokListener(val ketListener:(subProduct: SubProduct)->Unit){
    fun onKetClick(subProduct: SubProduct)= ketListener(subProduct)
}
class WarnaStokListener(val warnaListener:(subProduct: SubProduct)->Unit){
    fun onWarnaClick(subProduct: SubProduct)= warnaListener(subProduct)
}
class SubListener(val subproductListener:(subProduct: SubProduct)->Unit){
    fun onSubProductClick(subProduct: SubProduct)= subproductListener(subProduct)
}
class CheckBoxListenerSub(val checkBoxListenerSub:(view:View, subProduct:SubProduct)->Unit){
    fun onCheckBoxSubClick(view:View, subProduct:SubProduct)= checkBoxListenerSub(view,subProduct)
}
