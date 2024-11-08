package com.example.app21try6.statement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.DATE_FORMAT

import com.example.app21try6.databinding.ItemListSDiscountBinding
import com.example.app21try6.formatRupiah

class DiscountAdapter(
    val discountListener:DiscountListener,
    val longListener: DiscountLongListener,
    val delListener: DiscountDelListener
): ListAdapter<DiscountAdapterModel, DiscountAdapter.MyViewHolder>(DiscountDiffCallback()){
    class MyViewHolder private constructor(val binding: ItemListSDiscountBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: DiscountAdapterModel, discountListener: DiscountListener, longListener: DiscountLongListener,delListener: DiscountDelListener){
            binding.item=item
            binding.delListener=delListener
            binding.clickListener= discountListener
            if (item.discountName==null){
                binding.txtDiscountName.text=item.expense_category_name
                binding.txtMinQty.text=item.expense_name
                binding.txtCustLocation.text= item.date?.let { DATE_FORMAT.format(it) }
                binding.txtDiscValue.text = formatRupiah(item.expense_ammount!!.toDouble())
                binding.lblCustLocation.text="Tanggal:"
                binding.lblMinQty.text="Pengeluaran: "
                binding.lblDiscType.visibility=View.GONE
                binding.lblDiscValue.text="Jumlah: "
            }else{
                binding.txtDiscountName.text=item.discountName
                binding.txtMinQty.text=item.minimumQty.toString()
                binding.txtCustLocation.text=item.custLocation.toString()
                binding.txtDiscType.text=item.discountType
                binding.txtDiscValue.text = formatRupiah(item.discountValue!!.toDouble())
                binding.lblCustLocation.text="Lokasi Pembeli: "
                binding.lblMinQty.text="Minimum Pembelian: "
                binding.lblDiscType.text="Tipe: "
                binding.lblDiscValue.text="Diskon: "
            }
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListSDiscountBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscountAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DiscountAdapter.MyViewHolder, position: Int) {
        holder.bind(getItem(position),discountListener,longListener,delListener)
    }

}
class DiscountDiffCallback: DiffUtil.ItemCallback<DiscountAdapterModel>(){
    override fun areItemsTheSame(oldItem: DiscountAdapterModel, newItem: DiscountAdapterModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DiscountAdapterModel, newItem: DiscountAdapterModel): Boolean {
        return oldItem == newItem
    }

}
class DiscountListener(val clickListener: (discountTable: DiscountAdapterModel) -> Unit) {
    fun onClick(discount: DiscountAdapterModel) = clickListener(discount)

}
class DiscountDelListener(val clickListener: (discountTable: DiscountAdapterModel) -> Unit) {
    fun onClick(discount: DiscountAdapterModel) = clickListener(discount)
}
class  DiscountLongListener(val longListener: (discount: DiscountAdapterModel) -> Unit){
    fun onLongClick(v: View, discount: DiscountAdapterModel): Boolean {

        longListener(discount)
        return true}
}