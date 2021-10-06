package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.SubProduct
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.databinding.TransactionEditItemListBinding
import com.example.app21try6.formatRupiah

class TransactionEditAdapter(
        val clickListener: TransEditClickListener,
        val subsTransClickListener: SubsTransClickListener,
        val plusTransClickListener: PlusTransClickListener,
        val subslongListener: SubsTransLongListener,
        val plusLongListener: PlusTransLongListener)
    :ListAdapter<TransactionDetail,TransactionEditAdapter.MyViewHolder>(TransEditDiffCallBack()){
    class MyViewHolder private constructor(val binding:TransactionEditItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(
                item:TransactionDetail,
                clickListener: TransEditClickListener,
                subsTransClickListener: SubsTransClickListener,
                plusTransClickListener: PlusTransClickListener,
                subslongListener: SubsTransLongListener,
                plusLongListener: PlusTransLongListener){
            binding.item = item
            binding.txtProductT.text = item.trans_item_name
            binding.textSellsT.text = item.qty.toString()
            binding.txtPriceT.text = formatRupiah(item.trans_price.toDouble()).toString()
            binding.textSumsT.text = formatRupiah(item.total_price).toString()
            binding.clickListener = clickListener
            binding.subsListener = subsTransClickListener
            binding.plusListener = plusTransClickListener
            binding.subsLongListener = subslongListener
            binding.plusLongListener = plusLongListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent:ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransactionEditItemListBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.bind(getItem(position),clickListener,subsTransClickListener,plusTransClickListener,subslongListener,plusLongListener)
    }
}
class TransEditDiffCallBack: DiffUtil.ItemCallback<TransactionDetail>(){
    override fun areItemsTheSame(oldItem: TransactionDetail, newItem: TransactionDetail): Boolean {
        return oldItem.trans_price == newItem.trans_price
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: TransactionDetail, newItem: TransactionDetail): Boolean {
        return oldItem ==newItem
    }
}
class TransEditClickListener(val clickListener:(edit_trans: TransactionDetail)->Unit){
    fun onClick(edit_trans: TransactionDetail) = clickListener(edit_trans)

}
//class PlusTransClickListener(val clickListener:(edit_trans: TransactionDetail)->Unit){
  //  fun onClick(edit_trans: TransactionDetail) = clickListener(edit_trans)
//}
class SubsTransClickListener(val clickListener:(edit_trans: TransactionDetail)->Unit){
    fun onClick(edit_trans: TransactionDetail) = clickListener(edit_trans)
}
class PlusTransClickListener(val plusListener:(edit_trans:TransactionDetail)->Unit){
    fun onPlusButtonClick(edit_trans: TransactionDetail)= plusListener(edit_trans)
}
class PlusTransLongListener(val longListener:(edit_trans: TransactionDetail)->Unit){
    fun onLongClick(v: View, edit_trans: TransactionDetail):Boolean{
        longListener(edit_trans)
        return true
    }
}
class SubsTransLongListener(val longListener:(edit_trans: TransactionDetail)->Unit){
    fun onLongClick(v: View, edit_trans: TransactionDetail):Boolean{
        longListener(edit_trans)
        return true
    }
}