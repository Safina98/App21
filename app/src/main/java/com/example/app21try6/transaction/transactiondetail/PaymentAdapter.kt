package com.example.app21try6.transaction.transactiondetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.databinding.ItemListTransactionPaymentBinding
import com.example.app21try6.formatRupiah

class PaymentAdapter(
    var isPaidoff:Boolean?,
    val clickListener: TransPaymentClickListener,
    val longListener:TransPaymentLongListener,
    val datePaymentClickListener: TransDatePaymentClickListener):
    ListAdapter<PaymentModel, PaymentAdapter.MyViewHolder>(TransPaymentDiffCallBack())
{
    private var is_active = MutableLiveData<Boolean>(false)
    class MyViewHolder private constructor(val binding: ItemListTransactionPaymentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: PaymentModel,
        clickListener: TransPaymentClickListener,
        longListener: TransPaymentLongListener,
        isPaidOff:Boolean?,
        datePaymentClickListener: TransDatePaymentClickListener
    ) {
        binding.item = item
        binding.clickListener = clickListener
        binding.longClickListener = longListener
        binding.dateClickListener = datePaymentClickListener
        Log.i("DiscProbs","${formatRupiah(item.payment_ammount?.toDouble())}")
        binding.txtBayarItemList.text = formatRupiah(item.payment_ammount?.toDouble())
        binding.executePendingBindings()
    }
        companion object {
            fun from(parent: ViewGroup): PaymentAdapter.MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemListTransactionPaymentBinding.inflate(layoutInflater, parent, false)
                return PaymentAdapter.MyViewHolder(binding)
            }
        }

}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, longListener,is_active.value!!,datePaymentClickListener)
        // Update background color based on clickedItems
    }

    fun deActivate() {
        this.is_active.value  = false
    }

    fun isActive(is_active:Boolean){
        this.is_active.value  = is_active
        //notifyDataSetChanged()
    }
    }


class TransPaymentDiffCallBack: DiffUtil.ItemCallback<PaymentModel>(){
    override fun areItemsTheSame(oldItem: PaymentModel, newItem: PaymentModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PaymentModel, newItem: PaymentModel): Boolean {
        return oldItem == newItem
    }
}
class TransPaymentClickListener(val clickListener:(paymentModel:PaymentModel)->Unit){
    fun onClick(paymentModel:PaymentModel)=clickListener(paymentModel)
}

class TransDatePaymentClickListener(val clickListener:(paymentModel:PaymentModel)->Unit){
    fun onClick(paymentModel:PaymentModel)=clickListener(paymentModel)
}
class TransPaymentLongListener(val longListener:(paymentModel:PaymentModel)->Unit){
    fun onLongClick(v: View, paymentModel:PaymentModel):Boolean{
        longListener(paymentModel)
        return true
    }
}