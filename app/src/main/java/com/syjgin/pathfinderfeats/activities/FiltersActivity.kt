package com.syjgin.pathfinderfeats.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem

import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FiltersListAdapter

class FiltersActivity : BackButtonActivity() {

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
