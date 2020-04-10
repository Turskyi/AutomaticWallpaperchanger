package ua.turskyi.automaticwallpaperchanger.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.COLUMN_ID
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.TABLE_NAME

@Dao
interface PicturesDAO {

    @Query("SELECT COUNT(${COLUMN_ID}) FROM $TABLE_NAME")
    fun getTotalNum(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pictures: List<PictureLocal>?)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getLocalPicturesRx(): Flowable<MutableList<PictureLocal>>

    @Query("SELECT * FROM $TABLE_NAME")
    fun getLocalPictures(): MutableList<PictureLocal>
}