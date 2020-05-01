package ua.turskyi.automaticwallpaperchanger.data.repository

import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper
import ua.turskyi.automaticwallpaperchanger.util.mapModelToEntity

class WallpaperListRepository(private val database: PicturesDataBase) {

    fun addPictureToDB(pictures: Wallpaper) = Runnable {
        database.picturesDAO().insert(pictures.mapModelToEntity())
    }

    fun deletePicture(wallpaper: Wallpaper) = Runnable {
        database.picturesDAO().delete(wallpaper.mapModelToEntity())
    }
}
