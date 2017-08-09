package com.syjgin.pathfinderfeats.App

import android.app.Application
import com.syjgin.pathfinderfeats.PersistentData.MainDatabaseSource
import io.requery.Persistable
import io.requery.meta.EntityModelBuilder
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.reactivex.KotlinReactiveEntityStore

/**
 * Created by user1 on 05.08.17.
 */
class MainApp : Application() {
    companion object {
        var dataStore : KotlinReactiveEntityStore<Persistable>? = null
    }

    override fun onCreate() {
        super.onCreate()
        val model = EntityModelBuilder("main").build()
        val source = MainDatabaseSource(this, model, 1)
        dataStore =  KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }
}