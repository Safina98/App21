package com.example.app21try6.transaction.transactionall

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.databinding.ItemListTransactionAllBinding
import com.example.app21try6.formatRupiah
import java.util.Locale

class AllTransactionAdapter(
    val clickListener:AllTransClickListener,
    val checkBoxListener: CheckBoxListenerTransAll,
    var selectedItemId:Long?
) :ListAdapter<TransactionSummary,AllTransactionAdapter.MyViewHolder>(AllTransDiffCallBack()) {
    private var is_active = MutableLiveData<Boolean>(false)
    private var unfilteredList = listOf<TransactionSummary>()
    var selectedPosition: Int = RecyclerView.NO_POSITION

    class MyViewHolder private constructor(val binding: ItemListTransactionAllBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: TransactionSummary,
            clickListener: AllTransClickListener,
            checkBoxListener: CheckBoxListenerTransAll,
            bool:Boolean,
            isIdSelected:Boolean,
        ){
            binding.checkboxTransActive.visibility = when(bool){
                true ->View.VISIBLE
                else -> View.GONE
            }
            binding.item = item
            binding.txtNamaPe.text = item.cust_name
            val cardView = binding.cardViewAllTrans

            val color = when {
                isIdSelected -> R.color.primaryColor
                item.is_keeped -> R.color.transActiveBgColor
                else -> R.color.logrvbg
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, color))
            binding.txtTotalTrans.text = formatRupiah(item.total_after_discount.toDouble()).toString()
            val formattedDate = Constants.DETAILED_DATE_FORMATTER.format(item.trans_date)
            binding.txtTglTrans.text = formattedDate

            binding.clickListener = clickListener
            binding.checkboxListener = checkBoxListener

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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        val isIdSelected = item.tSCloudId == selectedItemId
        holder.bind(
            getItem(position),
            clickListener,
            checkBoxListener,
            is_active.value!!,
            isIdSelected
        )

        // Handle click events to update the selected position
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition) // Update the old selected item
            notifyItemChanged(selectedPosition) // Update the new selected item
            clickListener.onClick(getItem(position)) // Trigger click listener
        }
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
