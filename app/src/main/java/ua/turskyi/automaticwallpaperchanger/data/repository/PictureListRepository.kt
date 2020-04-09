package ua.turskyi.automaticwallpaperchanger.data.repository

import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureUri

class PictureListRepository(private val database: PicturesDataBase) {

     fun addPicturesToDB(picturesUri: MutableList<PictureUri>) = Runnable {
        val picturesLocal = mutableListOf<PictureLocal>()
         for ((id, picture) in picturesUri.withIndex()){
            val uriPath: String = picture.uri.toString()
            picturesLocal.add(PictureLocal(id, uriPath) )
         }
        database.pictureDAO().insertAll(picturesLocal)
    }
}
