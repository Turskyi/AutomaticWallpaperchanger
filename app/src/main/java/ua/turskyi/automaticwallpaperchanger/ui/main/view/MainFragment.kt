package ua.turskyi.automaticwallpaperchanger.ui.main.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchasesResult
import kotlinx.android.synthetic.main.fragment_main.*
import splitties.toast.longToast
import splitties.toast.toast
import ua.turskyi.automaticwallpaperchanger.App
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.data.Constants.INTERVAL_KEY
import ua.turskyi.automaticwallpaperchanger.data.Constants.LOGS
import ua.turskyi.automaticwallpaperchanger.data.Constants.WORK_TAG
import ua.turskyi.automaticwallpaperchanger.data.DataController
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper
import ua.turskyi.automaticwallpaperchanger.prefs
import ua.turskyi.automaticwallpaperchanger.service.work.ChangingWallpaperWork
import ua.turskyi.automaticwallpaperchanger.ui.main.view.adapter.PicturesAdapter
import ua.turskyi.automaticwallpaperchanger.ui.main.viewmodel.MainViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment(R.layout.fragment_main)
    , NumberPicker.OnValueChangeListener
    , PurchasesUpdatedListener {

    companion object {
        fun newInstance() = MainFragment()
        private const val PICK_IMAGE_NUM = 1
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PicturesAdapter
    private val workManager: WorkManager = WorkManager.getInstance(App.instance)
    private lateinit var billingClient: BillingClient
    private val mSkuDetailsMap: MutableMap<String, SkuDetails> = HashMap()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initView()
        initListeners()
        initObservers()
        initBilling()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_NUM && data != null) {
                val uri: Uri? = data.data
                val imageId = uri?.lastPathSegment?.takeLastWhile { it.isDigit() }?.toInt()
                getContentUriFromUri(imageId)?.let {
                    viewModel.addPictureToDB(
                        it
                    )
                }
            }
        } else {
            toast("did not choose anything")
        }
    }

    private fun getContentUriFromUri(id: Int?): Wallpaper? {
        val columns = arrayOf(MediaStore.Images.Media._ID)

        val orderBy =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) MediaStore.Images.Media.DATE_TAKEN
            else MediaStore.Images.Media._ID

        /** This cursor will hold the result of the query
        and put all data in Cursor by sorting in descending order */
        val cursor = App.instance.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns, null, null, "$orderBy DESC"
        )
        cursor?.moveToFirst()
        val uriImage = Uri.withAppendedPath(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "" + id
        )
        val galleryPicture = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)?.toLong()?.let {
            Wallpaper(
                it, uriImage)
        }
        cursor?.close()
        return galleryPicture
    }

    private fun initView() {
        if (prefs.isChangingStarted) {
            btnStartStop.text = getString(R.string.main_btn_txt_stop)
        } else {
            btnStartStop.text = getString(R.string.main_btn_txt_start)
        }
        npDelay.minValue = 0
        npDelay.maxValue = 9
        npInterval.minValue = 1
        npInterval.maxValue = 9
        initAdapter()
    }

    private fun initAdapter() {
        adapter = PicturesAdapter {
            toast("${it.uri}")
            viewModel.deleteWallpaper(it)
        }
        rvPictures.adapter = this.adapter
        rvPictures.layoutManager = LinearLayoutManager(activity)
    }

    private fun initListeners() {
        btnAddPicture.setOnClickListener { addWallpaper() }
        btnUpgrade.setOnClickListener { viewModel.mSkuId.launchBilling() }
        btnStartStop.setOnClickListener {
            when (prefs.isChangingStarted) {
                false -> {
                    scheduleWallpaperChanging()
                    toast("wallpaper changing started")
                }
                true -> {
                    stopScheduleChanging()
                    toast("wallpaper changing stopped")
                }
            }
        }

        npDelay.setOnValueChangedListener(this)
        npInterval.setOnValueChangedListener(this)
    }

    private fun stopScheduleChanging() {
        btnStartStop.text = getString(R.string.main_btn_txt_start)
        prefs.isChangingStarted = false
        workManager.cancelAllWorkByTag(WORK_TAG)
    }

    private fun createInputData(): Data {
        return Data.Builder()
            .putInt(INTERVAL_KEY, npInterval.value)
            .build()
    }

    private fun scheduleWallpaperChanging() {
        btnStartStop.text = getString(R.string.main_btn_txt_stop)
        prefs.isChangingStarted = true
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val wallpaperChangingWork = OneTimeWorkRequest
            .Builder(ChangingWallpaperWork::class.java)
            .setInitialDelay(2000, TimeUnit.MILLISECONDS)
            .setInputData(createInputData())
            .addTag(WORK_TAG)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(wallpaperChangingWork)
    }

    private fun addWallpaper() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.toolbar_title_choose_a_picture)
            ), PICK_IMAGE_NUM
        )
    }

    private fun initObservers() {
        DataController.getInstance().pictureFromUi.observe(viewLifecycleOwner, Observer {
            viewModel.addPictureToDB(it)
        })

        viewModel.picturesFromDB.observe(
            viewLifecycleOwner, Observer { pictures ->
                updateAdapter(pictures)
                if (pictures.isEmpty()) {
                    btnStartStop.visibility = GONE
                } else {
                    btnStartStop.visibility = VISIBLE
                }
                if (!prefs.isUpgraded) {
                    when {
                        pictures.size <= 5 -> {
                            btnUpgrade.visibility = GONE
                            btnAddPicture.visibility = VISIBLE
                        }
                        pictures.size > 5 -> {
                            btnUpgrade.visibility = VISIBLE
                            btnAddPicture.visibility = GONE
                        }
                    }
                } else {
                    btnUpgrade.visibility = GONE
                    btnAddPicture.visibility = VISIBLE
                }
            })
        adapter.visibilityLoader.observe(viewLifecycleOwner, Observer { currentVisibility ->
            pb.visibility = currentVisibility
        })
    }

    private fun updateAdapter(wallpapers: MutableList<Wallpaper>) {
        adapter.setData(wallpapers)
    }

    private fun String?.launchBilling() {
        longToast("will be available after posting on google play")
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(mSkuDetailsMap[this])
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onValueChange(numberPicker: NumberPicker?, oldMinute: Int, newMinute: Int) {
        when (numberPicker) {
            npDelay -> toast("Changing will start in $newMinute minutes")
            npInterval -> toast("Interval between changing $newMinute minutes")
        }
    }

    private fun initBilling() {
        billingClient =
            BillingClient.newBuilder(requireContext()).enablePendingPurchases().setListener(this)
                .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(LOGS, "billingResult.responseCode ==  OK")
                    /* The BillingClient is ready. You can query purchases here. */
                    /* here we can request information about purchases */

                    /* Sku request */
                    querySkuDetails()

                    /* purchase request */
                    val purchasesList = queryPurchases()
                    /* if the product has already been purchased, provide it to the user */
                    for (i in 0 until purchasesList?.size!!) {
                        val purchaseId = purchasesList[i]!!.sku
                        if (TextUtils.equals(viewModel.mSkuId, purchaseId)) {

                            setUpdatedVersion()
                            Log.d(LOGS, " upgrade purchased")
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                /* we get here if something goes wrong */
                /*   Try to restart the connection on the next request to
                    Google Play by calling the startConnection() method.*/
            }
        })
    }


    private fun setUpdatedVersion() {
        prefs.isUpgraded = true
    }

    private fun queryPurchases(): List<Purchase?>? {
        val purchasesResult: PurchasesResult =
            billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        return purchasesResult.purchasesList
    }

    private fun querySkuDetails() {
        val skuDetailsParamsBuilder = SkuDetailsParams.newBuilder()
        val skuList: MutableList<String> = ArrayList()
        /* here we added the product id from the Play Console */
        skuList.add(viewModel.mSkuId)
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build()) { responseCode, skuDetailsList ->
            if (responseCode.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    mSkuDetailsMap[skuDetails.sku] = skuDetails
                }
            }
        }
    }

    /* In the onPurchasesUpdated () method, we get when the purchase is completed. */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
/*            we will get here after the purchase is made */
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            /* Handle an error caused by a user cancelling the purchase flow. */
        } else {
            Log.d(LOGS, " error onPurchasesUpdated ")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            setUpdatedVersion()
            Log.d(LOGS, " upgrade purchased")
            /* Grant the item to the user, and then acknowledge the purchase */
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            /* Here you can confirm to the user that they've started the pending
             purchase, and to complete it, they should follow instructions that
             are given to them. You can also choose to remind the user in the
            future to complete the purchase if you detect that it is still
             pending. */
            Log.d(LOGS, "user started purchase but not finished yet")
        }
    }
}
