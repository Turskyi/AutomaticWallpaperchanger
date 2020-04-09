package ua.turskyi.automaticwallpaperchanger.data.room.dao

import android.graphics.Picture
import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Flowable
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal

@Dao
interface PictureDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(pictures: List<PictureLocal>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(profile: PictureLocal)

    @Query("SELECT * FROM ${PictureLocal.TABLE_NAME}")
    fun getRxLiveAll(): Flowable<MutableList<PictureLocal>>

    /* using in paging adapters */
    @Query("SELECT * FROM ${PictureLocal.TABLE_NAME} LIMIT :limit OFFSET :offset")
    fun getPicturesByRange(limit: Int, offset: Int): Flowable<List<PictureLocal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(country: PictureLocal)

    /* The rest is for use in future */

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(countries: List<PictureLocal>?)

    @Query("DELETE FROM ${PictureLocal.TABLE_NAME}")
    fun deleteAll()

    @Query("DELETE FROM ${PictureLocal.TABLE_NAME} WHERE ${PictureLocal.COLUMN_ID} = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM ${PictureLocal.TABLE_NAME}")
    fun getLiveAll(): LiveData<List<PictureLocal>>
}