package com.example.app21try6.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import java.text.Normalizer
import java.util.Locale

class SpaceInsensitiveArrayAdapter(
    context: Context,
    resource: Int,
    items: List<String>
) : ArrayAdapter<String>(context, resource, ArrayList(items)) {

    // keep a copy of original items for filtering
    private val allItems: List<String> = ArrayList(items)

    // normalize: strip diacritics, remove non-alphanumeric chars (including spaces), lowercase
    private fun normalize(s: String?): String {
        if (s.isNullOrEmpty()) return ""
        val decomposed = Normalizer.normalize(s, Normalizer.Form.NFD)
        // remove diacritic marks
        val noDiacritics = decomposed.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        // keep only letters and digits (remove spaces, punctuation, many whitespace variants)
        return noDiacritics.replace("[^\\p{Alnum}]".toRegex(), "").lowercase(Locale.ROOT)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint.isNullOrBlank()) {
                    results.values = allItems
                    results.count = allItems.size
                } else {
                    val pattern = normalize(constraint.toString())
                    val filtered = allItems.filter { normalize(it).contains(pattern) }
                    results.values = filtered
                    results.count = filtered.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results?.values is List<*>) {
                    @Suppress("UNCHECKED_CAST")
                    addAll(results.values as List<String>)
                }
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return resultValue?.toString() ?: ""
            }
        }
    }
}
