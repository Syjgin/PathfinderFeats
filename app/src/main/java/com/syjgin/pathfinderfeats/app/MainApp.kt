package com.syjgin.pathfinderfeats.app

import android.app.Application
import com.syjgin.pathfinderfeats.persistentData.MainDatabaseSource
import io.requery.Persistable
import io.requery.meta.EntityModelBuilder
import io.requery.sql.KotlinEntityDataStore
import io.requery.reactivex.KotlinReactiveEntityStore

/**
 * Created by user1 on 05.08.17.
 */
class MainApp : Application() {

    val dataStore : KotlinReactiveEntityStore<Persistable> by lazy {
        val model = EntityModelBuilder("main").build()
        val source = MainDatabaseSource(this, model, 1)
        KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }

    override fun onCreate() {
        super.onCreate()

    }
}