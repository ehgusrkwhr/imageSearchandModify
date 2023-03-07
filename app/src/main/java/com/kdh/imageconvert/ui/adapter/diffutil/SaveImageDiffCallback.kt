package com.kdh.imageconvert.ui.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.data.model.FileInfo

class SaveImageDiffCallback : DiffUtil.ItemCallback<FileInfo>() {
    override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
        return oldItem == newItem
    }


}