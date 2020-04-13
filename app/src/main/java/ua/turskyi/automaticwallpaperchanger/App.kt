package ua.turskyi.automaticwallpaperchanger

import android.app.Application
import ua.turskyi.automaticwallpaperchanger.data.Prefs

val prefs: Prefs by lazy {
    App.prefs!!
}

class App : Application(){
    companion object {
        var prefs: Prefs? = null
        lateinit var instance: App
    }


    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
        instance = this
    }
}