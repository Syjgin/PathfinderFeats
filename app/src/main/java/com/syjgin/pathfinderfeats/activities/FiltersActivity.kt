package com.syjgin.pathfinderfeats.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FiltersListAdapter

class FiltersActivity : BackButtonActivity() {

    companion object {
        const val SOURCE_FILTER = "SOURCE_FILTER"
        const val RACE_FILTER = "RACE_FILTER"
        const val SKILL_FILTER = "SKILL_FILTER"
        const val BOOLEAN_FILTER = "BOOLEAN_FILTER"
    }

    var adapter : FiltersListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        createToolbar()
        displayBackButton()
        val filterList = findViewById(R.id.filterList) as RecyclerView
        filterList.layoutManager = LinearLayoutManager(this)
        adapter = FiltersListAdapter(this)
        filterList.adapter = adapter
        val apply = findViewById(R.id.apply) as Button
        apply.setOnClickListener {
            applyFilters()
        }
    }

    private fun applyFilters() {
        if(adapter == null)
            return
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        if(adapter?.selectedSource!!.isNotEmpty())
            bundle.putString(SOURCE_FILTER, adapter?.selectedSource)
        if(adapter?.selectedRace!!.isNotEmpty())
            bundle.putString(RACE_FILTER, adapter?.selectedRace)
        if(adapter?.selectedSource!!.isNotEmpty())
            bundle.putString(SKILL_FILTER, adapter?.selectedSource)
        bundle.putSerializable(BOOLEAN_FILTER, adapter?.getBooleanFilter())
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == FilterValuesActivity.VALUES_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                val valueMode = data?.getSerializableExtra(FilterValuesActivity.VALUE_MODE) as FilterValuesActivity.ValueMode
                val selected = data.getStringExtra(FilterValuesActivity.SELECTED)
                adapter?.updateValue(valueMode, selected)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.clear) {
            adapter?.clear()
        }
        return super.onOptionsItemSelected(item)
    }

}
