package com.example.app21try6.transaction.transactiondetail

import android.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.databinding.TransactionDetailItemListBinding
import com.example.app21try6.formatRupiah



class TransactionDetailAdapter(
                               val clickListener: TransDetailClickListener,
                               val longListener:TransDetailLongListener):
    ListAdapter<TransactionDetail,TransactionDetailAdapter.MyViewHolder>(TransDetailDiffCallBack()) {

    private var is_active = MutableLiveData<Boolean>(false)


    class MyViewHolder private constructor(val binding: TransactionDetailItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: TransactionDetail,
            clickListener: TransDetailClickListener,
            longListener: TransDetailLongListener,
            isPaidOff:Boolean?
        ) {
            binding.item = item
            binding.longClickListener = longListener
            binding.txtItemTDetail.setBackgroundColor(Color.WHITE)
            binding.txtItemTDetail.text = item.trans_item_name
            binding.txtQtyTDetail.text = item.qty.toString()
            binding.txtUnitQtyDetail.text = item.unit_qty.toString()
            binding.txtUnitDetail.text = item.unit
            binding.txtItemTDate.visibility = when(isPaidOff){
                true ->View.VISIBLE
                else -> View.GONE
            }
            binding.txtPriceTDetail.text =
                if (item.qty >= 1) formatRupiah(item.trans_price.toDouble()).toString() else "-"
            binding.txtTotalTDetail.text = formatRupiah(item.total_price).toString()

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    TransactionDetailItemListBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, longListener, is_active.value!!)
        // Update background color based on clickedItems
    }
    fun deActivate() {
        this.is_active.value  = false
        notifyDataSetChanged()
    }

    fun isActive(is_active:Boolean){
        this.is_active.value  = is_active
        notifyDataSetChanged()
        //notifyDataSetChanged()
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

class TransDetailClickListener(val clickListener:(detail_trans:TransactionDetail)->Unit){
    fun onClick(detail_trans: TransactionDetail)=clickListener(detail_trans)
}




class TransDetailLongListener(val longListener:(trans_detail: TransactionDetail)->Unit){
    fun onLongClick(v: View, trans_detail:  TransactionDetail):Boolean{
        longListener(trans_detail)
        return true
    }
}