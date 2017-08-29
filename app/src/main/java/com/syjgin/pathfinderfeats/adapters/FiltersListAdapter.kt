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
import com.syjgin.pathfinderfeats.activities.FiltersActivity
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.*

/**
 * Created by user1 on 27.08.17.
 */
class FiltersListAdapter(private val activity : FiltersActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val children = listOf<Int>(0,1)
    val checkedList = mutableListOf<String>()

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
        if(position in children) {
            childElement = MainApp.instance?.applicationContext?.resources?.getStringArray(R.array.filter_string_values)?.get(position).toString()
        } else {
            checkboxElement = MainApp.instance?.applicationContext?.resources?.getStringArray(R.array.filter_boolean_values)?.get(position -2).toString()
        }
        if(holder is FilterCheckboxHolder) {
            FilterCheckboxHolder.bind(holder, checkboxElement, this)
        }
        if(holder is FilterChildHolder) {
            FilterChildHolder.bind(holder, childElement, activity)
        }
    }

    override fun getItemCount(): Int = 20

    override fun getItemViewType(position: Int): Int {
        if(position in children)
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
}