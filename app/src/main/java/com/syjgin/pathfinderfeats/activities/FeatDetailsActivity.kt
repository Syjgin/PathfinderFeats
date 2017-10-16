package com.syjgin.pathfinderfeats.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
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
        const val SEPARATOR = ", "
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
        title = feat.name
        val typeText = findViewById<TextView>(R.id.featType)
        displayCharacteristic(typeText, feat.type, R.string.type_title)
        val descriptionText = findViewById<TextView>(R.id.description)
        descriptionText.text = feat.description
        val prerequisitesText = findViewById<TextView>(R.id.prerequisites)
        displayCharacteristic(prerequisitesText, feat.prerequisites, R.string.prerequisites_title)
        val childFeats = findViewById<TextView>(R.id.childFeats)
        val showParentButton = findViewById<Button>(R.id.showParent)
        displayCharacteristic(childFeats, feat.prerequisite_feats, R.string.prerequisite_feats)
        if(feat.prerequisite_feats as String? == null) {
            showParentButton.visibility = View.GONE
        } else {
            showParentButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean(MainActivity.PARENT_MODE, true)
                bundle.putInt(MainActivity.FEAT_ID, feat.id)
                bundle.putString(MainActivity.FEAT_NAME, feat.name)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
        val showChildButton = findViewById<Button>(R.id.showChild)
        showChildButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean(MainActivity.CHILD_MODE, true)
            bundle.putInt(MainActivity.FEAT_ID, feat.id)
            bundle.putString(MainActivity.FEAT_NAME, feat.name)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        val benefitText = findViewById<TextView>(R.id.benefit)
        displayCharacteristic(benefitText, feat.benefit, R.string.benefit_title)
        val normalText = findViewById<TextView>(R.id.normal)
        displayCharacteristic(normalText, feat.normal, R.string.normal_title)
        val specialText = findViewById<TextView>(R.id.special)
        displayCharacteristic(specialText, feat.special, R.string.special_title)
        val sourceText = findViewById<TextView>(R.id.source)
        displayCharacteristic(sourceText, feat.source, R.string.source_title)
        val specialFeatureText = findViewById<TextView>(R.id.specialFeatures)
        val builder = StringBuilder()
        addBooleanToBuilder(builder, feat.teamwork, R.string.teamwork)
        addBooleanToBuilder(builder, feat.critical, R.string.critical)
        addBooleanToBuilder(builder, feat.grit, R.string.grit)
        addBooleanToBuilder(builder, feat.style, R.string.style)
        addBooleanToBuilder(builder, feat.performance, R.string.performance)
        addBooleanToBuilder(builder, feat.racial, R.string.racial)
        addBooleanToBuilder(builder, feat.companion_familiar, R.string.companion_familiar)
        addBooleanToBuilder(builder, feat.multiples, R.string.multiples)
        addBooleanToBuilder(builder, feat.panache, R.string.panache)
        addBooleanToBuilder(builder, feat.betrayal, R.string.betrayal)
        addBooleanToBuilder(builder, feat.targeting, R.string.targeting)
        addBooleanToBuilder(builder, feat.esoteric, R.string.esoteric)
        addBooleanToBuilder(builder, feat.stare, R.string.stare)
        addBooleanToBuilder(builder, feat.weapon_mastery, R.string.weapon_mastery)
        addBooleanToBuilder(builder, feat.item_mastery, R.string.item_mastery)
        addBooleanToBuilder(builder, feat.armor_mastery, R.string.armor_mastery)
        addBooleanToBuilder(builder, feat.shield_mastery, R.string.shield_mastery)
        addBooleanToBuilder(builder, feat.blood_hex, R.string.blood_hex)
        addBooleanToBuilder(builder, feat.trick, R.string.trick)
        if(!builder.isEmpty()) {
            displayCharacteristic(specialFeatureText, builder.toString().removeSuffix(SEPARATOR), R.string.special_features_title)
        }
        val raceText = findViewById<TextView>(R.id.race)
        displayCharacteristic(raceText, feat.race_name, R.string.race_title)
        val noteText = findViewById<TextView>(R.id.note)
        displayCharacteristic(noteText, feat.note, R.string.note_title)
        val goalText = findViewById<TextView>(R.id.goal)
        displayCharacteristic(goalText, feat.goal, R.string.goal_title)
        val completeText = findViewById<TextView>(R.id.completeonBenefit)
        displayCharacteristic(completeText, feat.completion_benefit, R.string.complete_title)
        val traitsText = findViewById<TextView>(R.id.suggestedTraits)
        displayCharacteristic(traitsText, feat.suggested_traits, R.string.traits_title)
        val skillsText = findViewById<TextView>(R.id.skills)
        displayCharacteristic(skillsText, feat.prerequisite_feats, R.string.skills_title)
    }

    private fun addBooleanToBuilder(builder : StringBuilder, boolean: Boolean, resource : Int) {
        if(boolean) {
            builder.append(getString(resource))
            builder.append(SEPARATOR)
        }
    }

    private fun displayCharacteristic(textView: TextView, text: String?, format: Int) {
        if(text == null) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = Html.fromHtml(String.format(getString(format), text))
        }
    }
}
