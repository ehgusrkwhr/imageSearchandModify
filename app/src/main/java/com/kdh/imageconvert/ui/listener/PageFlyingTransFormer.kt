package com.kdh.imageconvert.ui.listener

import android.view.View
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import com.kdh.imageconvert.R
import kotlin.math.abs

class PageFlyingTransFormer : PageTransformer {

    private val MIN_SCALE = 0.75f
    private val MIN_ALPHA = 0.5f

    override fun transformPage(page: View, position: Float) {
        page.apply {
            when {
                position < -1 -> { // 페이지가 화면 왼쪽에 완전히 사라짐
                    alpha = 0f
                }
                position <= 1 -> { // 페이지가 화면 안에 있는 경우
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                    val vertMargin = height * (1 - scaleFactor) / 2
                    val horzMargin = width * (1 - scaleFactor) / 2
                    translationX = if (position < 0) { //왼쪽
                        horzMargin - vertMargin / 2
                    } else { //오른쪽
                        -horzMargin + vertMargin / 2
                    }
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
                }
                else -> { // 페이지가 화면 오른쪽에 완전히 사라짐
                    alpha = 0f
                }
            }
        }

//        page.apply {
//            val pageWidth = resources.getDimension(R.dimen.image_frame_width)
//            val pageMargin = resources.getDimension(R.dimen.image_frame_margin)
//            val screenWidth = resources.displayMetrics.widthPixels
//            val offset = screenWidth - pageWidth - pageMargin
//            translationX = position * -offset
//        }
    }
}