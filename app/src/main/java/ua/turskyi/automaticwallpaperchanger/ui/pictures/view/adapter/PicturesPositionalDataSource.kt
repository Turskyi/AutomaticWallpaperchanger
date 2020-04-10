package ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter

import android.content.Context
import android.util.Log
import androidx.paging.PositionalDataSource
import ua.turskyi.automaticwallpaperchanger.data.Constants.TAG_DATA_SOURCE
import ua.turskyi.automaticwallpaperchanger.data.repository.FilesRepository
import ua.turskyi.automaticwallpaperchanger.model.PictureModel

class PicturesPositionalDataSource(private val context: Context) :
    PositionalDataSource<PictureModel>() {

    private val repository = FilesRepository()

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<PictureModel>
    ) {
        Log.d(
            TAG_DATA_SOURCE, "start = ${params.requestedStartPosition}, " +
                    "load size =  ${params.requestedLoadSize}"
        )
        val list = repository.getDataOfImageList(
            params.requestedStartPosition,
            params.requestedLoadSize,
            context
        )
        callback.onResult(list, 0)
    }

    override fun loadRange(
        params: LoadRangeParams,
        callback: LoadRangeCallback<PictureModel>
    ) {
        Log.d(
            TAG_DATA_SOURCE, "start = ${params.startPosition}," +
                    " load size =  ${params.loadSize}"
        )
        val list = repository.getDataOfImageList(
            params.startPosition,
            params.loadSize,
            context
        )
        callback.onResult(list)
    }
}