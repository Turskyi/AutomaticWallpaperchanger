package ua.turskyi.automaticwallpaperchanger.data

import androidx.lifecycle.MutableLiveData
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper

class DataController {

    companion object {
        private var INSTANCE: DataController? = null

        fun getInstance(): DataController {
            INSTANCE ?: run {
                INSTANCE = DataController()
            }
            return INSTANCE!!
        }
    }

    val pictureFromUi  = MutableLiveData<Wallpaper>()
}