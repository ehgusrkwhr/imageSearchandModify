package com.kdh.imageconvert

import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class GlideModule : AppGlideModule() {
}

@GlideExtension
object GlideExtension {
    fun imageOptions(options: RequestOptions): RequestOptions {
        return options
            .format(DecodeFormat.PREFER_RGB_565)
            .error(R.drawable.image_fail)
            .placeholder(R.drawable.image_fail)
            .timeout(10000)
            .diskCacheStrategy(DiskCacheStrategy.ALL)

    }
}