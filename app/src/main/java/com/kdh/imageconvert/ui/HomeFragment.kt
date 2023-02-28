package com.kdh.imageconvert.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kdh.imageconvert.databinding.FragmentHomeBinding
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.ui.adapter.ImageFrameAdapter
import com.kdh.imageconvert.ui.listener.FadeInOutPageTransFormer
import com.kdh.imageconvert.ui.listener.PageFlyingTransFormer
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import com.kdh.imageconvert.util.FileUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment() {

    //    private val binding : Sear
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private var coroutineJob: Job = Job()
    private val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob
    private var adapter: ImageFrameAdapter? = null
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
                adapter?.submitList(fileList)
            }
        }
    }

    private fun initImageFrameAdapter() {
        adapter = ImageFrameAdapter()
        binding.vpConvertImage.adapter = adapter
//        binding.vpConvertImage.setPageTransformer(FadeInOutPageTransFormer())
        binding.vpConvertImage.setPageTransformer(PageFlyingTransFormer())

//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//                val from = viewHolder.adapterPosition
//                val to = target.adapterPosition
//                val item = itemList.removeAt(from)
//                itemList.add(to, item)
//                recyclerView.adapter?.notifyItemMoved(from, to)
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                if (direction == ItemTouchHelper.UP) {
//                    val position = viewHolder.adapterPosition
//                    itemList.removeAt(position)
//                    recyclerView.adapter?.notifyItemRemoved(position)
//                }
//            }
//        }

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