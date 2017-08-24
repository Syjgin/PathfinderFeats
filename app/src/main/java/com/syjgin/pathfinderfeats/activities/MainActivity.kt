package com.syjgin.pathfinderfeats.activities

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FeatListAdapter
import com.syjgin.pathfinderfeats.interfaces.FeatListHandler
import com.syjgin.pathfinderfeats.model.Feat
import java.util.concurrent.Executors

class MainActivity : BackButtonActivity(), FeatListHandler {

    private var adapter: FeatListAdapter? = null

    private var parentMode = false
    override fun isParentMode(): Boolean = parentMode

    private var childMode = false
    override fun isChildMode(): Boolean = childMode

    private var featId : Int? = null
    override fun featId(): Int? = featId

    private var featName : String = ""

    private var list : RecyclerView? = null

    private var searchQuery : String = ""

    override fun openChildFeat(feat: Feat) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(MainActivity.CHILD_MODE, true)
        bundle.putInt(MainActivity.FEAT_ID, feat.id)
        bundle.putString(MainActivity.FEAT_NAME, feat.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onEmptyResult() {
        val notFoundCaption = findViewById(R.id.noResults)
        notFoundCaption.visibility = View.VISIBLE
        list?.visibility = View.GONE
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

    override fun openFeatDetails(feat: Feat) {
        val intent = Intent(this, FeatDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(FeatDetailsActivity.FEAT, feat)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun isSearchRequestSubmitted() : Boolean {
        if(intent.action == null)
            return false
        if(intent.action == Intent.ACTION_SEARCH) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY)
            if(searchQuery.isNotEmpty()) {
                displayBackButton()
                title = searchQuery
                adapter?.performSearch(searchQuery)
                return true
            } else {
                finish()
                return false
            }
        }
        return false
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
        createToolbar()
        getParametersFromIntent()
        if(parentMode || childMode) {
            displayBackButton()
        }
        if(parentMode) {
            setTitle(String.format(getString(R.string.parent_feats_title), featName))
        }
        if(childMode) {
            setTitle(String.format(getString(R.string.child_feats_title), featName))
        }
        list = findViewById(R.id.featsList) as RecyclerView
        val executor = Executors.newSingleThreadExecutor()
        adapter = FeatListAdapter(this)
        adapter?.setExecutor(executor)
        list?.adapter = adapter
        list?.layoutManager = LinearLayoutManager(this)
        if(!isSearchRequestSubmitted())
            adapter?.queryAsync()
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
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(!parentMode && !childMode && searchQuery.isEmpty())
            menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.search) {
            onSearchRequested()
        }
        return super.onOptionsItemSelected(item)
    }
}
