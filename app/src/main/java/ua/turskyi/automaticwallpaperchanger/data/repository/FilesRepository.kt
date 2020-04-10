package ua.turskyi.automaticwallpaperchanger.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images
import ua.turskyi.automaticwallpaperchanger.model.PictureModel

class FilesRepository {

    fun getDataOfImageList(from: Int, to: Int, context: Context): List<PictureModel> {

        val listOfImages = mutableListOf<PictureModel>()
        val columns = arrayOf(Images.Media._ID)

        val orderBy =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) Images.Media.DATE_TAKEN
            else Images.Media._ID

        /** This cursor will hold the result of the query
        and put all data in Cursor by sorting in descending order */
        val cursor = context.contentResolver.query(
            Images.Media.EXTERNAL_CONTENT_URI,
            columns, null, null, "$orderBy DESC"
        )
        cursor?.let {
            val columnIndexID: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            for (i in from until to + from) {
                while (it.moveToNext() && i < it.columnCount) {
                val id = it.getLong(columnIndexID)
                    val uriImage =
                        Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "" + id)
                val galleryPicture = PictureModel(uriImage)
                        listOfImages.add(galleryPicture)
                }
            }
            cursor.close()
        }
        return listOfImages
    }
}