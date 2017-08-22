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
import io.requery.kotlin.eq
import io.requery.kotlin.like
import io.requery.kotlin.notLike
import io.requery.query.Result
import io.requery.reactivex.ReactiveResult

/**
 * Created by maksimovoleg on 17/08/2017.
 */
class FeatListAdapter(handler: FeatListHandler) : QueryRecyclerAdapter<Feat, FeatListItemHolder>(Models.DEFAULT, Feat::class.java) {
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

    override fun onBindViewHolder(item: Feat, holder: FeatListItemHolder?, position: Int) {
        FeatListItemHolder.bind(holder, item, featListHandler)
    }

    override fun performQuery(): Result<Feat> {
        if(featListHandler.isChildMode()) {
            val result = MainApp.instance?.dataStore?.select(Feat::class)?.where(Feat::id.eq(featListHandler.featId()))?.get() as Result<Feat>
            if(result.count() > 0) {
                return MainApp.instance?.dataStore?.select(Feat::class)
                        ?.where(Feat::prerequisite_feats.like(result.first().name)
                                .and(Feat::name.notLike(result.first().name)).
                                and(Feat::type.eq(result.first().type)))?.get() as Result<Feat>
            } else {
                return emptyResult()
            }
        }
        if(featListHandler.isParentMode()) {
            val result = MainApp.instance?.dataStore?.select(Feat::class)?.where(Feat::id.eq(featListHandler.featId()))?.get() as Result<Feat>
            if(result.count() > 0) {
                val namesList = result.first().prerequisite_feats.split(", ")
                var namesResult : ReactiveResult<Feat>? = null
                for (name in namesList) {
                    if(namesResult == null) {
                        namesResult = MainApp.instance?.dataStore?.select(Feat::class)
                                ?.where(Feat::name.eq(name).and(Feat::type.eq(result.first().type)))
                                ?.get()
                    } else {
                        val nextResult = MainApp.instance?.dataStore?.select(Feat::class)
                                ?.where(Feat::name.eq(name).and(Feat::type.eq(result.first().type)))
                                ?.get()
                        namesResult.plus(nextResult?.first())
                    }
                }
                return namesResult as Result<Feat>
            } else {
                return emptyResult()
            }
        }
        return MainApp.instance?.dataStore?.select(Feat::class)?.get() as Result<Feat>
    }

    private fun emptyResult(): Result<Feat> =
            MainApp.instance?.dataStore?.select(Feat::class)?.where(Feat::id.eq(-1))?.get() as Result<Feat> //TODO: return empty result explicitly

}