package ua.turskyi.automaticwallpaperchanger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.main_activity.*
import ua.turskyi.automaticwallpaperchanger.common.BaseActivity
import ua.turskyi.automaticwallpaperchanger.data.Constants.PERMISSION_EXTERNAL_STORAGE
import ua.turskyi.automaticwallpaperchanger.ui.main.view.MainFragment

class MainActivity : BaseActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* permission must be in "onCreate" */
        checkPermission(savedInstanceState)
    }

    private fun checkPermission(savedInstanceState: Bundle?) {
        val permissionGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            showFragment(savedInstanceState)
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(),
            PERMISSION_EXTERNAL_STORAGE
        )
    }

    private fun showFragment(savedInstanceState: Bundle?) {
        savedInstanceState ?: run {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResult: IntArray
    ) {
        when (requestCode) {
            PERMISSION_EXTERNAL_STORAGE -> {

                /** If request is cancelled, the result array is empty. */
                if ((grantResult.isNotEmpty() && grantResult[0] == PackageManager.PERMISSION_GRANTED)) {
                    tvNoPermission.visibility = View.GONE
                    showFragment(savedInstanceState = null)
                } else {
                    /** shows the "get permission view" */
                    tvNoPermission.visibility = View.VISIBLE
                    tvNoPermission.setOnClickListener { requestPermission() }
                }
            }
        }
    }
}
