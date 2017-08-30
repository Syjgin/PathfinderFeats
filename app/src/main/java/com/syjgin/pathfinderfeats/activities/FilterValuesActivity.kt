package com.syjgin.pathfinderfeats.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FeatListAdapter
import com.syjgin.pathfinderfeats.adapters.FilterValuesAdapter
import java.util.concurrent.Executors

class FilterValuesActivity : BackButtonActivity() {
    var adapter : FilterValuesAdapter? = null

    companion object {
        const val SOURCE_MODE = "SOURCE_MODE"
        const val SELECTED = "SELECTED"
        const val VALUES_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_values)
        createToolbar()
        displayBackButton()
        val list = findViewById(R.id.list) as RecyclerView
        list.layoutManager = LinearLayoutManager(this)
        adapter = FilterValuesAdapter(intent.getBooleanExtra(SOURCE_MODE, false), this)
        list.adapter = adapter
    }
}
