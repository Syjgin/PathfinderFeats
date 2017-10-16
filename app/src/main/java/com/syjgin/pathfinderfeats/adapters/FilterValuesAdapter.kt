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
import io.requery.kotlin.asc
import io.requery.kotlin.select

/**
 * Created by user1 on 29.08.17.
 */
class FilterValuesAdapter(private var sourceMode: FilterValuesActivity.ValueMode, private val activity: Activity) : RecyclerView.Adapter<FilterRadioHolder>() {
    val data = mutableListOf<String>()

    init {
        val result = when(sourceMode) {
            FilterValuesActivity.ValueMode.SOURCE -> MainApp.instance?.dataStore?.select(Feat::source)?.distinct()?.orderBy(Feat::source.asc())?.get()
            FilterValuesActivity.ValueMode.RACE -> MainApp.instance?.dataStore?.select(Feat::race_name)?.distinct()?.orderBy(Feat::race_name.asc())?.get()
            FilterValuesActivity.ValueMode.SKILLS -> MainApp.instance?.dataStore?.select(Feat::prerequisite_skills)?.distinct()?.get()
            FilterValuesActivity.ValueMode.TYPE -> MainApp.instance?.dataStore?.select(Feat::type)?.distinct()?.get()
        }
        val key = when(sourceMode) {
            FilterValuesActivity.ValueMode.SOURCE -> "source"
            FilterValuesActivity.ValueMode.RACE -> "race_name"
            FilterValuesActivity.ValueMode.SKILLS -> "prerequisite_skills"
            FilterValuesActivity.ValueMode.TYPE -> "type"
        }
        result?.each {
            val value : String? = it.get(key)
            if(value != null) {
                if(sourceMode == FilterValuesActivity.ValueMode.SOURCE) {
                    if(!data.contains(value))
                        data.add(value)
                } else {
                    val splitted = value.toLowerCase().split(", ", "|", " | ")
                    splitted.forEach {
                        var filtered = it.replace(Regex("\\(.*\\)"), "").trim()
                        filtered = filtered.replace(Regex("([0-9@])"), "").trim()
                        filtered = filtered.replace("ranks", "").trim()
                        filtered = filtered.replace("rank", "").trim()
                        if(!data.contains(filtered))
                            data.add(filtered)
                    }
                }
            }
        }
        data.sort()
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
        holder.radioButton = view.findViewById<AppCompatRadioButton>(R.id.radioButton)
        return holder
    }

    public fun isSelected(value : String) : Boolean = value == selected

    public fun updateSelected(newValue : String) {
        selected = newValue
        notifyDataSetChanged()
        val intent = Intent()
        intent.putExtra(FilterValuesActivity.SELECTED, newValue)
        intent.putExtra(FilterValuesActivity.VALUE_MODE, sourceMode)
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }
}