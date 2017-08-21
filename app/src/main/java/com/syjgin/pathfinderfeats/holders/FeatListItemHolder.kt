package com.syjgin.pathfinderfeats.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.syjgin.pathfinderfeats.interfaces.FeatListHandler
import com.syjgin.pathfinderfeats.model.Feat

/**
 * Created by maksimovoleg on 17/08/2017.
 */
class FeatListItemHolder(view : View) : RecyclerView.ViewHolder(view) {
    var titleView : TextView? = null
    var childIcon : ImageView? = null
    var parentIcon : ImageView? = null
    companion object {
        fun bind(holder : FeatListItemHolder?, feat : Feat, handler: FeatListHandler) {
            holder?.titleView?.text = feat.name
            holder?.childIcon?.setOnClickListener {
                handler.openChildFeat(feat)
            }
            holder?.parentIcon?.setOnClickListener {
                handler.openParentFeat(feat)
            }
        }
    }
}