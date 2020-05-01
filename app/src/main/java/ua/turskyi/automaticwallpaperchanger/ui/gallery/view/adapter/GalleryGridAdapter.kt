package ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.picture_grid_item.view.*
import ua.turskyi.automaticwallpaperchanger.App
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper
import ua.turskyi.automaticwallpaperchanger.ui.gallery.view.adapter.DiffUtilComparator.PICTURES_DIFF_CALLBACK
import ua.turskyi.automaticwallpaperchanger.util.PixelUtil

class GalleryGridAdapter(
    private val onPictureClickListener: (post: Wallpaper) -> Unit
) : PagedListAdapter<Wallpaper, RecyclerView.ViewHolder>(PICTURES_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PictureGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val uri: Uri? = getItem(position)?.uri
        Glide.with(App.instance)
                .load(uri)
            .thumbnail(0.4f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.pic_gray_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .priority(Priority.IMMEDIATE)
                    .dontAnimate()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .override(
                        PixelUtil.convertDipToPixels(App.instance, 400F),
                        PixelUtil.convertDipToPixels(App.instance, 400F)
                    )
            )
            .into((holder as PictureGridViewHolder).picturePreviewIV)
    }
    inner class PictureGridViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        constructor(parent: ViewGroup) : this(
            LayoutInflater.from(parent.context).inflate(
                R.layout.picture_grid_item,
                parent,
                false
            )
        )
        init {
            itemView.setOnClickListener {
                onPictureClickListener.invoke(getItem(layoutPosition) as Wallpaper)
            }
        }
        val picturePreviewIV: ImageView = itemView.picturePreviewIV
    }
}