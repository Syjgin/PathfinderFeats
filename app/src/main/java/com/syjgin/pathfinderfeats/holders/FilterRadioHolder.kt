package com.syjgin.pathfinderfeats.holders

import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.syjgin.pathfinderfeats.adapters.FilterValuesAdapter

/**
 * Created by user1 on 27.08.17.
 */
class FilterRadioHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var radioButton : AppCompatRadioButton? = null
    companion object {
        fun bind(holder: FilterRadioHolder?, text : String, adapter : FilterValuesAdapter) {
            holder?.radioButton?.text = text
            holder?.radioButton?.setOnCheckedChangeListener(null)
            holder?.radioButton?.isChecked = adapter.isSelected(text)
            holder?.radioButton?.setOnCheckedChangeListener({button, checked ->
                adapter.updateSelected(text)
            })
        }
    }
}