package ua.turskyi.automaticwallpaperchanger.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import ua.turskyi.automaticwallpaperchanger.data.Constants.LOGS
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureModel
import java.io.ByteArrayOutputStream
import java.util.*

fun Bitmap.mapBitMapToString(): String {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 10, baos)
    this.compress(Bitmap.CompressFormat.JPEG, 10, baos)
    this.compress(Bitmap.CompressFormat.WEBP, 10, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

/**
 * @return bitmap (from given string)
 */
fun String.mapStringToBitMap(): Bitmap? {
    return try {
        val encodeByte = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    } catch (e: Exception) {
        Log.d(LOGS,"to bitmap ${e.message}")
        e.message
        null
    }
}

fun ArrayList<PictureModel>.mapModelListToEntityListWithIndex(): ArrayList<PictureLocal> {
    val picturesLocal = ArrayList<PictureLocal>()
    for ((id, picture) in this.withIndex()){
        val pictureData: String = picture.bitmap.mapBitMapToString()
        picturesLocal.add(PictureLocal(id, pictureData) )
    }
    return  picturesLocal
}

fun Uri.mapUriToBitMap(context: Context): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }

fun PictureLocal.mapEntityToModel() = pictureData.mapStringToBitMap()?.let { PictureModel(bitmap = it) }

fun List<PictureLocal>.mapEntityListToModelList() = mapTo(
    mutableListOf(), {
        it.mapEntityToModel()
    }
)