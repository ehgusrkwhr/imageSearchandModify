package com.kdh.imageconvert.ui.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.kdh.imageconvert.data.model.Document

class ImageDiffCallback : DiffUtil.ItemCallback<Document>() {
    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        return (oldItem.image_url == newItem.image_url && oldItem.datetime == newItem.datetime)
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem == newItem
    }

}