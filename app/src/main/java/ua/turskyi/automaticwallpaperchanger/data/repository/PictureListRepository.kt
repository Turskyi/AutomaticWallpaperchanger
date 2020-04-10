package ua.turskyi.automaticwallpaperchanger.data.repository

import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.model.PictureModel
import ua.turskyi.automaticwallpaperchanger.util.mapModelListToEntityListWithIndex

class PictureListRepository(private val database: PicturesDataBase) {

     fun addPicturesToDB(picturesModel: MutableList<PictureModel>) = Runnable {
        database.picturesDAO().insertAll(picturesModel.mapModelListToEntityListWithIndex())
    }
}
