package com.kdh.imageconvert.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kdh.imageconvert.databinding.FragmentHomeBinding
import com.kdh.imageconvert.repeatLastCollectOnStarted
import com.kdh.imageconvert.ui.adapter.ImageFrameAdapter
import com.kdh.imageconvert.ui.adapter.SaveConvertImageAdapter
import com.kdh.imageconvert.ui.custom.CustomSimpleDialog
import com.kdh.imageconvert.ui.listener.PageFlyingTransFormer
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import com.kdh.imageconvert.util.FileUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment() {

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
        initVpEvent()
        initSaveImageAdapter()

    }

    private fun initVpEvent() {
        //가져오기 특정 폴더 안에 있는 데이터...

        binding.btnImageAdd.setOnClickListener {
            imageFrameAdapter?.currentList?.get(binding.vpConvertImage.currentItem)?.let { fileInfo ->
                Log.d("dodo33 ", "fileInfo : ${fileInfo}")
                viewModel.selectedSaveConvertImage(fileInfo)
            }
        }

        binding.btnImageConvert.setOnClickListener {
            val bitmapList = viewModel.convertFileToBitmap()
            //추가 파일 리스트 비트맵 리스트로 전환 해서 glide 함수 사용 해서 Gif 로 뷰에 보여 주기
        }
    }

    private fun getImageFiles() {
        viewModel.getImageSearchFileList()
    }


    private fun initSaveImageAdapter() {
        val saveImageAdapter = SaveConvertImageAdapter()
        binding.rvImageAddList.adapter = saveImageAdapter

        repeatLastCollectOnStarted {
            viewModel.imageSaveFileList.collect { saveFiles ->
                saveImageAdapter.submitList(saveFiles)
                Log.d("dodo33 ", "saveFiles : ${saveFiles}")
            }
        }
    }

    private fun initImageFrameAdapter() {
        imageFrameAdapter = ImageFrameAdapter()
        val gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {

            private var isDownEvent = false // 아이템 눌렀는지 파악

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                val dialog = CustomSimpleDialog("이미지 삭제하시겠습니까?", "정말로 삭제 하겠습니까!?")
                dialog.setButtonClickListener(object : CustomSimpleDialog.SimpleDialogClickedListener {
                    override fun onNegativeClicked() {
                        dialog.dismiss()
                    }

                    override fun onPositiveClicked() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            imageFrameAdapter?.getItemId(binding.vpConvertImage.currentItem)
                            val item = imageFrameAdapter?.currentList?.get(binding.vpConvertImage.currentItem)
                            var result: Boolean = false
                            item?.let { fileInfo ->
                                withContext(Dispatchers.IO) {
                                    result = FileUtil.deleteImageFile(requireContext(), fileInfo)
                                }
                                dialog.dismiss()
                                if (result) {
                                    getImageFiles()
                                    Toast.makeText(requireContext(), "삭제가 되었습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "삭제가 실패.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                })
                if (parentFragmentManager.findFragmentByTag("simple_dialog") == null) {
                    dialog.show(parentFragmentManager, "simple_dialog")
                }
                return super.onDoubleTap(e)
            }

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
//                    Log.d("dodo55 ", "setOnTouchListener")
                    gestureDetector.onTouchEvent(event)
                    v.performClick()
                    false
                }
            }
        }

        repeatLastCollectOnStarted {
            viewModel.imageFileList.collect { fileList ->
                imageFrameAdapter?.submitList(fileList)
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