package ua.turskyi.automaticwallpaperchanger.data

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    companion object {
        const val PREFS_FILENAME = "ua.turskyi.automaticwallpaperchanger.prefs"
        const val IS_STARTED = "background_color"
        const val NEXT_PIC = "next_pic"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    var changingStarted: Boolean
        get() = prefs.getBoolean(IS_STARTED, false)
        set(value) = prefs.edit().putBoolean(IS_STARTED, value).apply()

    var nextPic: Int
        get() = prefs.getInt(NEXT_PIC, 0)
        set(value) = prefs.edit().putInt(NEXT_PIC, value).apply()
}