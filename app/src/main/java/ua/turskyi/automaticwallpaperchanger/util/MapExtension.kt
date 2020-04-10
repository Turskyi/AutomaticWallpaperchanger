package ua.turskyi.automaticwallpaperchanger.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureModel

fun MutableList<PictureModel>.mapModelListToEntityListWithIndex(): MutableList<PictureLocal> {
    val picturesLocal = mutableListOf<PictureLocal>()
    for ((id, picture) in this.withIndex()){
        val uriPath: String = picture.uri.toString()
        picturesLocal.add(PictureLocal(id, uriPath) )
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
fun String.mapStringToUri(): Uri = Uri.parse(this)
fun PictureLocal.mapEntityToModel() = PictureModel(uri = pictureData.mapStringToUri())

fun List<PictureLocal>.mapEntityListToModelList() = mapTo(
    mutableListOf(), {
        it.mapEntityToModel()
    }
)