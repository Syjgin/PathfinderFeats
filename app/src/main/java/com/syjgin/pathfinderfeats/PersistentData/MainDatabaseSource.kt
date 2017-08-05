package com.syjgin.pathfinderfeats

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.requery.android.sqlite.DatabaseSource
import io.requery.android.sqlitex.SqlitexDatabaseSource
import io.requery.meta.EntityModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter

/**
 * Created by user1 on 04.08.17.
 */

class MainDatabaseSource(context: Context?, model: EntityModel?, version: Int) : SqlitexDatabaseSource(context, model, version) {
    init {
        if (!(DataStorage.isDbInitialized(context))) {
            val db = writableDatabase
            val inputStream = context?.resources?.openRawResource(R.raw.dump)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val builder = StringBuilder()
            var line : String?
            var end = false
            while (!end) {
                line = reader.readLine()
                if(line == null) {
                    end = true
                } else {
                    builder.append(line)
                }
            }
            db.execSQL(builder.toString())
            onCreate(db)
            DataStorage.setDbInitialized(context)
        }
    }
}