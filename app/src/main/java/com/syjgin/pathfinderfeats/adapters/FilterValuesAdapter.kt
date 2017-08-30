package com.syjgin.pathfinderfeats.adapters

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.activities.FilterValuesActivity
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.FilterRadioHolder
import com.syjgin.pathfinderfeats.model.Feat
import com.syjgin.pathfinderfeats.model.Models
import io.requery.android.QueryRecyclerAdapter
import io.requery.kotlin.asc
import io.requery.kotlin.select
import io.requery.query.Result
import io.requery.query.Tuple

/**
 * Created by user1 on 29.08.17.
 */
class FilterValuesAdapter(private var sourceMode: Boolean, private val activity: Activity) : RecyclerView.Adapter<FilterRadioHolder>() {
    val data = mutableListOf<String>()

    init {
        val result = if(sourceMode) MainApp.instance?.dataStore?.select(Feat::source)?.distinct()?.orderBy(Feat::source.asc())?.get() else
            MainApp.instance?.dataStore?.select(Feat::race_name)?.distinct()?.orderBy(Feat::race_name.asc())?.get() as Result<Tuple>
        result?.each {
            val value : String? = it.get(if(sourceMode) "source" else "race_name")
            if(value != null)
                data.add(value)
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FilterRadioHolder?, position: Int) {
        FilterRadioHolder.bind(holder, data.get(position), this)
    }

    override fun getItemCount(): Int = data.size

    private var selected : String = ""

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FilterRadioHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.item_filter_value, parent, false)
        val holder = FilterRadioHolder(view)
        holder.radioButton = view.findViewById(R.id.radioButton) as AppCompatRadioButton
        return holder
    }

    public fun isSelected(value : String) : Boolean = value == selected

    public fun updateSelected(newValue : String) {
        selected = newValue
        notifyDataSetChanged()
        val intent = Intent()
        intent.putExtra(FilterValuesActivity.SELECTED, newValue)
        intent.putExtra(FilterValuesActivity.SOURCE_MODE, sourceMode)
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }
}