package com.syjgin.pathfinderfeats.Models

import io.requery.Entity
import io.requery.Index
import io.requery.Key

/**
 * Created by user1 on 05.08.17.
 */

@Entity
abstract class Feat {

    @Key
    abstract fun getId() : Int

    abstract fun getName() : String

    abstract fun getType() : String

    abstract fun getDescription() : String

    abstract fun getPrerequisites() : String

    @Index("prerequisite_feats")
    abstract fun getPrerequisitesFeats() : String

    abstract fun getBenefit() : String

    abstract fun getNormal() : String

    abstract fun getSpecial() : String

    abstract fun getSource() : String

    abstract fun getFullText() : String

    abstract fun getTeamwork() : Boolean

    abstract fun getCritical() : Boolean

    abstract fun getGrit() : Boolean

    abstract fun getStyle() : Boolean

    abstract fun getPerformance() : Boolean

    abstract fun getRacial() : Boolean

    @Index("companion_familiar")
    abstract fun getCompanionFamiliar() : Boolean

    abstract fun getRaceName() : String

    abstract fun getNote() : String

    abstract fun getGoal() : String

    @Index("completion_benefit")
    abstract fun getCompletionBenefit() : String

    abstract fun getMultiples() : Boolean

    @Index("suggested_traits")
    abstract fun getSuggestedTraits() : String

    @Index("prerequisite_skills")
    abstract fun getPrerequisiteSkills() : String

    abstract fun getPanache() : Boolean

    abstract fun getBetrayal() : Boolean

    abstract fun getTargeting() : Boolean

    abstract fun getEsoteric() : Boolean

    abstract fun getStare() : Boolean

    @Index("weapon_mastery")
    abstract fun getWeaponMastery() : Boolean

    @Index("item_mastery")
    abstract fun getItemMastery() : Boolean

    @Index("armor_mastery")
    abstract fun getArmorMastery() : Boolean

    @Index("shield_mastery")
    abstract fun getShieldMastery() : Boolean

    @Index("blood_hex")
    abstract fun getBloodHex() : Boolean

    abstract fun getTrick() : Boolean
}