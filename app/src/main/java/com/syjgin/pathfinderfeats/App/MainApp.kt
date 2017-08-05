package com.syjgin.pathfinderfeats.App

import android.app.Application
import com.syjgin.pathfinderfeats.PersistentData.MainDatabaseSource
import io.requery.Persistable
import io.requery.meta.EntityModelBuilder
import io.requery.reactivex.ReactiveEntityStore
import io.requery.reactivex.ReactiveSupport
import io.requery.sql.EntityDataStore

/**
 * Created by user1 on 05.08.17.
 */
class App : Application() {
    companion object {
        var dataStore : ReactiveEntityStore<Persistable>? = null
    }
    init {
        val model = EntityModelBuilder("main").build()
        val source = MainDatabaseSource(this, model, 1)
        val configuration = source.configuration
        dataStore = ReactiveSupport.toReactiveStore(EntityDataStore<Persistable>(configuration))
    }
}