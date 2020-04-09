package ua.turskyi.automaticwallpaperchanger.ui.main.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.picture_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.turskyi.automaticwallpaperchanger.R
import ua.turskyi.automaticwallpaperchanger.ui.main.model.PictureUri

class PicturesAdapter : RecyclerView.Adapter<PicturesAdapter.ItemViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val pictures: MutableList<PictureUri> = mutableListOf()
    private val _visibilityLoader = MutableLiveData<Int>()
    val visibilityLoader: MutableLiveData<Int>
        get() = _visibilityLoader

    fun setData(newPictures: MutableList<PictureUri>) {
        _visibilityLoader.postValue(View.VISIBLE)
        this.pictures.clear()
        this.pictures.addAll(newPictures)
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
                _visibilityLoader.postValue(View.GONE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.picture_item,
            parent, false
        )
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val uri: Uri? = pictures[position].uri
        Glide.with(holder.itemView.context)
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(holder.aciv)
    }

    override fun getItemCount() = pictures.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aciv: AppCompatImageView = itemView.aciv
    }
}