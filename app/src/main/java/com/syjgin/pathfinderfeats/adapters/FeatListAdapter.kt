package com.syjgin.pathfinderfeats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.activities.MainActivity
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.FeatListItemHolder
import com.syjgin.pathfinderfeats.model.Feat
import com.syjgin.pathfinderfeats.model.Models
import io.requery.android.QueryRecyclerAdapter
import io.requery.kotlin.*
import io.requery.query.Result
import io.requery.reactivex.ReactiveResult
import kotlin.reflect.KMutableProperty1

/**
 * Created by maksimovoleg on 17/08/2017.
 */
class FeatListAdapter(handler: MainActivity) :
        QueryRecyclerAdapter<Feat, FeatListItemHolder>(Models.DEFAULT, Feat::class.java) {

    private var searchQuery : String = ""
    val featListHandler = handler

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeatListItemHolder {
        featListHandler.dismissProgressbar()
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.item_feat_list, parent, false)
        val holder = FeatListItemHolder(view)
        holder.titleView = view.findViewById(R.id.featTitle)
        holder.childIcon = view.findViewById(R.id.childFeats)
        holder.parentIcon = view.findViewById(R.id.parentFeats)
        return holder
    }

    fun performSearch(query : String) {
        searchQuery = query
        if(searchQuery.isNotEmpty()) {
            queryAsync()
        }
    }

    override fun onBindViewHolder(item: Feat, holder: FeatListItemHolder?, position: Int) {
        FeatListItemHolder.bind(holder, item, featListHandler)
    }

    override fun performQuery(): Result<Feat> {
        val mythic = "Mythic"
        when(featListHandler.currentMode) {
            MainActivity.OpenMode.SEARCH -> {
                if(searchQuery.isNotEmpty()) {
                    val result = MainApp.instance?.dataStore?.select(Feat::class)
                            ?.where(Feat::name.like("%$searchQuery%"))
                            ?.orderBy(Feat::name.asc())
                            ?.get() as Result<Feat>
                    featListHandler.onResult(result.count() == 0)
                    searchQuery = ""
                    return result
                }
            }
            MainActivity.OpenMode.CHILD -> {
                val result = MainApp.instance?.dataStore?.select(Feat::class)
                        ?.where(Feat::id.eq(featListHandler.featId()))?.get() as Result<Feat>
                if(result.count() > 0) {
                    if(result.first().type == mythic) {
                        val query = MainApp.instance?.dataStore?.select(Feat::class)
                                ?.where(Feat::prerequisite_feats.like(result.first().name)
                                        .and(Feat::name.notLike(result.first().name))
                                        .and(Feat::type.like(mythic)))
                                ?.orderBy(Feat::name.asc())
                                ?.get() as Result<Feat>
                        featListHandler.onResult(query.count() == 0)
                        return query
                    } else {
                        val name2find = result.first().name
                        val query = MainApp.instance?.dataStore?.select(Feat::class)
                                ?.where(Feat::prerequisite_feats.like("%, $name2find%")
                                        .or(Feat::prerequisite_feats.like("%$name2find,%"))
                                        .or(Feat::prerequisite_feats.like("%$name2find|%"))
                                        .or(Feat::prerequisite_feats.like("%|$name2find%"))
                                        .or(Feat::prerequisite_feats.like("%$name2find | %"))
                                        .or(Feat::prerequisite_feats.like("% | $name2find%"))
                                        .or(Feat::prerequisite_feats.eq(name2find)))
                                ?.orderBy(Feat::name.asc())
                                ?.get() as Result<Feat>
                        featListHandler.onResult(query.count() == 0)
                        return query
                    }
                } else {
                    return emptyResult()
                }
            }
            MainActivity.OpenMode.PARENT -> {
                val result = MainApp.instance?.dataStore?.select(Feat::class)
                        ?.where(Feat::id.eq(featListHandler.featId()))?.get() as Result<Feat>
                if(result.count() > 0) {
                    val namesList = result.first().prerequisite_feats.split(", ", "|", " | ")
                    val filtered = mutableListOf<String>()
                    for (listElement : String in namesList) {
                        filtered.add(listElement.replace(Regex("\\(.*\\)"), "").trim())
                    }
                    val query = MainApp.instance?.dataStore?.select(Feat::class)
                            ?.where(Feat::name.`in`(filtered).and(Feat::type.notLike(mythic)))
                            ?.orderBy(Feat::name.asc())
                            ?.get() as Result<Feat>
                    featListHandler.onResult(query.count() == 0)
                    return query
                } else {
                    return emptyResult()
                }
            }
            MainActivity.OpenMode.FILTER -> {
                val filterValues = featListHandler.filterValues()
                if(filterValues != null) {
                    val query = MainApp.instance?.dataStore?.select(Feat::class);
                    var condition : WhereAndOr<ReactiveResult<Feat>>? = null
                    if(filterValues.sourceFilter.isNotEmpty()) {
                        condition = query?.where(Feat::source.like(filterValues.sourceFilter));
                    }
                    if(filterValues.raceFilter.isNotEmpty()) {
                        val raceFilter = filterValues.raceFilter
                        condition = addStringCharacteristicToFilter(condition, query, raceFilter, Feat::race_name)
                    }
                    if(filterValues.skillFilter.isNotEmpty()) {
                        val skillFilter = filterValues.skillFilter
                        condition = addStringCharacteristicToFilter(condition, query, skillFilter, Feat::prerequisite_skills)
                    }
                    if(filterValues.panache) {
                        condition = addBooleanValueToFilter(condition, query,Feat::panache)
                    }
                    if(filterValues.trick) {
                        condition = addBooleanValueToFilter(condition, query,Feat::trick)
                    }
                    if(filterValues.blood_hex) {
                        condition = addBooleanValueToFilter(condition, query,Feat::blood_hex)
                    }
                    if(filterValues.shield_mastery) {
                        condition = addBooleanValueToFilter(condition, query,Feat::shield_mastery)
                    }
                    if(filterValues.armor_mastery) {
                        condition = addBooleanValueToFilter(condition, query,Feat::armor_mastery)
                    }
                    if(filterValues.item_mastery) {
                        condition = addBooleanValueToFilter(condition, query,Feat::item_mastery)
                    }
                    if(filterValues.weapon_mastery) {
                        condition = addBooleanValueToFilter(condition, query,Feat::weapon_mastery)
                    }
                    if(filterValues.stare) {
                        condition = addBooleanValueToFilter(condition, query,Feat::stare)
                    }
                    if(filterValues.esoteric) {
                        condition = addBooleanValueToFilter(condition, query,Feat::esoteric)
                    }
                    if(filterValues.targeting) {
                        condition = addBooleanValueToFilter(condition, query,Feat::targeting)
                    }
                    if(filterValues.betrayal) {
                        condition = addBooleanValueToFilter(condition, query,Feat::betrayal)
                    }
                    if(filterValues.multiples) {
                        condition = addBooleanValueToFilter(condition, query,Feat::multiples)
                    }
                    if(filterValues.companion_familiar) {
                        condition = addBooleanValueToFilter(condition, query,Feat::companion_familiar)
                    }
                    if(filterValues.performance) {
                        condition = addBooleanValueToFilter(condition, query,Feat::performance)
                    }
                    if(filterValues.style) {
                        condition = addBooleanValueToFilter(condition, query,Feat::style)
                    }
                    if(filterValues.grit) {
                        condition = addBooleanValueToFilter(condition, query,Feat::grit)
                    }
                    if(filterValues.critical) {
                        condition = addBooleanValueToFilter(condition, query,Feat::critical)
                    }
                    if(filterValues.teamwork) {
                        condition = addBooleanValueToFilter(condition, query,Feat::teamwork)
                    }
                    val result = condition?.get() as Result<Feat>
                    featListHandler.onResult(result.count() == 0)
                    return result
                }
            }
            MainActivity.OpenMode.STANDARD -> {
                featListHandler.onResult(false)
                return MainApp.instance?.dataStore?.select(Feat::class)?.orderBy(Feat::name.asc())
                        ?.get() as Result<Feat>
            }
        }
        featListHandler.onResult(false)
        return MainApp.instance?.dataStore?.select(Feat::class)?.orderBy(Feat::name.asc())
                ?.get() as Result<Feat>
    }

    private fun addStringCharacteristicToFilter(condition: WhereAndOr<ReactiveResult<Feat>>?,
                                                query: Selection<ReactiveResult<Feat>>?,
                                                filter: String, characteristic : KMutableProperty1<Feat, String>): WhereAndOr<ReactiveResult<Feat>>? {
        var condition1 = condition
        if (condition1 == null) {
            condition1 = query?.where(characteristic.like("%$filter%"));
        } else {
            condition1 = condition1.and(characteristic.like("%$filter%"))
        }
        return condition1
    }

    private fun addBooleanValueToFilter(condition: WhereAndOr<ReactiveResult<Feat>>?,
                                        query: Selection<ReactiveResult<Feat>>?,
                                        characteristic: KMutableProperty1<Feat, Boolean>) : WhereAndOr<ReactiveResult<Feat>>? {
        var condition1 = condition
        if(condition1 == null) {
            condition1 = query?.where(characteristic.eq(true));
        } else {
            condition1 = condition1.and(characteristic.eq(true))
        }
        return condition1
    }

    private fun emptyResult(): Result<Feat> {
        featListHandler.onResult(true)
        return MainApp.instance?.dataStore?.select(Feat::class)?.where(Feat::id.eq(-1))
                ?.get() as Result<Feat> //TODO: return empty result explicitly
    }

}