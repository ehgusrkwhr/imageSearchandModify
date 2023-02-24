package com.kdh.imageconvert.ui.adapter


import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.kdh.imageconvert.GlideApp
import com.kdh.imageconvert.GlideExtension
import com.kdh.imageconvert.R
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.databinding.ProgressItemBinding
import com.kdh.imageconvert.databinding.SearchImageItemBinding
import com.kdh.imageconvert.ui.adapter.diffutil.ImageDiffCallback

//class ImageSearchAdapter : ListAdapter<Document, ImageSearchAdapter.ImageSearchViewHolder>(ImageDiffCallback()) {
class ImageSearchAdapter(private val context : Context) : ListAdapter<Document, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    private var listener: OnItemClickListener? = null
    var isLoading = false

    fun loadingProgressShow() {
        this.isLoading = true
    }

    fun loadingProgressHide() {
        this.isLoading = false
    }

    companion object {
        private const val VIEW_TYPE_PROGRESS = 1
        private const val VIEW_TYPE_DATA = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding = SearchImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ImageSearchViewHolder(binding)
        val binding = SearchImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == VIEW_TYPE_PROGRESS) {
            //로딩
            ProgressViewHolder(binding)
        } else {

            ImageSearchViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProgressViewHolder -> holder.bind()
            is ImageSearchViewHolder -> holder.bind(getItem(position), position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && isLoading) {
            VIEW_TYPE_PROGRESS
        } else {
            VIEW_TYPE_DATA
        }
    }

    inner class ProgressViewHolder(private val binding: SearchImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.progressImage.visibility = View.VISIBLE
        }
    }

    inner class ImageSearchViewHolder(private val binding: SearchImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivImageSearch.setOnClickListener {
                listener?.onItemClicked(bindingAdapterPosition)
            }
        }

        fun bind(document: Document, pos: Int) {
            binding.progressImage.visibility = View.GONE
            GlideApp.with(binding.ivImageSearch)
                .load(document.image_url)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                       // 특정 height 초과했을떄 값 고정
                        resource?.let{
                            val height = it.intrinsicHeight
                            val maxHeight =  context.resources.getDimensionPixelSize(R.dimen.image_max_height)
//                            Log.d("dodo55 ","height : ${height}  ,, maxHeight : ${maxHeight}")
                            if(height > maxHeight){
                                binding.ivImageSearch.layoutParams.height = maxHeight
                            }else{
                                binding.ivImageSearch.layoutParams.height = height
                            }
                        }
                        return false
                    }

                })
                .apply(GlideExtension.imageOptions(RequestOptions()))
//                .override(imageViewWidth, imageViewHeight)
                .into(binding.ivImageSearch)
            binding.tvImageSize.text = "${document.width} x ${document.height}"
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int): Unit
    }

    fun setClickListener(itemListener: OnItemClickListener) {
        this.listener = itemListener
    }


}

