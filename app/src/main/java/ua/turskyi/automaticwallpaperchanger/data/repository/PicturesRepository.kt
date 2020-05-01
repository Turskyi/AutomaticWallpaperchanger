package ua.turskyi.automaticwallpaperchanger.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Images
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper

class PicturesRepository {

    fun getImagesFromDevice(from: Int, to: Int, context: Context): List<Wallpaper> {

        val listOfImages = mutableListOf<Wallpaper>()

        val orderBy =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) Images.Media.DATE_TAKEN
            else Images.Media._ID

        /** This cursor will hold the result of the query
        and put all data in Cursor by sorting in descending order */
        val cursor = context.contentResolver.query(
            Images.Media.EXTERNAL_CONTENT_URI,null, null, null, "$orderBy DESC"
        )
        cursor?.let {
            val columnIndexID: Int = it.getColumnIndexOrThrow(Images.Media._ID)
            for (i in from until to + from) {
                while (it.moveToNext() && i < it.columnCount) {
                val id = it.getLong(columnIndexID)
                    val uriImage = Uri.withAppendedPath(
                            Images.Media.EXTERNAL_CONTENT_URI,
                    "" + id)
                    val galleryPicture = Wallpaper(id, uriImage)
                        listOfImages.add(galleryPicture)
                }
            }
            cursor.close()
        }
        return listOfImages
    }
}