package ua.turskyi.automaticwallpaperchanger.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase

abstract class BaseActivity(layout: Int): AppCompatActivity(layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        PicturesDataBase.destroyInstance()
        super.onDestroy()
    }
}