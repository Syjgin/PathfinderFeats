package com.syjgin.pathfinderfeats.adapters

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.activities.FilterValuesActivity
import com.syjgin.pathfinderfeats.activities.FiltersActivity
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.FilterCheckboxHolder
import com.syjgin.pathfinderfeats.holders.FilterChildHolder
import com.syjgin.pathfinderfeats.model.FilterValues

/**
 * Created by user1 on 27.08.17.
 */
class FiltersListAdapter(private val activity : FiltersActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val checkedList = mutableListOf<String>()
    private var selectedSource: String = ""
    private var selectedRace: String = ""
    private var selectedSkill : String = ""
    private var selectedType : String = ""

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
                holder.main = view.findViewById<LinearLayout>(R.id.main)
                holder.textView = view.findViewById<TextView>(R.id.categoryText)
                return holder
            }
            CellType.Checkbox.num -> {
                val view = inflater.inflate(R.layout.item_filter_checkbox, parent, false)
                val holder = FilterCheckboxHolder(view)
                holder.checkbox = view.findViewById<AppCompatCheckBox>(R.id.checkbox)
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
                FilterValuesActivity.ValueMode.SKILLS -> selectedSkill.isEmpty()
                FilterValuesActivity.ValueMode.TYPE -> selectedType.isEmpty()
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
                    FilterValuesActivity.ValueMode.SKILLS -> MainApp.instance?.applicationContext?.resources?.getString(R.string.skills_title)
                    FilterValuesActivity.ValueMode.TYPE -> MainApp.instance?.applicationContext?.resources?.getString(R.string.feat_type_title)
                    else -> {
                        ""
                    }
                }
                val selectedString = when(mode) {
                    FilterValuesActivity.ValueMode.SOURCE -> selectedSource
                    FilterValuesActivity.ValueMode.RACE -> selectedRace
                    FilterValuesActivity.ValueMode.SKILLS -> selectedSkill
                    FilterValuesActivity.ValueMode.TYPE -> selectedType
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
        2 -> FilterValuesActivity.ValueMode.SKILLS
        3 -> FilterValuesActivity.ValueMode.TYPE
        else -> {
            null
        }
    }

    override fun getItemCount(): Int = MainApp.instance?.applicationContext?.resources
            ?.getStringArray(R.array.filter_boolean_values)?.size
            ?.plus(MainApp.instance?.applicationContext?.resources
                    ?.getStringArray(R.array.filter_string_values)!!.size)!!

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
                FilterValuesActivity.ValueMode.SKILLS -> selectedSkill = selected
                FilterValuesActivity.ValueMode.TYPE -> selectedType = selected
            }
            notifyDataSetChanged()
        }
    }

    fun clear() {
        selectedSource = ""
        selectedRace = ""
        selectedSkill = ""
        selectedType = ""
        checkedList.clear()
        notifyDataSetChanged()
    }

    fun getFilterValues() : FilterValues {
        val filter = FilterValues()
        var i = 0;
        while (i < MainApp.instance?.applicationContext?.resources?.getStringArray(R.array.filter_boolean_values)!!.size) {
            val text = MainApp.instance?.applicationContext?.resources?.getStringArray(R.array.filter_boolean_values)?.get(i)
            if(checkedList.contains(text)) {
                when(i) {
                    0 -> filter.teamwork = true
                    1 -> filter.critical = true
                    2 -> filter.grit = true
                    3 -> filter.style = true
                    4 -> filter.performance = true
                    5 -> filter.companion_familiar = true
                    6 -> filter.multiples = true
                    7 -> filter.panache = true
                    8 -> filter.betrayal = true
                    9 -> filter.targeting = true
                    10 -> filter.esoteric = true
                    11 -> filter.stare = true
                    12 -> filter.weapon_mastery = true
                    13 -> filter.item_mastery = true
                    14 -> filter.armor_mastery = true
                    15 -> filter.shield_mastery = true
                    16 -> filter.blood_hex = true
                    17 -> filter.trick = true
                }
            }
            i++
        }
        filter.sourceFilter = selectedSource
        filter.skillFilter = selectedSkill
        filter.raceFilter = selectedRace
        filter.typeFilter = selectedType
        return filter
    }
}