package ua.turskyi.automaticwallpaperchanger.ui.main.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.wallpaper_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.model.Wallpaper
import ua.turskyi.automaticwallpaperchanger.util.PixelUtil

class PicturesAdapter(private val onPictureClickListener:(wallpaper: Wallpaper) -> Unit) : RecyclerView.Adapter<PicturesAdapter.ItemViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val wallpapers: MutableList<Wallpaper> = mutableListOf()
    private val _visibilityLoader = MutableLiveData<Int>()
    val visibilityLoader: MutableLiveData<Int>
        get() = _visibilityLoader

    fun setData(newWallpapers: MutableList<Wallpaper>) {
        _visibilityLoader.postValue(View.VISIBLE)
        this.wallpapers.clear()
        this.wallpapers.addAll(newWallpapers)
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
                _visibilityLoader.postValue(View.GONE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.wallpaper_item,
            parent, false
        )
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val uri: Uri = wallpapers[position].uri
        Glide.with(holder.itemView.context)
            .load(uri)
            .thumbnail(0.3f)
            .override(
                PixelUtil.convertDipToPixels(holder.itemView.context, 100F),
                PixelUtil.convertDipToPixels(holder.itemView.context, 100F)
            )
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .priority(Priority.IMMEDIATE)
                    .dontAnimate()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .dontTransform()
            )
            .into(holder.aciv)
    }

    override fun getItemCount() = wallpapers.size

  inner  class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aciv: AppCompatImageView = itemView.aciv
        init {
            itemView.acivHandle.setOnClickListener {
                onPictureClickListener.invoke(wallpapers[layoutPosition])
            }
        }
    }
}