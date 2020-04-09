package ua.turskyi.automaticwallpaperchanger.ui.main.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import kotlinx.android.synthetic.main.main_fragment.*
import splitties.toast.toast
import ua.turskyi.automaticwallpaperchanger.App
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.data.Constants
import ua.turskyi.automaticwallpaperchanger.data.Constants.INTERVAL_KEY
import ua.turskyi.automaticwallpaperchanger.data.Constants.PICK_IMAGE_NUM
import ua.turskyi.automaticwallpaperchanger.prefs
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureUri
import ua.turskyi.automaticwallpaperchanger.ui.main.view.adapter.PicturesAdapter
import ua.turskyi.automaticwallpaperchanger.ui.main.viewmodel.MainViewModel
import ua.turskyi.automaticwallpaperchanger.util.getHour
import ua.turskyi.automaticwallpaperchanger.util.getMinute
import ua.turskyi.automaticwallpaperchanger.work.ChangingWallpaperWork
import java.util.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment(R.layout.main_fragment), NumberPicker.OnValueChangeListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PicturesAdapter
    lateinit var pictureList: MutableList<PictureUri>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initView()
        initListeners()
        initObservers()
    }

    private fun initView() {
        pictureList = mutableListOf()
        if (prefs.changingStarted) {
            btnStartStop.text = getString(R.string.main_btn_txt_stop)
        } else {
            btnStartStop.text = getString(R.string.main_btn_txt_start)
        }
        npDelay.minValue = 1
        npDelay.maxValue = 9
        npInterval.minValue = 1
        npInterval.maxValue = 9
        initAdapter()
    }

    private fun initAdapter() {
        adapter = PicturesAdapter()
        rvPictures.adapter = this.adapter
        rvPictures.layoutManager = LinearLayoutManager(activity)
    }

    private fun initListeners() {
        btnStartStop.setOnClickListener {
            when (prefs.changingStarted) {
                false -> {
                    toast("wallpaper changing started")
                    btnStartStop.text = getString(R.string.main_btn_txt_stop)
                    prefs.changingStarted = true
                    //TODO: implement start schedule wallpaper changing"
                    scheduleWallpaperChanging()
                }
                true -> {
                    toast("wallpaper changing stopped")
                    btnStartStop.text = getString(R.string.main_btn_txt_start)
                    prefs.changingStarted = false
                    //TODO: implement stop schedule wallpaper changing
                }
            }
        }

        btnAddPicture.setOnClickListener {
            addPicture()
        }

        npDelay.setOnValueChangedListener(this)
        npInterval.setOnValueChangedListener(this)
    }

    private fun scheduleWallpaperChanging() {
        val data = Data.Builder().putInt(INTERVAL_KEY, npInterval.value).build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val currentTime = System.currentTimeMillis()
        val dueTime: Calendar = Calendar.getInstance()
        dueTime.set(Calendar.HOUR_OF_DAY, getHour(App.instance))
        dueTime.set(Calendar.MINUTE, getMinute(App.instance) + npDelay.value)
        Log.d(
            Constants.LOGS,
            "Wallpaper will change at " +
                    "${getHour(App.instance)} hours " +
                    "${(getMinute(App.instance) + npDelay.value)} minute"
        )
        dueTime.set(Calendar.SECOND, 0)
        val timeDiff = dueTime.timeInMillis - currentTime

        val wallpaperChangingWork = OneTimeWorkRequest
            .Builder(ChangingWallpaperWork::class.java)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(App.instance).enqueue(wallpaperChangingWork)
    }

    private fun addPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.main_chooser_title)
            ), PICK_IMAGE_NUM
        )
    }

    private fun initObservers() {
        viewModel.picturesFromRxDB.observe(
            viewLifecycleOwner, Observer { pictures ->
                updateAdapter(pictures)
            })
        adapter.visibilityLoader.observe(viewLifecycleOwner, Observer { currentVisibility ->
            pb.visibility = currentVisibility
        })
    }

    private fun updateAdapter(pictures: MutableList<PictureUri>) {
        adapter.setData(pictures)
    }

    override fun onValueChange(numberPicker: NumberPicker?, oldMinute: Int, newMinute: Int) {
        when (numberPicker) {
            npDelay -> toast("Changing will start in $newMinute minutes")
            npInterval -> toast("Interval between changing $newMinute minutes")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            val picture = PictureUri(uri)
            pictureList.plusAssign(picture)
            viewModel.addPicturesToDB(pictureList)
        } else {
            toast(getString(R.string.main_toast_wrong_result))
        }
    }
}
