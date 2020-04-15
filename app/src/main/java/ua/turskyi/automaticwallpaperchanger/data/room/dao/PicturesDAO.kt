package ua.turskyi.automaticwallpaperchanger.data.room.dao

import androidx.room.*
import io.reactivex.Flowable
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.COLUMN_ID
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.TABLE_NAME

@Dao
interface PicturesDAO {

    @Query("SELECT COUNT(${COLUMN_ID}) FROM $TABLE_NAME")
    fun getTotalNum(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallpaper: PictureLocal)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getLocalPicturesRx(): Flowable<MutableList<PictureLocal>>

    @Query("SELECT * FROM $TABLE_NAME")
    fun getLocalPictures(): MutableList<PictureLocal>

    @Delete
    fun delete(pictureLocal: PictureLocal)
}