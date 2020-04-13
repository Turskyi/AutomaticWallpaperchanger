package ua.turskyi.automaticwallpaperchanger.ui.main.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchasesResult
import kotlinx.android.synthetic.main.main_fragment.*
import splitties.toast.longToast
import splitties.toast.toast
import ua.turskyi.automaticwallpaperchanger.App
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.data.Constants.INTERVAL_KEY
import ua.turskyi.automaticwallpaperchanger.data.Constants.LOGS
import ua.turskyi.automaticwallpaperchanger.data.Constants.WORK_TAG
import ua.turskyi.automaticwallpaperchanger.data.DataController
import ua.turskyi.automaticwallpaperchanger.model.PictureModel
import ua.turskyi.automaticwallpaperchanger.prefs
import ua.turskyi.automaticwallpaperchanger.service.work.ChangingWallpaperWork
import ua.turskyi.automaticwallpaperchanger.ui.main.view.adapter.PicturesAdapter
import ua.turskyi.automaticwallpaperchanger.ui.main.viewmodel.MainViewModel
import ua.turskyi.automaticwallpaperchanger.ui.pictures.view.PicturesFragment
import ua.turskyi.automaticwallpaperchanger.util.getHour
import ua.turskyi.automaticwallpaperchanger.util.getMinute
import java.util.*
import java.util.concurrent.TimeUnit


class MainFragment : Fragment(R.layout.main_fragment)
    , NumberPicker.OnValueChangeListener
    , PurchasesUpdatedListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PicturesAdapter
    private val workManager: WorkManager = WorkManager.getInstance(App.instance)
    private lateinit var billingClient: BillingClient
    private val mSkuDetailsMap: MutableMap<String, SkuDetails> = HashMap()
    private val mSkuId = "sku_id_1"
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initView()
        initListeners()
        initObservers()
        initBilling()
    }

    private fun initBilling() {
        billingClient = activity?.applicationContext?.let {
            BillingClient.newBuilder(it).enablePendingPurchases().setListener(this).build()
        }!!
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(LOGS, "billingResult.responseCode ==  OK")
                    // The BillingClient is ready. You can query purchases here.
                    //здесь мы можем запросить информацию о товарах и покупках

                    //запрос о товарах
                    querySkuDetails()

                    //запрос о покупках
                    val purchasesList = queryPurchases()
                    //если товар уже куплен, предоставить его пользователю
                    for (i in 0 until purchasesList?.size!!) {
                        val purchaseId = purchasesList[i]!!.sku
                        if (TextUtils.equals(mSkuId, purchaseId)) {

                            setUpdatedVersion()
                            Log.d(LOGS, " upgrade purchased")
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(LOGS, "billingResult.responseCode !=  OK")
                //сюда мы попадем если что-то пойдет не так
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
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
        //здесь мы добавили id товара из Play Console
        skuList.add(mSkuId)
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build()) { responseCode, skuDetailsList ->
            if (responseCode.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    mSkuDetailsMap[skuDetails.sku] = skuDetails
                }
            }
        }
    }

    //    В метод onPurchasesUpdated() мы попадаем когда покупка осуществлена
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            //сюда мы попадем когда будет осуществлена покупка
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // TODO: Handle any other error codes.
            Log.d(LOGS, " other error")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            setUpdatedVersion()
            Log.d(LOGS, " upgrade purchased")
            // Grant the item to the user, and then acknowledge the purchase
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.
            Log.d(LOGS, "user started purchase but not finished yet")
        }
    }

    private fun initView() {
        if (prefs.changingStarted) {
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
        adapter = PicturesAdapter()
        rvPictures.adapter = this.adapter
        rvPictures.layoutManager = LinearLayoutManager(activity)
    }

    private fun initListeners() {
        btnAddPicture.setOnClickListener { addPicture() }
        btnUpgrade.setOnClickListener { mSkuId.launchBilling() }
        btnStartStop.setOnClickListener {
            when (prefs.changingStarted) {
                false -> {
                    btnStartStop.text = getString(R.string.main_btn_txt_stop)
                    prefs.changingStarted = true
                    scheduleWallpaperChanging()
                    toast("wallpaper changing started")
                }
                true -> {
                    btnStartStop.text = getString(R.string.main_btn_txt_start)
                    prefs.changingStarted = false
                    workManager.cancelAllWorkByTag(WORK_TAG)
                    toast("wallpaper changing stopped")
                }
            }
        }

        npDelay.setOnValueChangedListener(this)
        npInterval.setOnValueChangedListener(this)
    }

    private fun createInputData(): Data {
        return Data.Builder()
            .putInt(INTERVAL_KEY, npInterval.value)
            .build()
    }

    private fun scheduleWallpaperChanging() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val currentTime = System.currentTimeMillis()
        val dueTime: Calendar = Calendar.getInstance()
        dueTime.set(Calendar.HOUR_OF_DAY, getHour(App.instance))
        dueTime.set(Calendar.MINUTE, getMinute(App.instance) + npDelay.value)
        Log.d(
            LOGS,
            "Wallpaper will change at " +
                    "${getHour(App.instance)} hours " +
                    "${(getMinute(App.instance) + npDelay.value)} minute"
        )
        dueTime.set(Calendar.SECOND, 0)
        val timeDiff = dueTime.timeInMillis - currentTime

        val wallpaperChangingWork = OneTimeWorkRequest
            .Builder(ChangingWallpaperWork::class.java)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setInputData(createInputData())
            .addTag(WORK_TAG)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(wallpaperChangingWork)
    }

    private fun addPicture() {
        val fragmentManager: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        val detailedFragment =
            PicturesFragment()
        fragmentManager?.replace(
                R.id.container,
                detailedFragment
            )
            ?.addToBackStack(null)?.commit()
    }

    private fun initObservers() {
        DataController.getInstance().picturesFromUi.observe(viewLifecycleOwner, Observer {
            viewModel.addPicturesToDB(it)
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

    private fun updateAdapter(pictures: MutableList<PictureModel>) {
        adapter.setData(pictures)
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
}
