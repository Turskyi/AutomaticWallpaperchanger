package ua.turskyi.automaticwallpaperchanger.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class PictureLocal (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_ID) var id: Long,
    @ColumnInfo(name = COLUMN_DATA) val pictureData: String
) {
    companion object {
        const val TABLE_NAME = "Pictures"
        const val COLUMN_ID = "id"
        const val COLUMN_DATA = "data"
    }
}