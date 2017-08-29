package com.syjgin.pathfinderfeats.holders

import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by user1 on 27.08.17.
 */
class FilterChildHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var main : LinearLayout? = null
    var textView : TextView? = null
    companion object {
        fun bind(holder: FilterChildHolder, text: String) {
            holder.textView?.text = text
        }
    }
}