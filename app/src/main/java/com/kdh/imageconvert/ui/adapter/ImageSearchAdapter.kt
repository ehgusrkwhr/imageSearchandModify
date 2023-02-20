package com.kdh.imageconvert.ui.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kdh.imageconvert.GlideApp
import com.kdh.imageconvert.R
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.databinding.SearchImageItemBinding
import com.kdh.imageconvert.ui.adapter.diffutil.ImageDiffCallback

class ImageSearchAdapter : ListAdapter<Document, ImageSearchAdapter.ImageSearchViewHolder>(ImageDiffCallback()) {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(position: Int): Unit
    }

    fun setClickListener(itemListener: OnItemClickListener) {
        this.listener = itemListener
    }

    inner class ImageSearchViewHolder(private val binding: SearchImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.ivImageSearch.setOnClickListener {
                listener?.onItemClicked(bindingAdapterPosition)
            }
        }

        fun bind(document: Document) {
//            binding.root.animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.search_image_slide)
            GlideApp.with(binding.ivImageSearch)
                .load(document.thumbnail_url)
                .error(R.drawable.image_fail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivImageSearch)
            binding.tvImageSize.text = "${document.width} x ${document.height}"



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSearchViewHolder {
        val binding = SearchImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSearchViewHolder, position: Int) {

        holder.bind(getItem(position))
    }


}

