package ua.turskyi.automaticwallpaperchanger.ui.gallery.viewmodel

import android.app.Application
import androidx.paging.PagedList
import ua.turskyi.automaticwallpaperchanger.common.BaseViewModel
import ua.turskyi.automaticwallpaperchanger.data.DataController
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper
import ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter.GalleryPositionalDataSource
import ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter.MainThreadExecutor
import java.util.concurrent.Executors

class GalleryViewModel(application: Application) : BaseViewModel(application) {
    companion object {
        const val LOG_PIC = "log_pic"
    }

    var pagedList: PagedList<Wallpaper>

    init {
        val dataSource = GalleryPositionalDataSource(application)

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(16)
            .setPageSize(8)
            .setPrefetchDistance(4)
            .build()

        pagedList = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
    }

    fun addPictureToDB(wallpaper: Wallpaper) {
        val task = Runnable {
            DataController.getInstance().pictureFromUi.postValue(wallpaper)
        }
        val thread = Thread(task)
        thread.start()
    }
}