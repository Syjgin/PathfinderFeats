package com.syjgin.pathfinderfeats.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.model.Feat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.requery.kotlin.eq

class FeatDetailsActivity : BackButtonActivity() {
    var feat : Feat? = null

    companion object {
        const val FEAT = "FEAT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feat_details)
        createToolbar()
        displayBackButton()
        getParametersFromIntent()
    }

    private fun getParametersFromIntent() {
        val featIdFromIntent = intent.getIntExtra(MainActivity.FEAT_ID, -1)
        if(featIdFromIntent >= 0) {
            MainApp.instance?.dataStore?.select(Feat::class)
                    ?.where(Feat::id.eq(featIdFromIntent))?.get()?.observableResult()
                    ?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe {
                initWithFeat(it.first())
            }
        } else {
            val featFromIntent = intent.getSerializableExtra(FEAT) as Feat?
            if(featFromIntent != null)
                initWithFeat(featFromIntent)
        }
    }

    private fun initWithFeat(feat : Feat) {
        this.feat = feat
        setTitle(feat.name)
        val typeText = findViewById(R.id.featType) as TextView
        typeText.setText(String.format(getString(R.string.type_title), feat.type))
        val descriptionText = findViewById(R.id.description) as TextView
        descriptionText.setText(feat.description)
        val prerequisitesText = findViewById(R.id.prerequisites) as TextView
        if(feat.prerequisites as String? != null)
            prerequisitesText.setText(String.format(getString(R.string.prerequisites_title), feat.prerequisites))
        else
            prerequisitesText.visibility = View.GONE
        val childFeats = findViewById(R.id.childFeats) as TextView
        if(feat.prerequisite_feats as String? == null) {
            childFeats.visibility = View.GONE
        } else if(feat.prerequisite_feats.isEmpty()) {
            childFeats.visibility = View.GONE
        } else {
            childFeats.setText(String.format(getString(R.string.prerequisite_feats), feat.prerequisite_feats))
            childFeats.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean(MainActivity.PARENT_MODE, true)
                bundle.putInt(MainActivity.FEAT_ID, feat.id)
                bundle.putString(MainActivity.FEAT_NAME, feat.name)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }
}
