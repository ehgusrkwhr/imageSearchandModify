package com.kdh.imageconvert.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kdh.imageconvert.GlideApp
import com.kdh.imageconvert.GlideExtension
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.data.model.FileInfo
import com.kdh.imageconvert.databinding.ItemFrameImageBinding

class ImageFrameAdapter : ListAdapter<FileInfo, ImageFrameAdapter.ImageFrameViewHolder>(ImageFrameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageFrameViewHolder {
        val binding = ItemFrameImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageFrameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageFrameViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ImageFrameViewHolder(private val binding: ItemFrameImageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 이벤트 설정 여기서 다이어로그 띄워야함 ..
        }

        fun bind(fileInfo: FileInfo, pos: Int) {
            GlideApp.with(binding.ivCardImage)
                .load(fileInfo.contentUri)
                .apply { GlideExtension.imageOptions(RequestOptions()) }
//                .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                .into(binding.ivCardImage)

        }
    }

}

class ImageFrameDiffCallback : DiffUtil.ItemCallback<FileInfo>() {
    override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
        return oldItem == newItem
    }

}