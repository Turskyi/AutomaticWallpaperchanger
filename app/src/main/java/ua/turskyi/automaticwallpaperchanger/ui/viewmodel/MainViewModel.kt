package ua.turskyi.automaticwallpaperchanger.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import ua.turskyi.automaticwallpaperchanger.common.BaseViewModel
import ua.turskyi.automaticwallpaperchanger.data.repository.WallpaperListRepository
import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper
import ua.turskyi.automaticwallpaperchanger.util.mapEntityListToModelList

class MainViewModel(application: Application) : BaseViewModel(application) {
    val mSkuId = "sku_id_1"
    private val database = PicturesDataBase.getInstance(application)
    private val picturesRepository = database?.let { WallpaperListRepository(it) }
    private val _picturesFromDb = MutableLiveData<MutableList<Wallpaper>>()
    val picturesFromDB: MutableLiveData<MutableList<Wallpaper>>
        get() = _picturesFromDb

    init {
        viewModelScope.launch {
            getPicturesFromDB()
        }
    }

    fun addPictureToDB(wallpaper: Wallpaper) {
        val task = picturesRepository?.addPictureToDB(wallpaper)
        val thread = Thread(task)
        thread.start()
        getPicturesFromDB()
    }

    fun deleteWallpaper(wallpaper: Wallpaper){
        val task = picturesRepository?.deletePicture(wallpaper)
        val thread = Thread(task)
        thread.start()
        getPicturesFromDB()
    }

    private fun getPicturesFromDB() {
        val picturesDbDisposable = database?.picturesDAO()?.getLocalPicturesRx()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
            ?.subscribe({ picturesFromDb ->
                _picturesFromDb.postValue(picturesFromDb.mapEntityListToModelList())
            }, { throwable ->
                Log.d(throwable.message, "error :(")
            })
        picturesDbDisposable?.let { compositeDisposable.add(it) }
    }
}
