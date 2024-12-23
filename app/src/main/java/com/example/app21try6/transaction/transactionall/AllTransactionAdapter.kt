package com.example.app21try6.transaction.transactionall

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.R
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.databinding.ItemListTransactionAllBinding
import com.example.app21try6.formatRupiah

class AllTransactionAdapter(val clickListener:AllTransClickListener,
                            val checkBoxListener: CheckBoxListenerTransAll

)
    :ListAdapter<TransactionSummary,AllTransactionAdapter.MyViewHolder>(AllTransDiffCallBack()) {
    private var is_active = MutableLiveData<Boolean>(false)
    private var unfilteredList = listOf<TransactionSummary>()

    class MyViewHolder private constructor(val binding: ItemListTransactionAllBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: TransactionSummary,
            clickListener: AllTransClickListener,
            checkBoxListener: CheckBoxListenerTransAll,
            bool:Boolean
        ){
            binding.checkboxTransActive.visibility = when(bool){
                true ->View.VISIBLE
                else -> View.GONE
            }
            binding.item = item
            binding.txtNamaPe.text = item.cust_name
            val cardView = binding.cardViewAllTrans
            if (item.is_keeped) { // Example condition
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.transActiveBgColor))
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.logrvbg))
            }
            //binding.txtTglTrans.text = item.trans_date
            binding.txtTotalTrans.text = formatRupiah(item.total_trans.toDouble()).toString()
            binding.clickListener = clickListener
            binding.checkboxListener = checkBoxListener
            val formattedDate = DETAILED_DATE_FORMATTER.format(item.trans_date)
            binding.txtTglTrans.text = formattedDate
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent:ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTransactionAllBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    fun deActivate() {
        val list = mutableListOf<TransactionSummary>()
        val l = unfilteredList.toMutableList()
        this.is_active.value  = false
        l.filter { it.is_paid_off.equals(true)}.forEach { it.is_paid_off=false }
        submitList(l)
        notifyDataSetChanged()
    }

    fun isActive(is_active:Boolean){
        this.is_active.value  = is_active
        //  notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener,
            checkBoxListener,
            is_active.value!!
        )
    }
}
class AllTransDiffCallBack: DiffUtil.ItemCallback<TransactionSummary>(){
    override fun areItemsTheSame(oldItem: TransactionSummary, newItem: TransactionSummary): Boolean {
        return oldItem.cust_name ==newItem.cust_name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: TransactionSummary, newItem: TransactionSummary): Boolean {
        return oldItem ==newItem
    }
}
class AllTransClickListener(val clickListener:(active_trans: TransactionSummary)->Unit){
    fun onClick(active_trans: TransactionSummary) = clickListener(active_trans)
}


class CheckBoxListenerTransAll(val checkBoxListener:(view: View, stok: TransactionSummary)->Unit){
    fun onCheckBoxClick(view: View, stok: TransactionSummary)= checkBoxListener(view,stok)
}
