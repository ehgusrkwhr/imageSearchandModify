package com.kdh.imageconvert.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kdh.imageconvert.databinding.FragmentHomeBinding
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.ui.adapter.ImageFrameAdapter
import com.kdh.imageconvert.ui.custom.CustomSimpleDialog
import com.kdh.imageconvert.ui.listener.FadeInOutPageTransFormer
import com.kdh.imageconvert.ui.listener.PageFlyingTransFormer
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import com.kdh.imageconvert.util.FileUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs

class HomeFragment : Fragment() {

    //    private val binding : Sear
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private var coroutineJob: Job = Job()
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob
    private var imageFrameAdapter: ImageFrameAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImageFrameAdapter()
        getImageFiles()

    }

    private fun initVpEvent() {
        //가져오기 특정 폴더 안에 있는 데이터...
    }

    private fun getImageFiles() {
        lifecycleScope.launch(coroutineContext) {
            //파일객체가져오기
            val fileList = FileUtil.fetchImagesToMediaStore(requireContext())
            //객체를 비트맵 전환 ????
            withContext(Dispatchers.Main) {
                imageFrameAdapter?.submitList(fileList)
            }
        }
    }

    private fun initImageFrameAdapter() {
        imageFrameAdapter = ImageFrameAdapter()
        val gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {

            private var isDownEvent = false // 아이템 눌렀는지 파악

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                val dialog = CustomSimpleDialog("이미지 삭제하시겠습니까?", "정말로 삭제 하겠씁니까!!??")
                if (parentFragmentManager.findFragmentByTag("simple_dialog") == null) {
                    dialog.show(parentFragmentManager, "simple_dialog")
                }
                return super.onDoubleTap(e)
            }

//            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//                // 수평 움직임 보다 수직 움직임이 클떄 !! 위 아래 상관 ㄴㄴ
//                if ((abs(distanceY) > abs(distanceX) && abs(distanceY) > 60 ) && isDownEvent) {
//                    Log.d("dodo55 ", "distanceY : ${distanceY}")
//                    if (distanceY > 0) {  //위쪽 이벤트 처리
//
//                    } else { //아래쪽 이벤처리
//                        //다이어 로그
//                        val dialog = CustomSimpleDialog("이미지 삭제하시겠습니까?", "정말로 삭제 하겠씁니까!!??")
//                        if (parentFragmentManager.findFragmentByTag("simple_dialog") == null) {
//                            dialog.show(parentFragmentManager, "simple_dialog")
//                        }
//                    }
//                    isDownEvent = false
//                }
//                return true
//            }
//
//            override fun onDown(e: MotionEvent?): Boolean {
//                isDownEvent = true
//                return true
//            }


        })

        binding.vpConvertImage.apply {

            adapter = imageFrameAdapter
            isNestedScrollingEnabled
            setPageTransformer(PageFlyingTransFormer())

            getChildAt(0)?.also {
                it as RecyclerView
                (it.layoutManager as? LinearLayoutManager)?.apply {
                    // 스크롤 민감도 조절
                    isSmoothScrollbarEnabled = false
                }
                it.setOnTouchListener { v, event ->
                    Log.d("dodo55 ", "setOnTouchListener")
                    gestureDetector.onTouchEvent(event)
                    v.performClick()
                    false
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d("dodo55 ", "HomeFragment onDestroyView")
        coroutineContext.cancel()
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("dodo55 ", "HomeFragment onDestroy")
        super.onDestroy()
    }

}