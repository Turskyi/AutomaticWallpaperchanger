package ua.turskyi.automaticwallpaperchanger.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ua.turskyi.automaticwallpaperchanger.data.room.dao.PicturesDAO
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal

@Database(entities = [PictureLocal::class], version = 1, exportSchema = false)
abstract class PicturesDataBase : RoomDatabase() {

    abstract fun picturesDAO(): PicturesDAO

    companion object {

        private var INSTANCE: PicturesDataBase? = null

        fun getInstance(context: Context): PicturesDataBase? {
            synchronized(PicturesDataBase::class) {
                INSTANCE ?: run {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PicturesDataBase::class.java,
                        "pictures.db"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}