package com.syjgin.pathfinderfeats.persistentData

import android.content.Context
import android.util.Log
import com.syjgin.pathfinderfeats.R
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.sqlitex.SqlitexDatabaseSource
import io.requery.meta.EntityModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.logging.Logger
import javax.sql.CommonDataSource

/**
 * Created by user1 on 04.08.17.
 */

class MainDatabaseSource(context: Context?, model: EntityModel?, version: Int) : SqlitexDatabaseSource(context, model, version), CommonDataSource {

    private var logWriter : PrintWriter? = null
    private var loginTimeout : Int = 0
    private val parentLogger : Logger = Logger.getAnonymousLogger()

    override fun getParentLogger(): Logger = parentLogger

    override fun setLoginTimeout(p0: Int) {
        loginTimeout = p0
    }

    override fun getLogWriter(): PrintWriter = logWriter!!

    override fun getLoginTimeout(): Int = loginTimeout

    override fun setLogWriter(p0: PrintWriter?) {
        logWriter = p0
    }

    init {
        if (!(DataStorage.isDbInitialized(context))) {
            val db = writableDatabase
            tryUnpackDump(context, db)
            DataStorage.setDbInitialized(context)
        }
    }

    private fun tryUnpackDump(context: Context?, db: SQLiteDatabase) {
        val inputStream = context?.resources?.openRawResource(R.raw.dump)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        var end = false
        while (!end) {
            line = reader.readLine()
            if (line == null) {
                end = true
            } else {
                try {
                    db.execSQL(line)
                } catch (e: Exception) {
                    Log.d("DB_INIT_ERROR", e.message)
                }
            }
        }
    }
}