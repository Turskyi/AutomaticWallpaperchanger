package ua.turskyi.automaticwallpaperchanger.ui.pictures.viewmodel

import android.app.Application
import androidx.paging.PagedList
import ua.turskyi.automaticwallpaperchanger.common.BaseViewModel
import ua.turskyi.automaticwallpaperchanger.data.DataController
import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.model.PictureModel
import ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter.MainThreadExecutor
import ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter.PicturesPositionalDataSource
import ua.turskyi.automaticwallpaperchanger.util.mapEntityListToModelList
import java.util.concurrent.Executors

class PicturesViewModel(application: Application) : BaseViewModel(application) {

    private val database = PicturesDataBase.getInstance(application)

    var pagedList: PagedList<PictureModel>

    init {
        val dataSource = PicturesPositionalDataSource(application)

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        pagedList = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
    }

    fun addPictureToDB(picture: PictureModel) {
        val task = Runnable {
            val modelPictures = database?.picturesDAO()?.getLocalPictures()?.mapEntityListToModelList()
           modelPictures?.plusAssign(picture)
            DataController.getInstance().picturesFromUi.postValue(modelPictures)
        }
        val thread = Thread(task)
        thread.start()
    }
}