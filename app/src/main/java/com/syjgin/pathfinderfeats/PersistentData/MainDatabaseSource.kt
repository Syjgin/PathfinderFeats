package com.syjgin.pathfinderfeats.PersistentData

import android.content.Context
import com.syjgin.pathfinderfeats.R
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

    override fun getParentLogger(): Logger {
        return parentLogger
    }

    override fun setLoginTimeout(p0: Int) {
        loginTimeout = p0
    }

    override fun getLogWriter(): PrintWriter {
        return logWriter!!
    }

    override fun getLoginTimeout(): Int {
        return loginTimeout
    }

    override fun setLogWriter(p0: PrintWriter?) {
        logWriter = p0
    }

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