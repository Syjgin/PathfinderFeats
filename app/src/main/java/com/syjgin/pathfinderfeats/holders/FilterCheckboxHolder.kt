package com.syjgin.pathfinderfeats.holders

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.View
import com.syjgin.pathfinderfeats.adapters.FiltersListAdapter

/**
 * Created by user1 on 27.08.17.
 */
class FilterCheckboxHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var checkbox : AppCompatCheckBox? = null
    companion object {
        fun bind(holder: FilterCheckboxHolder, text : String, adapter : FiltersListAdapter) {
            holder.checkbox?.text = text
            holder.checkbox?.setOnCheckedChangeListener(null)
            holder.checkbox?.isChecked = adapter.isElementChecked(text)
            holder.checkbox?.setOnCheckedChangeListener({ button, checked ->
                adapter.modifyCheckedList(text, checked)
            })
        }
    }
}