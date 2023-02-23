package com.kdh.imageconvert.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kdh.imageconvert.GlideApp

import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//class SearchDetailFragment : Fragment() {
//    private var _binding: FragmentSearchDetailBinding? = null
//    private val binding get() = _binding!!
//    private val searchViewModel: SearchViewModel by activityViewModels()
//    private lateinit var imageDetailViewPagerAdapter: ImageDetailViewPagerAdapter
//
//
//    //이미지 확대 축소
//    private var mScaleGestureDetector: ScaleGestureDetector? = null
//    private var scaleFactor = 1.0f
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        _binding = FragmentSearchDetailBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.d("dodo55 ", "onViewCreated")
//        initImageDetailAdapter()
////        initSearchDataObserver()
//    }
//
//
//
//
//    private fun initSearchDataObserver() {
//        lifecycleScope.launch {
//            searchViewModel.searchData.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
//             //   Log.d("dodo55 ", "it.image_url222 ${it!!.documents}")
//              //  Log.d("dodo55 ", "searchViewModel.position222 :  ${searchViewModel.position}")
//                it.let {
//               //     imageDetailViewPagerAdapter.submitList(it.documents)
//                //    binding.vpImageDetail.currentItem = searchViewModel.position
//                }
//            }
//        }
//
////        lifecycleScope.launch {
////            searchViewModel.imageDetail.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
////                Log.d("dodo55 ", "it.image_url ${it!!.image_url}")
////                it.let {
////                //    GlideApp.with(requireActivity()).load(it.image_url).into(binding.ivImageDetail)
////                }
////            }
////        }
//    }
//
//    private fun initImageDetailAdapter(){
//        imageDetailViewPagerAdapter= ImageDetailViewPagerAdapter()
//        binding.vpImageDetail.adapter = imageDetailViewPagerAdapter
//
//     //   imageDetailViewPagerAdapter.submitList(searchViewModel.searchData.value.documents)
//    //    binding.vpImageDetail.setCurrentItem(searchViewModel.position,false)
//    }
//
//
//}