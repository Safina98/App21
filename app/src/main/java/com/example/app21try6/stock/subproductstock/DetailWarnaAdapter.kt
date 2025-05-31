package com.example.app21try6.stock.subproductstock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.R
import com.example.app21try6.SIMPLE_DATE_FORMATTER
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.databinding.ItemListSubDetailBinding

class DetailWarnaAdapter (
                  val longListener: DetailWarnaLongListener,
    val deleteDetailWarnaListener: DeleteDetailWarnaListener,
    val editDetailWarnaListener: EditDetailWarnaListener,
    val trackDetailWarnaListener: TrackDetailWarnaListener
) :
    ListAdapter<DetailMerchandiseModel,
           DetailWarnaAdapter.MyViewHolder>(DetailWarnaDiffCallback()){
    class MyViewHolder private constructor( val binding: ItemListSubDetailBinding

    ): RecyclerView.ViewHolder(binding.root){
        fun bind(
                 item: DetailMerchandiseModel,
                 deleteDetailWarnaListener: DeleteDetailWarnaListener,
                 editDetailWarnaListener: EditDetailWarnaListener,
                 trackDetailWarnaListener: TrackDetailWarnaListener
        ){
            binding.item = item
            binding.deleteListener=deleteDetailWarnaListener
            binding.editListener= editDetailWarnaListener
            binding.trackListener=trackDetailWarnaListener
            if(item.batchCount==null){
                binding.btnTrack.background = ContextCompat.getDrawable(itemView.context, R.drawable.baseline_subtract_24)
                binding.btnEdit.background = ContextCompat.getDrawable(itemView.context, R.drawable.baseline_add_24)
                binding.txtNet.text= SIMPLE_DATE_FORMATTER.format(item.date)
                binding.txtBatchCount.text=item.net.toString()  +"m"
            }
            else{
                binding.txtNet.text=item.net.toString() +"Meter"
                binding.txtBatchCount.text=item.batchCount.toString() +"Roll"
            }
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListSubDetailBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),deleteDetailWarnaListener,editDetailWarnaListener,trackDetailWarnaListener)
    }
}

class DetailWarnaDiffCallback : DiffUtil.ItemCallback<DetailMerchandiseModel>() {
    override fun areItemsTheSame(oldItem: DetailMerchandiseModel, newItem: DetailMerchandiseModel): Boolean {
        return oldItem.id == newItem.id
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DetailMerchandiseModel, newItem: DetailMerchandiseModel): Boolean {
        return oldItem == newItem
    }
}
class DetailWarnaLongListener(val longListener:(DetailMerchandiseModel)->Unit){
    fun onLongClick(v: View, detailWarnaTable: DetailMerchandiseModel):Boolean{
        longListener(detailWarnaTable)
        return true
    }

}
class DeleteDetailWarnaListener(val clickListener: (detailwarnaTable1: DetailMerchandiseModel) -> Unit) {
    fun onClick(detailwarnaTable: DetailMerchandiseModel) = clickListener(detailwarnaTable)
}
class EditDetailWarnaListener(val clickListener: (detailwarnaTable1: DetailMerchandiseModel) -> Unit) {
    fun onClick(detailwarnaTable: DetailMerchandiseModel) = clickListener(detailwarnaTable)
}
class TrackDetailWarnaListener(val clickListener: (detailwarnaTable1: DetailMerchandiseModel) -> Unit) {
    fun onClick(detailwarnaTable: DetailMerchandiseModel) = clickListener(detailwarnaTable)
}