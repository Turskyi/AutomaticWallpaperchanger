package ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.picture_grid_item.view.*
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.model.PictureModel
import ua.turskyi.automaticwallpaperchanger.ui.pictures.view.adapter.DiffUtilComparator.PICTURES_DIFF_CALLBACK

class PictureGridAdapter(
    private val onPictureClickListener: (post: PictureModel) -> Unit
) : PagedListAdapter<PictureModel, RecyclerView.ViewHolder>(PICTURES_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PictureGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PictureGridViewHolder) {
            val uri: Uri? = getItem(position)?.uri
            Glide.with(holder.itemView.context)
                .load(uri)
                .into(holder.picturePreviewIV)
        }
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
                onPictureClickListener.invoke(getItem(layoutPosition) as PictureModel)
            }
        }
        val picturePreviewIV: ImageView = itemView.picturePreviewIV
    }
}