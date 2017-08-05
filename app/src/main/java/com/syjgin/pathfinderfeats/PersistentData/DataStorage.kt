package com.syjgin.pathfinderfeats.PersistentData

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Created by user1 on 05.08.17.
 */
object DataStorage {

    const val appName = "PathfinderFeats"
    const val isDbInitialized = "isDbInitialized"

    private fun createEditor(context : Context?) : SharedPreferences.Editor? {
        val prefs = context?.getSharedPreferences(appName, Context.MODE_PRIVATE)
        return prefs?.edit()
    }

    private fun createPrefsReader(context : Context?) : SharedPreferences? {
        val prefs = context?.getSharedPreferences(appName, Context.MODE_PRIVATE)
        return prefs
    }

    fun isDbInitialized(context: Context?) : Boolean {
        val reader = createPrefsReader(context)
        val result = reader?.getBoolean(isDbInitialized, false) ?: return false
        return !!result
    }

    fun setDbInitialized(context: Context?) {
        val editor = createEditor(context)
        editor?.putBoolean(isDbInitialized, true)
        editor?.commit()
    }
}