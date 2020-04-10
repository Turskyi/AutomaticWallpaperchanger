package ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter

import androidx.recyclerview.widget.DiffUtil
import ua.turskyi.automaticwallpaperchanger.model.PictureModel

object DiffUtilComparator {

    val PICTURES_DIFF_CALLBACK: DiffUtil.ItemCallback<PictureModel> =
        object : DiffUtil.ItemCallback<PictureModel>() {
            override fun areItemsTheSame(
                oldItem: PictureModel,
                newItem: PictureModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PictureModel,
                newItem: PictureModel
            ): Boolean {
                return oldItem.uri == newItem.uri
            }
        }
}