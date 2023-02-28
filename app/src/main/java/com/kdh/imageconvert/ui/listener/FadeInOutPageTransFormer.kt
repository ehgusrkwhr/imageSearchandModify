package com.kdh.imageconvert.ui.listener

import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class FadeInOutPageTransFormer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            alpha = if(position < -1 || position >1){
                //페이지 화면 밖
                0f
            }else if(position <= 0){
                //왼쪾 스크롤
                1 + position
            }else{
                1 - position
            }
        }
    }
}