package com.syjgin.pathfinderfeats.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.syjgin.pathfinderfeats.model.Feat

/**
 * Created by maksimovoleg on 17/08/2017.
 */
class FeatListItemHolder(view : View) : RecyclerView.ViewHolder(view) {
    var titleView : TextView? = null
    companion object {
        fun bind(holder : FeatListItemHolder?, feat : Feat?) {
            holder?.titleView?.text = feat?.name
        }
    }
}