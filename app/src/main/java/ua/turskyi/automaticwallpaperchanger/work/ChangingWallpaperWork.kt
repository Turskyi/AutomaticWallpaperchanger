package ua.turskyi.automaticwallpaperchanger.work

import android.content.Context
import android.util.Log
import androidx.work.*
import androidx.work.ListenableWorker.Result.success
import ua.turskyi.automaticwallpaperchanger.data.Constants
import ua.turskyi.automaticwallpaperchanger.data.Constants.INTERVAL_KEY
import ua.turskyi.automaticwallpaperchanger.util.getHour
import ua.turskyi.automaticwallpaperchanger.util.getMinute
import ua.turskyi.automaticwallpaperchanger.util.vibratePhone
import java.util.*
import java.util.concurrent.TimeUnit

class ChangingWallpaperWork(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            changeWallpaper()
            val interval = inputData.getInt(INTERVAL_KEY, 2)
            val currentTime = System.currentTimeMillis()
            val dueTime = Calendar.getInstance()
            dueTime.set(Calendar.HOUR_OF_DAY, getHour(context = applicationContext))
            dueTime.set(Calendar.MINUTE, getMinute(context = applicationContext) + interval)
            Log.d(
                Constants.LOGS,
                "Wallpaper will change at ${(getMinute(context = applicationContext) + interval)} minutes"
            )
            dueTime.set(Calendar.SECOND, 0)
            val timeDiff = dueTime.timeInMillis - currentTime

            val intervalWorkRequest =
                OneTimeWorkRequest.Builder(ChangingWallpaperWork::class.java)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build()
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(
                    Constants.WALLPAPER_CHANGING_WORK,
                    ExistingWorkPolicy.REPLACE,
                    intervalWorkRequest
                )
            return success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun changeWallpaper() {
        vibratePhone(applicationContext)
//        TODO: change wallpaper
    }
}