package com.syjgin.pathfinderfeats.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FilterValuesAdapter

class FilterValuesActivity : BackButtonActivity() {
    
    enum class ValueMode {
        SOURCE,
        RACE,
        SKILLS
    }

    companion object {
        const val VALUE_MODE = "VALUE_MODE"
        const val SELECTED = "SELECTED"
        const val VALUES_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_values)
        createToolbar()
        displayBackButton()
        val list = findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(this)
        val adapter = FilterValuesAdapter(intent.getSerializableExtra(VALUE_MODE) as ValueMode, this)
        list.adapter = adapter
    }
}
