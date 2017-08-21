package com.syjgin.pathfinderfeats.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FeatListAdapter
import com.syjgin.pathfinderfeats.interfaces.FeatListHandler
import com.syjgin.pathfinderfeats.model.Feat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), FeatListHandler {
    private var parentMode = false
    override fun isParentMode(): Boolean = parentMode

    private var childMode = false
    override fun isChildMode(): Boolean = childMode

    private var featId : Int? = null
    override fun featId(): Int? = featId

    private var featName : String = ""

    override fun openChildFeat(feat: Feat) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(MainActivity.CHILD_MODE, true)
        bundle.putInt(MainActivity.FEAT_ID, feat.id)
        bundle.putString(MainActivity.FEAT_NAME, feat.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun openParentFeat(feat: Feat) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(MainActivity.PARENT_MODE, true)
        bundle.putInt(MainActivity.FEAT_ID, feat.id)
        bundle.putString(MainActivity.FEAT_NAME, feat.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    companion object {
        const val FEAT_ID = "FEAT_ID"
        const val FEAT_NAME = "FEAT_NAME"
        const val PARENT_MODE = "PARENT_MODE"
        const val CHILD_MODE = "CHILD_MODE"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getParametersFromIntent()
        if(parentMode || childMode) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }
        if(parentMode) {
            setTitle(String.format(getString(R.string.parent_feats_title), featName))
        }
        if(childMode) {
            setTitle(String.format(getString(R.string.child_feats_title), featName))
        }
        val recyclerView = findViewById(R.id.featsList) as RecyclerView
        val executor = Executors.newSingleThreadExecutor()
        val adapter = FeatListAdapter(this)
        adapter.setExecutor(executor)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.queryAsync()
    }

    private fun getParametersFromIntent() {
        val featIdFromIntent = intent.getIntExtra(FEAT_ID, -1)
        if(featIdFromIntent >= 0)
            featId = featIdFromIntent
        parentMode = intent.getBooleanExtra(PARENT_MODE, false)
        childMode = intent.getBooleanExtra(CHILD_MODE, false)
        val name = intent.getStringExtra(FEAT_NAME)
        if(name != null)
            featName = name
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
