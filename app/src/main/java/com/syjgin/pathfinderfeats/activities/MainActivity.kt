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
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.adapters.FeatListAdapter
import com.syjgin.pathfinderfeats.dialogs.AboutAppDialog
import com.syjgin.pathfinderfeats.model.Feat
import com.syjgin.pathfinderfeats.model.FilterValues
import com.syjgin.pathfinderfeats.model.ProgressEvent
import com.syjgin.pathfinderfeats.persistentData.DataStorage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import java.util.concurrent.Executors

class MainActivity : BackButtonActivity(), SearchView.OnQueryTextListener {

    enum class OpenMode {
        STANDARD,
        SEARCH,
        CHILD,
        PARENT,
        FILTER
    }

    private var _currentMode: OpenMode = OpenMode.STANDARD
    var currentMode : OpenMode
        get() = _currentMode
        private set(value) {
            _currentMode = value
        }

    private var adapter: FeatListAdapter? = null

    private var featId : Int? = null
    fun featId(): Int? = featId

    private var featName : String = ""

    private lateinit var list : RecyclerView
    private lateinit var searchView : SearchView
    private lateinit var notFoundCaption : View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarDeteminate: ProgressBar
    private lateinit var loadingCaption : LinearLayout

    private val intentQueue : LinkedList<Intent> = LinkedList()

    private val scrollValueQueue : LinkedList<Int> = LinkedList()
    private lateinit var layoutManager : LinearLayoutManager

    private var filterValues: FilterValues? = null
    fun filterValues() : FilterValues? = filterValues

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.isIconified = true;
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean = true


    fun openChildFeat(feat: Feat) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(MainActivity.CHILD_MODE, true)
        bundle.putInt(MainActivity.FEAT_ID, feat.id)
        bundle.putString(MainActivity.FEAT_NAME, feat.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun onResult(isEmpty : Boolean) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if(isEmpty) {
                notFoundCaption.visibility = View.VISIBLE
                list.visibility = View.GONE
                dismissProgressbar()
            } else {
                notFoundCaption.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        }
    }

    fun openParentFeat(feat: Feat) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(MainActivity.PARENT_MODE, true)
        bundle.putInt(MainActivity.FEAT_ID, feat.id)
        bundle.putString(MainActivity.FEAT_NAME, feat.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun openFeatDetails(feat: Feat) {
        val intent = Intent(this, FeatDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(FeatDetailsActivity.FEAT, feat)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    companion object {
        const val FEAT_ID = "FEAT_ID"
        const val FEAT_NAME = "FEAT_NAME"
        const val PARENT_MODE = "PARENT_MODE"
        const val CHILD_MODE = "CHILD_MODE"
        const val ABOUT_TEXT = "<p><b>This small app is intended to search and explore hierarchy of Pathfinder Feats.</b></p>" +
                "<p>All feats data was downloaded from <a href=\"http://www.d20pfsrd.com/\">www.d20pfsrd.com</a>.</p>" +
                "<p>Icons was downloaded from <a href=\"https://material.io/icons/\">https://material.io</a> (mostly).</p>" +
                "<p>Any help is welcome! You can send me comments via <a href=\"mailto:gruz103@gmail.com\">email</a> or create an issue on <a href=\"https://github.com/Syjgin/PathfinderFeats\">" +
                "project's github page</a></p>"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createToolbar()

        list = findViewById(R.id.featsList)
        notFoundCaption = findViewById(R.id.noResults)
        progressBar = findViewById(R.id.progressBar)
        loadingCaption = findViewById(R.id.loadingDatabase)
        progressBarDeteminate = findViewById(R.id.determinateProgressBar)

        if(!DataStorage.isDbInitialized(this)) {
            loadingCaption.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        val executor = Executors.newSingleThreadExecutor()
        adapter = FeatListAdapter(this)
        adapter?.setExecutor(executor)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
        layoutManager = list.layoutManager as LinearLayoutManager
        intentQueue.push(intent)
        getParametersFromIntent()
    }

    private fun getParametersFromIntent() {
        var searchQuery = ""
        if(intent.action != null && intent.action == Intent.ACTION_SEARCH) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY)
            if(searchQuery.isNotEmpty()) {
                adapter?.performSearch(searchQuery)
            }
        }
        val featIdFromIntent = intent.getIntExtra(FEAT_ID, -1)
        if(featIdFromIntent >= 0)
            featId = featIdFromIntent
        filterValues = intent.getSerializableExtra(FiltersActivity.FILTER) as FilterValues?
        _currentMode = OpenMode.STANDARD
        if(intent.action == Intent.ACTION_SEARCH)
            _currentMode = OpenMode.SEARCH
        if(intent.getBooleanExtra(PARENT_MODE, false))
            _currentMode = OpenMode.PARENT
        if(intent.getBooleanExtra(CHILD_MODE, false))
            _currentMode = OpenMode.CHILD
        if(filterValues != null)
            _currentMode = OpenMode.FILTER
        val name = intent.getStringExtra(FEAT_NAME)
        if(name != null)
            featName = name
        invalidateOptionsMenu()
        if(_currentMode != OpenMode.STANDARD) {
            displayBackButton()
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        title = when(_currentMode) {
            OpenMode.STANDARD -> getString(R.string.title_activity_main)
            OpenMode.CHILD -> String.format(getString(R.string.child_feats_title), featName)
            OpenMode.PARENT -> String.format(getString(R.string.parent_feats_title), featName)
            OpenMode.FILTER -> getString(R.string.filtered_feats)
            OpenMode.SEARCH -> searchQuery
        }
        if(_currentMode != OpenMode.SEARCH)
            adapter?.queryAsync()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(_currentMode == OpenMode.STANDARD) {
            menuInflater.inflate(R.menu.search_menu, menu)
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu?.findItem(R.id.search)?.actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.setOnQueryTextListener(this)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.search -> onSearchRequested()
            R.id.filter -> {
                val intent = Intent(this, FiltersActivity::class.java)
                startActivity(intent)
            }
            R.id.info -> {
                val dialog = AboutAppDialog(this)
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        scrollValueQueue.push(layoutManager.findLastVisibleItemPosition())
        intentQueue.push(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        setIntent(intent)
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
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                var previousValue = 0
                if(!scrollValueQueue.isEmpty()) {
                    previousValue = scrollValueQueue.pop()
                }
                list.scrollToPosition(previousValue)
            }, 300)
        }
    }

    fun dismissProgressbar() {
        progressBar.visibility = View.GONE
        loadingCaption.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onUnpackProgressChanged(event : ProgressEvent) {
        progressBarDeteminate.progress = event.value
    }
}
