package ua.turskyi.automaticwallpaperchanger.ui.pictures.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_pictures.*
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.ui.main.view.MainFragment
import ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter.PictureGridAdapter
import ua.turskyi.automaticwallpaperchanger.ui.pictures.viewmodel.PicturesViewModel

class PicturesFragment : Fragment(R.layout.fragment_pictures) {

    private lateinit var gridViewAdapter: PictureGridAdapter
    private lateinit var picturesViewModel: PicturesViewModel

    private var gridLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picturesViewModel = ViewModelProvider(requireActivity()).get(PicturesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateFragment()
    }

    private fun updateFragment() {

        gridLayoutManager?.spanCount = 2

        gridViewAdapter = PictureGridAdapter { picture ->
            picturesViewModel.addPictureToDB(picture)
            val fragmentManager: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentManager?.replace(
                R.id.container,
                MainFragment()
            )?.addToBackStack(null)?.commit()
        }
        gridViewAdapter.submitList(picturesViewModel.pagedList)

        /**
         *@Description gets number of columns and switch between listView and gridView
         * */
        updateLayoutManager()
    }

    private fun updateLayoutManager() {
        gridLayoutManager = GridLayoutManager(context, 2)
        /* Without this line nothing going to show up */
        picturesRecyclerView.adapter = gridViewAdapter
        picturesRecyclerView.layoutManager = gridLayoutManager
    }
}
