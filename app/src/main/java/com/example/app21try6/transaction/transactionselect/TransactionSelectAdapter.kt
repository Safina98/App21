package com.example.app21try6.transaction.transactionselect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.bookkeeping.vendiblelist.*
import com.example.app21try6.bookkeeping.vendiblelist.CheckBoxListener
import com.example.app21try6.bookkeeping.vendiblelist.DelLongListenerV
import com.example.app21try6.bookkeeping.vendiblelist.TextListener
import com.example.app21try6.database.Product
import com.example.app21try6.database.SubProduct
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.databinding.TransactionSelectItemListBinding
import com.example.app21try6.databinding.VendibleItemListBinding
class TransactionSelectAdapter (
   val plusSelectListener: PlusSelectListener,
   val subsSelectListener: SubsSelectListener,
   val checkBoxListener: CheckBoxSelectListener,
   val plusSelectLongListener: PlusSelectLongListener,
   val subSelectLongListener: SubsSelectLongListener
): ListAdapter<TransSelectModel,
        TransactionSelectAdapter.MyViewHolder>(SelectDiffCallback()){
    class MyViewHolder private constructor(val binding: TransactionSelectItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(
                 item:TransSelectModel,
                 plusSelectListener: PlusSelectListener,
                 subsSelectListener: SubsSelectListener,
                 checkBoxListener: CheckBoxSelectListener,
                 plusSelectLongListener: PlusSelectLongListener,
                 subSelectLongListener: SubsSelectLongListener
        ){
            binding.item = item
            binding.plusClickListener = plusSelectListener
            binding.subsClickListener = subsSelectListener
            binding.checkboxListener =checkBoxListener
            binding.plusLongListener = plusSelectLongListener
            binding.subsLongListener = subSelectLongListener
            binding.txtProductT.text = item.item_name
            binding.textSellsT.text = item.qty.toString()
            binding.checkBox3.isChecked = item.is_selected or if (item.qty>0){true}else { false }
            // Set OnClickListener for the checkbox

        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =TransactionSelectItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            plusSelectListener,
            subsSelectListener,
            checkBoxListener,
            plusSelectLongListener,
            subSelectLongListener
          )
    }
}

class SelectDiffCallback : DiffUtil.ItemCallback<TransSelectModel>() {
    override fun areItemsTheSame(oldItem: TransSelectModel, newItem:TransSelectModel): Boolean {
        return oldItem.qty == newItem.qty
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: TransSelectModel, newItem: TransSelectModel): Boolean {
        return oldItem == newItem
    }
}
class SubStokLongListener(val longListener:(SubProduct)->Unit){
    fun onLongClick(v: View, subProduct: SubProduct):Boolean{
        longListener(subProduct)
        return true
    }
}

class CheckBoxSelectListener(val checkBoxListener:(view: View, transSelectModel: TransSelectModel)->Unit){
    fun onCheckBoxClick(view: View, transSelectModel: TransSelectModel)= checkBoxListener(view,transSelectModel)
}
class TextListener(val textListener:(view: View, product: Product)->Unit){
    fun onTextClick(view: View, product: Product)= textListener(view,product)
}
class DelLongListenerV(val delLongListenerV:(product: Product)->Unit){
    fun onLongClick(v: View, product: Product):Boolean{
        delLongListenerV(product)
        return true
    }
}
class PlusSelectListener(val plusListener:(selectModel:TransSelectModel)->Unit){
    fun onPlusButtonClick(selectModel:TransSelectModel)= plusListener(selectModel)
}
class SubsSelectListener(val subsListener:(selectModel:TransSelectModel)->Unit){
    fun onSubsButtonClick(selectModel:TransSelectModel)= subsListener(selectModel)
}
class PlusSelectLongListener(val longListener:(edit_trans: TransSelectModel)->Unit){
    fun onLongClick(v: View, edit_trans: TransSelectModel):Boolean{
        longListener(edit_trans)
        return true
    }
}
class SubsSelectLongListener(val longListener:(edit_trans: TransSelectModel)->Unit){
    fun onLongClick(v: View, edit_trans: TransSelectModel):Boolean{
        longListener(edit_trans)
        return true
    }
}
