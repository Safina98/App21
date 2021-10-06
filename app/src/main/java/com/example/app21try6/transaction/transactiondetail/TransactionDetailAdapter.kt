package com.example.app21try6.transaction.transactiondetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.databinding.TransactionDetailItemListBinding
import com.example.app21try6.formatRupiah
import com.example.app21try6.transaction.transactionedit.TransactionEditDummyModel

class TransactionDetailAdapter(val clickListener: TransDetailClickListener):ListAdapter<TransactionDetail,TransactionDetailAdapter.MyViewHolder>(TransDetailDiffCallBack()) {
    class MyViewHolder private constructor(val binding:TransactionDetailItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:TransactionDetail,clickListener: TransDetailClickListener){
            binding.item  = item
            binding.txtItemTDetail.text = item.trans_item_name
            binding.txtQtyTDetail.text = item.qty.toString()
            binding.txtPriceTDetail.text = formatRupiah(item.trans_price.toDouble()).toString()
            binding.txtTotalTDetail.text = formatRupiah(item.total_price).toString()
        }
        companion object{
            fun from(parent:ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransactionDetailItemListBinding.inflate(layoutInflater,parent,false)
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
class TransDetailDiffCallBack:DiffUtil.ItemCallback<TransactionDetail>(){
    override fun areItemsTheSame(oldItem: TransactionDetail, newItem: TransactionDetail): Boolean {
        return oldItem.sum_id == newItem.sum_id
    }

    override fun areContentsTheSame(oldItem: TransactionDetail, newItem: TransactionDetail): Boolean {
        return oldItem == newItem
    }
}
class TransDetailClickListener(val clickListener:(detail_trans:TransactionEditDummyModel)->Unit){
    fun onClick(detail_trans: TransactionEditDummyModel)=clickListener(detail_trans)
}