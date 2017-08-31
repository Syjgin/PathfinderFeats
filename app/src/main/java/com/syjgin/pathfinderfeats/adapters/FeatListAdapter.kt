package com.syjgin.pathfinderfeats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
                        if(condition == null) {
                            condition = query?.where(Feat::race_name.like("%$raceFilter%"));
                        } else {
                            condition = condition.and(Feat::race_name.like("%$raceFilter%"))
                        }
                    }
                    if(filterValues.skillFilter.isNotEmpty()) {
                        val skillFilter = filterValues.skillFilter
                        if(condition == null) {
                            condition = query?.where(Feat::prerequisite_skills.like("%$skillFilter%"));
                        } else {
                            condition = condition.and(Feat::prerequisite_skills.like("%$skillFilter%"))
                        }
                    }
                    if(filterValues.panache) {
                        if(condition == null) {
                            condition = query?.where(Feat::panache.eq(true));
                        } else {
                            condition = condition.and(Feat::panache.eq(true))
                        }
                    }
                    if(filterValues.trick) {
                        if(condition == null) {
                            condition = query?.where(Feat::trick.eq(true));
                        } else {
                            condition = condition.and(Feat::trick.eq(true))
                        }
                    }
                    if(filterValues.blood_hex) {
                        if(condition == null) {
                            condition = query?.where(Feat::blood_hex.eq(true));
                        } else {
                            condition = condition.and(Feat::blood_hex.eq(true))
                        }
                    }
                    if(filterValues.shield_mastery) {
                        if(condition == null) {
                            condition = query?.where(Feat::shield_mastery.eq(true));
                        } else {
                            condition = condition.and(Feat::shield_mastery.eq(true))
                        }
                    }
                    if(filterValues.armor_mastery) {
                        if(condition == null) {
                            condition = query?.where(Feat::armor_mastery.eq(true));
                        } else {
                            condition = condition.and(Feat::armor_mastery.eq(true))
                        }
                    }
                    if(filterValues.item_mastery) {
                        if(condition == null) {
                            condition = query?.where(Feat::item_mastery.eq(true));
                        } else {
                            condition = condition.and(Feat::item_mastery.eq(true))
                        }
                    }
                    if(filterValues.weapon_mastery) {
                        if(condition == null) {
                            condition = query?.where(Feat::weapon_mastery.eq(true));
                        } else {
                            condition = condition.and(Feat::weapon_mastery.eq(true))
                        }
                    }
                    if(filterValues.stare) {
                        if(condition == null) {
                            condition = query?.where(Feat::stare.eq(true));
                        } else {
                            condition = condition.and(Feat::stare.eq(true))
                        }
                    }
                    if(filterValues.esoteric) {
                        if(condition == null) {
                            condition = query?.where(Feat::esoteric.eq(true));
                        } else {
                            condition = condition.and(Feat::esoteric.eq(true))
                        }
                    }
                    if(filterValues.targeting) {
                        if(condition == null) {
                            condition = query?.where(Feat::targeting.eq(true));
                        } else {
                            condition = condition.and(Feat::targeting.eq(true))
                        }
                    }
                    if(filterValues.betrayal) {
                        if(condition == null) {
                            condition = query?.where(Feat::betrayal.eq(true));
                        } else {
                            condition = condition.and(Feat::betrayal.eq(true))
                        }
                    }
                    if(filterValues.multiples) {
                        if(condition == null) {
                            condition = query?.where(Feat::multiples.eq(true));
                        } else {
                            condition = condition.and(Feat::multiples.eq(true))
                        }
                    }
                    if(filterValues.companion_familiar) {
                        if(condition == null) {
                            condition = query?.where(Feat::companion_familiar.eq(true));
                        } else {
                            condition = condition.and(Feat::companion_familiar.eq(true))
                        }
                    }
                    if(filterValues.performance) {
                        if(condition == null) {
                            condition = query?.where(Feat::performance.eq(true));
                        } else {
                            condition = condition.and(Feat::performance.eq(true))
                        }
                    }
                    if(filterValues.style) {
                        if(condition == null) {
                            condition = query?.where(Feat::style.eq(true));
                        } else {
                            condition = condition.and(Feat::style.eq(true))
                        }
                    }
                    if(filterValues.grit) {
                        if(condition == null) {
                            condition = query?.where(Feat::grit.eq(true));
                        } else {
                            condition = condition.and(Feat::grit.eq(true))
                        }
                    }
                    if(filterValues.critical) {
                        if(condition == null) {
                            condition = query?.where(Feat::critical.eq(true));
                        } else {
                            condition = condition.and(Feat::critical.eq(true))
                        }
                    }
                    if(filterValues.teamwork) {
                        if(condition == null) {
                            condition = query?.where(Feat::teamwork.eq(true));
                        } else {
                            condition = condition.and(Feat::teamwork.eq(true))
                        }
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

    private fun emptyResult(): Result<Feat> {
        featListHandler.onResult(true)
        return MainApp.instance?.dataStore?.select(Feat::class)?.where(Feat::id.eq(-1))
                ?.get() as Result<Feat> //TODO: return empty result explicitly
    }

}