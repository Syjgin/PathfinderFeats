package com.syjgin.pathfinderfeats.adapters

import android.preference.PreferenceActivity
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.activities.FilterValuesActivity
import com.syjgin.pathfinderfeats.activities.FiltersActivity
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.*

/**
 * Created by user1 on 27.08.17.
 */
class FiltersListAdapter(private val activity : FiltersActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val checkedList = mutableListOf<String>()
    var selectedSource : String = ""
    var selectedRace : String = ""

    enum class CellType(val num : Int) {
        Child(0),
        Checkbox(1),
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(parent?.context)
        when(viewType) {
            CellType.Child.num -> {
                val view = inflater.inflate(R.layout.item_filter_child, parent, false)
                val holder = FilterChildHolder(view)
                holder.main = view.findViewById(R.id.main) as LinearLayout
                holder.textView = view.findViewById(R.id.categoryText) as TextView
                return holder
            }
            CellType.Checkbox.num -> {
                val view = inflater.inflate(R.layout.item_filter_checkbox, parent, false)
                val holder = FilterCheckboxHolder(view)
                holder.checkbox = view.findViewById(R.id.checkbox) as AppCompatCheckBox
                return holder
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        var childElement = ""
        var checkboxElement = ""
        val mode = modeByPosition(position)
        if(position < FilterValuesActivity.ValueMode.values().size) {
            val condition = when(mode) {
                FilterValuesActivity.ValueMode.SOURCE -> selectedSource.isEmpty()
                FilterValuesActivity.ValueMode.RACE -> selectedRace.isEmpty()
                else -> {
                    true
                }
            }
            if(condition)
                childElement = MainApp.instance?.applicationContext?.resources?.getStringArray(R.array.filter_string_values)?.get(position).toString()
            else {
                val baseString = when(mode) {
                    FilterValuesActivity.ValueMode.SOURCE -> MainApp.instance?.applicationContext?.resources?.getString(R.string.source_title)
                    FilterValuesActivity.ValueMode.RACE -> MainApp.instance?.applicationContext?.resources?.getString(R.string.race_title)
                    else -> {
                        ""
                    }
                }
                val selectedString = when(mode) {
                    FilterValuesActivity.ValueMode.SOURCE -> selectedSource
                    FilterValuesActivity.ValueMode.RACE -> selectedRace
                    else -> {
                        ""
                    }
                }
                childElement = String.format(baseString as String, selectedString)
            }
        } else {
            checkboxElement = MainApp.instance?.applicationContext?.resources?.getStringArray(R.array.filter_boolean_values)?.get(position - FilterValuesActivity.ValueMode.values().size).toString()
        }
        if(holder is FilterCheckboxHolder) {
            FilterCheckboxHolder.bind(holder, checkboxElement, this)
        }
        if(holder is FilterChildHolder) {
            FilterChildHolder.bind(holder, childElement, activity, mode)
        }
    }

    private fun modeByPosition(position: Int) : FilterValuesActivity.ValueMode? = when(position) {
        0 -> FilterValuesActivity.ValueMode.SOURCE
        1 -> FilterValuesActivity.ValueMode.RACE
        else -> {
            null
        }
    }

    override fun getItemCount(): Int = 20

    override fun getItemViewType(position: Int): Int {
        if(position < FilterValuesActivity.ValueMode.values().size)
            return CellType.Child.num
        return CellType.Checkbox.num
    }

    public fun modifyCheckedList(id : String, add : Boolean) {
        if(add) {
            if(!isElementChecked(id)) {
                checkedList.add(id)
            } else {
                if(isElementChecked(id)) {
                    checkedList.remove(id)
                }
            }
        }
    }

    public fun isElementChecked(checkboxElement: String) = checkedList.contains(checkboxElement)
    fun updateValue(mode: FilterValuesActivity.ValueMode?, selected: String?) {
        if(mode != null && selected != null) {
            when(mode) {
                FilterValuesActivity.ValueMode.SOURCE -> selectedSource = selected
                FilterValuesActivity.ValueMode.RACE -> selectedRace = selected
            }
            notifyDataSetChanged()
        }
    }

    fun clear() {
        selectedSource = ""
        selectedRace = ""
        checkedList.clear()
        notifyDataSetChanged()
    }
}