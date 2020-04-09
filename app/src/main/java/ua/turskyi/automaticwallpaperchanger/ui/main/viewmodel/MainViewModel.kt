package ua.turskyi.automaticwallpaperchanger.ui.main.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import ua.turskyi.automaticwallpaperchanger.common.BaseViewModel
import ua.turskyi.automaticwallpaperchanger.data.repository.PictureListRepository
import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureUri

class MainViewModel(application: Application) : BaseViewModel(application) {

    private val database = PicturesDataBase.getInstance(application)
    private val picturesRepository = database?.let { PictureListRepository(it) }
    private val _picturesFromRxDB = MutableLiveData<MutableList<PictureUri>>()
    val picturesFromRxDB: MutableLiveData<MutableList<PictureUri>>
        get() = _picturesFromRxDB

    fun addPicturesToDB(pictures: MutableList<PictureUri>){
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
        val picturesDbDisposable = database?.pictureDAO()?.getRxLiveAll()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
            ?.subscribe({ picturesFromDb ->
                val pictures = mutableListOf<PictureUri>()
                for (picture in picturesFromDb){
                    val mUri: Uri = Uri.parse(picture.uriPath)
                    pictures.add(PictureUri(mUri))
                }
                _picturesFromRxDB.postValue(pictures)
            }, { throwable ->
                Log.d(throwable.message, "error :(")
            })
        picturesDbDisposable?.let { compositeDisposable.add(it) }
    }
}
