package com.syjgin.pathfinderfeats.holders

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.syjgin.pathfinderfeats.activities.FilterValuesActivity
import com.syjgin.pathfinderfeats.activities.FiltersActivity

/**
 * Created by user1 on 27.08.17.
 */
class FilterChildHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    var main : LinearLayout? = null
    var textView : TextView? = null
    companion object {
        fun bind(holder: FilterChildHolder, text: String, activity: FiltersActivity, valueMode: FilterValuesActivity.ValueMode?) {
            holder.textView?.text = Html.fromHtml(text)
            holder.main?.setOnClickListener(null)
            holder.main?.setOnClickListener({view ->
                val intent = Intent(activity, FilterValuesActivity::class.java)
                intent.putExtra(FilterValuesActivity.VALUE_MODE, valueMode)
                activity.startActivityForResult(intent, FilterValuesActivity.VALUES_REQUEST)
            })
        }
    }
}