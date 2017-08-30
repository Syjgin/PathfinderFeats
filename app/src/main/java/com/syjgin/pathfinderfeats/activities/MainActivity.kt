package com.syjgin.pathfinderfeats.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FeatListAdapter
import com.syjgin.pathfinderfeats.interfaces.FeatListHandler
import com.syjgin.pathfinderfeats.model.Feat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : BackButtonActivity(), FeatListHandler, SearchView.OnQueryTextListener {

    private var adapter: FeatListAdapter? = null

    private var parentMode = false
    override fun isParentMode(): Boolean = parentMode

    private var childMode = false
    override fun isChildMode(): Boolean = childMode

    private var searchMode = false

    private var featId : Int? = null
    override fun featId(): Int? = featId

    private var featName : String = ""

    private var list : RecyclerView? = null

    private var searchView : SearchView? = null

    private val intentQueue : LinkedList<Intent> = LinkedList()

    var notFoundCaption : View? = null

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView?.setQuery("", false);
        searchView?.clearFocus();
        searchView?.isIconified = true;
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }


    override fun openChildFeat(feat: Feat) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(MainActivity.CHILD_MODE, true)
        bundle.putInt(MainActivity.FEAT_ID, feat.id)
        bundle.putString(MainActivity.FEAT_NAME, feat.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onResult(isEmpty : Boolean) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if(isEmpty) {
                notFoundCaption?.visibility = View.VISIBLE
                list?.visibility = View.GONE
            } else {
                notFoundCaption?.visibility = View.GONE
                list?.visibility = View.VISIBLE
            }
        }
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

    private fun performSearchRequest() {
        if(intent.action == null)
            return
        if(intent.action == Intent.ACTION_SEARCH) {
            val searchQuery = intent.getStringExtra(SearchManager.QUERY)
            if(searchQuery.isNotEmpty()) {
                title = searchQuery
                adapter?.performSearch(searchQuery)
                return
            }
        }
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
        list = findViewById(R.id.featsList) as RecyclerView
        notFoundCaption = findViewById(R.id.noResults)
        val executor = Executors.newSingleThreadExecutor()
        adapter = FeatListAdapter(this)
        adapter?.setExecutor(executor)
        list?.adapter = adapter
        list?.layoutManager = LinearLayoutManager(this)
        intentQueue.push(Intent())
        getParametersFromIntent()
    }

    private fun getParametersFromIntent() {
        val featIdFromIntent = intent.getIntExtra(FEAT_ID, -1)
        if(featIdFromIntent >= 0)
            featId = featIdFromIntent
        searchMode = intent.action == Intent.ACTION_SEARCH
        parentMode = intent.getBooleanExtra(PARENT_MODE, false)
        childMode = intent.getBooleanExtra(CHILD_MODE, false)
        val name = intent.getStringExtra(FEAT_NAME)
        if(name != null)
            featName = name
        invalidateOptionsMenu()
        if(parentMode || childMode || searchMode) {
            displayBackButton()
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        if(parentMode) {
            title = String.format(getString(R.string.parent_feats_title), featName)
        } else if(childMode) {
            title = String.format(getString(R.string.child_feats_title), featName)
        } else if(!searchMode){
            title = getString(R.string.title_activity_main)
        }
        if(!searchMode)
            adapter?.queryAsync()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(!parentMode && !childMode && !searchMode) {
            menuInflater.inflate(R.menu.search_menu, menu)
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu?.findItem(R.id.search)?.actionView as SearchView?
            searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView?.setOnQueryTextListener(this)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.search) {
            onSearchRequested()
        }
        if(item?.itemId == R.id.filter) {
            val intent = Intent(this, FiltersActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        intentQueue.push(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        setIntent(intent)
        performSearchRequest()
        getParametersFromIntent()
    }

    override fun onBackPressed() {
        if(intentQueue.size == 0)
            super.onBackPressed()
        else {
            intentQueue.pop()
            if(intentQueue.size == 0) {
                super.onBackPressed()
                return
            }
            val previousIntent = intentQueue.first()
            handleIntent(previousIntent)
        }
    }
}
