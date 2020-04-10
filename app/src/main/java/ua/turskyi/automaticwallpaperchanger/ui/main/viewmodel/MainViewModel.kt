package ua.turskyi.automaticwallpaperchanger.ui.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import ua.turskyi.automaticwallpaperchanger.common.BaseViewModel
import ua.turskyi.automaticwallpaperchanger.data.repository.PictureListRepository
import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureModel
import ua.turskyi.automaticwallpaperchanger.util.mapEntityListToModelList

class MainViewModel(application: Application) : BaseViewModel(application) {

    private val database = PicturesDataBase.getInstance(application)
    private val picturesRepository = database?.let { PictureListRepository(it) }
    private val _picturesFromDb = MutableLiveData<MutableList<PictureModel>>()
    val picturesFromDB: MutableLiveData<MutableList<PictureModel>>
        get() = _picturesFromDb

    fun addPicturesToDB(pictures: ArrayList<PictureModel>) {
        val task = picturesRepository?.addPicturesToDB(pictures)
        val thread = Thread(task)
        thread.start()
        getPicturesFromDB()
    }

    init {
        viewModelScope.launch {
            getPicturesFromDB()
        }
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
