package com.example.app21try6.transaction.transactionactive

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.databinding.TransactionActiveItemListBinding
import com.example.app21try6.formatRupiah

class TransactionActiveAdapter(val clickListener:ActiveClickListener)
    :ListAdapter<TransactionSummary,TransactionActiveAdapter.MyViewHolder>(TransActiveDiffCallBack()) {
    class MyViewHolder private constructor(val binding: TransactionActiveItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(
            item:TransactionSummary,
        clickListener: ActiveClickListener
        ){
            binding.item = item
            binding.txtNamaPe.text = item.cust_name
            binding.txtTglTrans.text = item.trans_date
            binding.txtTotalTrans.text = formatRupiah(item.total_trans.toDouble()).toString()
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent:ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransactionActiveItemListBinding.inflate(layoutInflater,parent,false)
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
class TransActiveDiffCallBack: DiffUtil.ItemCallback<TransactionSummary>(){
    override fun areItemsTheSame(oldItem: TransactionSummary, newItem: TransactionSummary): Boolean {
        return oldItem.cust_name ==newItem.cust_name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: TransactionSummary, newItem: TransactionSummary): Boolean {
        return oldItem ==newItem
    }
}
class ActiveClickListener(val clickListener:(active_trans:TransactionSummary)->Unit){
    fun onClick(active_trans: TransactionSummary) = clickListener(active_trans)
}

