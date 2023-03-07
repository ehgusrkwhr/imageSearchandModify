package com.kdh.imageconvert.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kdh.imageconvert.GlideApp
import com.kdh.imageconvert.data.model.FileInfo
import com.kdh.imageconvert.databinding.ItemSaveImageBinding
import com.kdh.imageconvert.ui.adapter.diffutil.SaveImageDiffCallback

class SaveConvertImageAdapter : ListAdapter<FileInfo, SaveConvertImageAdapter.SaveConvertViewHolder>(SaveImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveConvertViewHolder {
        val binding = ItemSaveImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaveConvertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaveConvertViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class SaveConvertViewHolder(private val binding: ItemSaveImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fileInfo : FileInfo,pos : Int){
            GlideApp.with(binding.ivSaveImage)
                .load(fileInfo.contentUri)
                .into(binding.ivSaveImage)
        }
    }


}
