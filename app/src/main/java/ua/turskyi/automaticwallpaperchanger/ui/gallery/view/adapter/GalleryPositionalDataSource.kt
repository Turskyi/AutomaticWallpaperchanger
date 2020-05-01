package ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter

import android.content.Context
import androidx.paging.PositionalDataSource
import ua.turskyi.automaticwallpaperchanger.data.repository.PicturesRepository
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper

class GalleryPositionalDataSource(private val context: Context) : PositionalDataSource<Wallpaper>() {

    private val repository = PicturesRepository()

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<Wallpaper>
    ) {
        val list = repository.getImagesFromDevice(
            params.requestedStartPosition,
            params.requestedLoadSize,
            context
        )
        callback.onResult(list, 0)
    }

    override fun loadRange(
        params: LoadRangeParams,
        callback: LoadRangeCallback<Wallpaper>
    ) {
        val list = repository.getImagesFromDevice(
            params.startPosition,
            params.loadSize,
            context
        )
        callback.onResult(list)
    }
}