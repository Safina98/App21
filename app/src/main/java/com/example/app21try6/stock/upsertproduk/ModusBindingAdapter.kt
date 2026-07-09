package com.example.app21try6.stock.upsertproduk

import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.app21try6.R
import com.example.app21try6.database.models.ProductModusModel

object ModusBindingAdapter {

    @JvmStatic
    @BindingAdapter("modusList")
    fun setModusList(container: LinearLayout, list: List<ProductModusModel>?) {
        container.removeAllViews()
        if (list.isNullOrEmpty()) {
            val emptyView = TextView(container.context).apply {
                text = "Belum ada data"
                setTextAppearance(R.style.modus_text_style)
            }
            container.addView(emptyView)
            return
        }

        list.forEach { modus ->
            val row = TextView(container.context).apply {
                text = "${modus.qty} m terjual ${modus.count}x "
                setTextAppearance(R.style.modus_text_style)
                setPadding(0, 2, 0, 8)
            }
            container.addView(row)
        }
    }
}