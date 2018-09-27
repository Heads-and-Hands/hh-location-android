package ru.handh.hhlocation.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(val context: Context) {

    companion object {
        const val PREF_FILE_NAME = "navi_pref_file"

        const val KEY_ALREADY_REGISTERED = "already_registered"
    }

    private val pref: SharedPreferences

    init {
        pref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    // ===========================================================================================================================
    // Properties
    // ===========================================================================================================================

    var alreadyRegistered: Boolean
        get() = pref.getBoolean(KEY_ALREADY_REGISTERED, false)
        set(value) {
            pref.edit().putBoolean(KEY_ALREADY_REGISTERED, value).apply()
        }
}