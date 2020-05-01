package ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter

import androidx.recyclerview.widget.DiffUtil
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper

object DiffUtilComparator {

    val PICTURES_DIFF_CALLBACK: DiffUtil.ItemCallback<Wallpaper> =
        object : DiffUtil.ItemCallback<Wallpaper>() {
            override fun areItemsTheSame(
                oldItem: Wallpaper,
                newItem: Wallpaper
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Wallpaper,
                newItem: Wallpaper
            ): Boolean {
                return oldItem.uri == newItem.uri
            }
        }
}