package com.syjgin.pathfinderfeats.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.FeatListItemHolder
import com.syjgin.pathfinderfeats.interfaces.FeatListHandler
import com.syjgin.pathfinderfeats.model.Feat
import com.syjgin.pathfinderfeats.model.Models
import io.requery.android.QueryRecyclerAdapter
import io.requery.kotlin.*
import io.requery.query.Result
import io.requery.reactivex.ReactiveResult

/**
 * Created by maksimovoleg on 17/08/2017.
 */
class FeatListAdapter(handler: FeatListHandler) :
        QueryRecyclerAdapter<Feat, FeatListItemHolder>(Models.DEFAULT, Feat::class.java) {

    private var searchQuery : String = ""
    val featListHandler: FeatListHandler = handler

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeatListItemHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.item_feat_list, parent, false)
        val holder = FeatListItemHolder(view)
        holder.titleView = view.findViewById(R.id.featTitle) as TextView
        holder.childIcon = view.findViewById(R.id.childFeats) as ImageView
        holder.parentIcon = view.findViewById(R.id.parentFeats) as ImageView
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
        if(searchQuery.isNotEmpty()) {
            val result = MainApp.instance?.dataStore?.select(Feat::class)
                    ?.where(Feat::name.like("%$searchQuery%"))
                    ?.orderBy(Feat::name.asc())
                    ?.get() as Result<Feat>
            featListHandler.onResult(result.count() == 0)
            searchQuery = ""
            return result
        }
        if(featListHandler.isChildMode()) {
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
        if(featListHandler.isParentMode()) {
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
        featListHandler.onResult(false)
        return MainApp.instance?.dataStore?.select(Feat::class)?.orderBy(Feat::name.asc())
                ?.get() as Result<Feat>
    }

    private fun emptyResult(): Result<Feat> {
        featListHandler.onResult(true)
        return MainApp.instance?.dataStore?.select(Feat::class)?.where(Feat::id.eq(-1))
                ?.get() as Result<Feat> //TODO: return empty result explicitly
    }

}