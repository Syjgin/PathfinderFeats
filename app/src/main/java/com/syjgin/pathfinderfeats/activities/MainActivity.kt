package com.syjgin.pathfinderfeats.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.model.Feat
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FeatListAdapter
import io.reactivex.Observable
import io.requery.query.Result
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val recyclerView = findViewById(R.id.featsList) as RecyclerView
        val executor = Executors.newSingleThreadExecutor()
        val adapter = FeatListAdapter()
        adapter.setExecutor(executor)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.queryAsync()
    }

}
