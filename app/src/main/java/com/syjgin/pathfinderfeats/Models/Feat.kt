package com.syjgin.pathfinderfeats.Models

import io.requery.Entity
import io.requery.Index
import io.requery.Key
import io.requery.Persistable

/**
 * Created by user1 on 05.08.17.
 */

@Entity
interface Feat : Persistable {

    @get : Key
    var id : Int

    var name : String

    var type : String

    var description : String

    var prerequisites : String

    var prerequisite_feats : String

    var benefit : String

    var normal : String

    var special : String

    var source : String

    var fullText : String

    var teamwork : Boolean

    var critical : Boolean

    var grit : Boolean

    var style : Boolean

    var performance : Boolean

    var racial : Boolean

    var companion_familiar : Boolean

    var raceName : String

    var note : String

    var goal : String

    var completion_benefit : String

    var Multiples : Boolean

    var suggested_traits : String

    var prerequisite_skills : String

    var panache : Boolean

    var betrayal : Boolean

    var targeting : Boolean

    var esoteric : Boolean

    var stare : Boolean

    var weapon_mastery : Boolean

    var item_mastery : Boolean

    var armor_mastery : Boolean

    var shield_mastery : Boolean

    var blood_hex : Boolean

    var trick : Boolean
}