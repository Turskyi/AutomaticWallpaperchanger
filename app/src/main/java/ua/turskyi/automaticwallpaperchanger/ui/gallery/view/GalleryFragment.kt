package ua.turskyi.automaticwallpaperchanger.ui.gallery.view

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_pictures.*
import kotlinx.android.synthetic.main.toolbar.*
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.ui.main.view.MainFragment
import ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter.GalleryGridAdapter
import ua.turskyi.automaticwallpaperchanger.ui.gallery.viewmodel.GalleryViewModel

class GalleryFragment : Fragment(R.layout.fragment_pictures) {

    private lateinit var gridViewAdapter: GalleryGridAdapter
    private lateinit var galleryViewModel: GalleryViewModel

    private var gridLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryViewModel = ViewModelProvider(requireActivity()).get(GalleryViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initListener() {
        btnArrowBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initView() {
        makeBtnClickable()
        gridLayoutManager?.spanCount = 2
        gridViewAdapter = GalleryGridAdapter { picture ->
            galleryViewModel.addPictureToDB(picture)
            val fragmentManager: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentManager?.replace(
                R.id.container,
                MainFragment()
            )?.addToBackStack(null)?.commit()
        }
        gridViewAdapter.submitList(galleryViewModel.pagedList)
        updateLayoutManager()
    }

    private fun makeBtnClickable() {
        val outValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)
        btnArrowBack.setBackgroundResource(outValue.resourceId)
    }

    private fun updateLayoutManager() {
        gridLayoutManager = GridLayoutManager(context, 2)
        /* Without this line nothing going to show up */
        picturesRecyclerView.adapter = gridViewAdapter
        picturesRecyclerView.layoutManager = gridLayoutManager
    }
}
