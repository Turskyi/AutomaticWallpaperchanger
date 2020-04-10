package ua.turskyi.automaticwallpaperchanger.work

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.*
import androidx.work.ListenableWorker.Result.success
import ua.turskyi.automaticwallpaperchanger.data.Constants
import ua.turskyi.automaticwallpaperchanger.data.Constants.INTERVAL_KEY
import ua.turskyi.automaticwallpaperchanger.data.Constants.LOGS
import ua.turskyi.automaticwallpaperchanger.data.Constants.WORK_TAG
import ua.turskyi.automaticwallpaperchanger.data.room.PicturesDataBase
import ua.turskyi.automaticwallpaperchanger.prefs
import ua.turskyi.automaticwallpaperchanger.util.*
import java.util.*
import java.util.concurrent.TimeUnit

class ChangingWallpaperWork(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        try {
            val interval = inputData.getInt(INTERVAL_KEY, 2)
            changeWallpaper(prefs.nextPic)
            Log.d(LOGS, "next picture num ${prefs.nextPic}")
            val currentTime = System.currentTimeMillis()
            val dueTime = Calendar.getInstance()
            dueTime.set(Calendar.HOUR_OF_DAY, getHour(context = applicationContext))
            dueTime.set(Calendar.MINUTE, getMinute(context = applicationContext) + interval)
            Log.d(
                LOGS,
                "Wallpaper will change at ${(getMinute(context = applicationContext) + interval)} minutes"
            )
            dueTime.set(Calendar.SECOND, 0)
            val timeDiff = dueTime.timeInMillis - currentTime
            val intervalWorkRequest =
                OneTimeWorkRequest.Builder(ChangingWallpaperWork::class.java)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .addTag(WORK_TAG)
                    .build()
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(
                    Constants.WALLPAPER_CHANGING_WORK,
                    ExistingWorkPolicy.REPLACE,
                    intervalWorkRequest
                )
            return success()
        } catch (e: Exception) {
            Log.d(LOGS, e.message!!)
            return Result.retry()
        }
    }

    private fun changeWallpaper(next: Int) {
        vibratePhone(applicationContext)
        val database = PicturesDataBase.getInstance(applicationContext)
        val localPictures = database?.picturesDAO()?.getLocalPictures()
        val total = database?.picturesDAO()?.getTotalNum()
        val pictures = localPictures?.mapEntityListToModelList()
        val scheduledPicture = pictures?.get(next)?.uri?.mapUriToBitMap(applicationContext)
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        Log.d(LOGS, "before load")
        wallpaperManager.setBitmap(scheduledPicture)
        Log.d(LOGS, "loaded")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wallpaperManager.setBitmap(scheduledPicture, null, true, WallpaperManager.FLAG_LOCK)
        }
        if (total != null) {
            if (total > next.plus(1)) {
                Log.d(LOGS, "$total > ${next.plus(1)}")
                prefs.nextPic = next.plus(1)
            } else {
                Log.d(LOGS, "next = 0")
                prefs.nextPic = 0
            }
        } else {
            Log.d(LOGS, "db is empty")
        }
    }
}