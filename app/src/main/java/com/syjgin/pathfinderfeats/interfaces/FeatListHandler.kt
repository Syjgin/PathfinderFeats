package com.syjgin.pathfinderfeats.interfaces

import com.syjgin.pathfinderfeats.model.Feat

/**
 * Created by maksimovoleg on 21/08/2017.
 */
interface FeatListHandler {
    fun openParentFeat(feat : Feat)
    fun openChildFeat(feat : Feat)
    fun openFeatDetails(feat : Feat)
    fun isParentMode() : Boolean
    fun isChildMode() : Boolean
    fun featId() : Int?
}