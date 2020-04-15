package ua.turskyi.automaticwallpaperchanger.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import ua.turskyi.automaticwallpaperchanger.data.room.model.PictureLocal
import ua.turskyi.automaticwallpaperchanger.model.PictureModel

fun Uri.mapUriToBitMap(context: Context): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }

fun Uri.mapUriToString(): String = this.toString()
fun String.mapStringToUri(): Uri = Uri.parse(this)
fun PictureLocal.mapEntityToModel() = PictureModel(id = id, uri = pictureData.mapStringToUri())

fun PictureModel.mapModelToEntity() = PictureLocal(id = id, pictureData = uri.mapUriToString())

fun List<PictureLocal>.mapEntityListToModelList() = mapTo(
    mutableListOf(), {
        it.mapEntityToModel()
    }
)