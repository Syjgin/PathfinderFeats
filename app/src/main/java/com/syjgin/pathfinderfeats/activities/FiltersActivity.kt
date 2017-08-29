package com.syjgin.pathfinderfeats.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View

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
        adapter = FiltersListAdapter()
        filterList.adapter = adapter
    }

}
