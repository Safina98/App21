package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView


class SimilarWordAdapter(
    context: Context,
    private var originalList: List<String> = listOf()
) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, ArrayList()) {
    private val suggestions = ArrayList<String>()
    private val lev=Levenshtein()
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                suggestions.clear()

                if (constraint.isNullOrEmpty()) {
                    suggestions.addAll(originalList)
                } else {
                    val filteredList = lev.filterSuggestions(constraint.toString(), originalList)
                    suggestions.addAll(filteredList)
                }

                results.values = suggestions
                results.count = suggestions.size
                Log.d("Filter", "Filtering with: $constraint")
                Log.d("Filter", "Found ${suggestions.size} suggestions")
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results?.values is List<*>) {
                    addAll(results.values as List<String>)
                }
                notifyDataSetChanged()
            }
        }
    }



    override fun getItem(position: Int): String? {
        return super.getItem(position)
    }
    fun updateData(newList: List<String>) {
        originalList = newList
        notifyDataSetChanged()
        Log.d("Filter","updateData called")
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)
        (view as TextView).text = item
        return view
    }


}
