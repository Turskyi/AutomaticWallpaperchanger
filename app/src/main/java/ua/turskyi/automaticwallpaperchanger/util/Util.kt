package ua.turskyi.automaticwallpaperchanger.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.TimePicker

fun getHour(context: Context): Int {
    @Suppress("DEPRECATION")
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> TimePicker(context).hour
        else -> TimePicker(context).currentHour
    }
}

fun getMinute(context: Context): Int {
    @Suppress("DEPRECATION")
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> TimePicker(context).minute
        else -> TimePicker(context).currentMinute
    }
}

fun vibratePhone(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                100,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
    }
}