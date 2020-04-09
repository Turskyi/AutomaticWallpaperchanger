package ua.turskyi.automaticwallpaperchanger.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.COLUMN_URI
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME , indices = [Index(value = [COLUMN_URI], unique = true)])
data class PictureLocal (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_ID) var id: Int,
    @ColumnInfo(name = COLUMN_URI) val uriPath: String
) {
    companion object {
        const val TABLE_NAME = "Pictures"
        const val COLUMN_ID = "id"
        const val COLUMN_URI = "uriPath"
    }
}