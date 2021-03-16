package ua.turskyi.automaticwallpaperchanger.data

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    companion object {
        const val PREFS_FILENAME = "ua.turskyi.automaticwallpaperchanger.prefs"
        const val IS_STARTED = "is_changing_started"
        const val NEXT_PIC = "next_pic"
        const val IS_UPGRADED = "IS_UPGRADED"
        private const val IS_ON_UNLOCK_ENABLED = "IS_ON_UNLOCK_ENABLED"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var isChangingStarted: Boolean
        get() = prefs.getBoolean(IS_STARTED, false)
        set(value) = prefs.edit().putBoolean(IS_STARTED, value).apply()

    var isOnUnlockEnabled: Boolean
        get() = prefs.getBoolean(IS_ON_UNLOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(IS_ON_UNLOCK_ENABLED, value).apply()

    var nextPic: Int
        get() = prefs.getInt(NEXT_PIC, 0)
        set(value) = prefs.edit().putInt(NEXT_PIC, value).apply()

    var isUpgraded: Boolean
//        get() = prefs.getBoolean(IS_UPGRADED, false)
//        TODO: uncomment above delete bellow
        get() = prefs.getBoolean(IS_UPGRADED, true)
        set(value) = prefs.edit().putBoolean(IS_UPGRADED, value).apply()
}