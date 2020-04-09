package ua.turskyi.automaticwallpaperchanger.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     * Since viewModelJob is passed, all coroutines launched by uiScope can be canceled by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        compositeDisposable.clear()
    }
}