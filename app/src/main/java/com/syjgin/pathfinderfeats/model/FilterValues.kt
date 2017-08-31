package com.syjgin.pathfinderfeats.model

import java.io.Serializable

/**
 * Created by maksimovoleg on 30/08/2017.
 */
data class FilterValues(var panache : Boolean = false, var trick : Boolean = false,
                        var blood_hex : Boolean = false, var shield_mastery : Boolean = false,
                        var armor_mastery : Boolean = false, var item_mastery : Boolean = false,
                        var weapon_mastery : Boolean = false, var stare : Boolean = false,
                        var esoteric : Boolean = false, var targeting : Boolean = false,
                        var betrayal : Boolean = false, var multiples: Boolean = false,
                        var companion_familiar : Boolean = false, var performance : Boolean = false,
                        var style : Boolean = false, var grit : Boolean = false, var critical : Boolean = false,
                        var teamwork : Boolean = false, var sourceFilter : String = "",
                        var raceFilter : String = "", var skillFilter : String = "") : Serializable {
}