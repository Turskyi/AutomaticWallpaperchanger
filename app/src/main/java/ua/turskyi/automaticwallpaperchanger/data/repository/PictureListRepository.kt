package ua.turskyi.automaticwallpaperchanger.data.repository

import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.model.PictureModel
import ua.turskyi.automaticwallpaperchanger.util.mapModelToEntity

class PictureListRepository(private val database: PicturesDataBase) {

    fun addPictureToDB(picturesModel: PictureModel) = Runnable {
        database.picturesDAO().insert(picturesModel.mapModelToEntity())
    }

    fun deletePicture(pictureModel: PictureModel) = Runnable {
        database.picturesDAO().delete(pictureModel.mapModelToEntity())
    }
}
