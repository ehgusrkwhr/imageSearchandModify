package com.kdh.imageconvert.data.model

import android.net.Uri

data class FileInfo(
    val id: Long,
    val displayName: String,
    val size: Long,
    val dateModified: Long,
    val contentUri: Uri
)
