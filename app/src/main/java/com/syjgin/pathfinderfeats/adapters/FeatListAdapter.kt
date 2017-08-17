package com.syjgin.pathfinderfeats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.syjgin.pathfinderfeats.R
import com.syjgin.pathfinderfeats.app.MainApp
import com.syjgin.pathfinderfeats.holders.FeatListItemHolder
import com.syjgin.pathfinderfeats.model.Feat
import com.syjgin.pathfinderfeats.model.Models
import io.requery.android.QueryRecyclerAdapter
import io.requery.query.Result

/**
 * Created by maksimovoleg on 17/08/2017.
 */
class FeatListAdapter : QueryRecyclerAdapter<Feat, FeatListItemHolder>(Models.DEFAULT, Feat::class.java) {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeatListItemHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.item_feat_list, parent, false)
        val holder = FeatListItemHolder(view)
        holder.titleView = view.findViewById(R.id.featTitle) as TextView?
        return holder
    }

    override fun onBindViewHolder(item: Feat?, holder: FeatListItemHolder?, position: Int) {
        FeatListItemHolder.bind(holder, item)
    }

    override fun performQuery(): Result<Feat> =
            MainApp.instance?.dataStore?.select(Feat::class)?.get() as Result<Feat>
}