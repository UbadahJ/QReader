package com.ubadahj.qidianundergroud.ui.adapters

import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class FilterableListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback), Filterable {

    /**
     * Set the predicate by which the list will be filtered
     * */
    var filterPredicate: ((list: List<T>, constraint: String) -> List<T>)? = null

    private var originalList: List<T> = currentList.toList()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val shouldFilter = !(constraint.isNullOrEmpty() || filterPredicate == null)
                return FilterResults().apply {
                    values = if (shouldFilter)
                        filterPredicate!!.invoke(originalList, constraint.toString())
                    else
                        originalList
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as? List<T>, true)
            }
        }
    }

    override fun submitList(list: List<T>?) {
        submitList(list, false)
    }

    /**
     * This function is responsible for maintaining the
     * actual contents for the list for filtering
     * The submitList for parent class delegates false
     * so that a new contents can be set
     * While a filter pass true which make sure original list
     * is maintained
     *
     * @param filtered True if the list was updated using filter interface
     * */
    private fun submitList(list: List<T>?, filtered: Boolean) {
        if (!filtered)
            originalList = list ?: listOf()

        super.submitList(list)
    }

}