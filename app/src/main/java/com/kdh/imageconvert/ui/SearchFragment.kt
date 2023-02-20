package com.kdh.imageconvert.ui

import android.app.ActivityOptions
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.viewbinding.ViewBinding
import com.kdh.imageconvert.R
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.repeatLastCollectOnStarted
import com.kdh.imageconvert.textChangesToFlow
import com.kdh.imageconvert.ui.adapter.ImageDetailViewPagerAdapter
import com.kdh.imageconvert.ui.adapter.ImageSearchAdapter
import com.kdh.imageconvert.ui.state.UiState
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SearchFragment : Fragment() {

    //    private val binding : Sear

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by activityViewModels()
    private lateinit var imageSearchAdapter: ImageSearchAdapter
    private var textCoroutineJob: Job = Job()
    private val textCoroutineContext: CoroutineContext
        get() = Dispatchers.IO + textCoroutineJob

    private lateinit var backEvent: OnBackPressedCallback
    private lateinit var imageDetailViewPagerAdapter: ImageDetailViewPagerAdapter

    private var sumSearchData = mutableListOf<Document>()
    private var keyword = ""
    private val rvPagingSet = mutableSetOf<Int>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backEvent = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.detailLayout.visibility == View.VISIBLE) {
                    binding.detailLayout.visibility = View.GONE
                    binding.searchLayout.visibility = View.VISIBLE
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backEvent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchAdapter()
        initSearchEvent()
        initDataObserver()
        initImageDetailAdapter()
    }

    private fun initSearchAdapter() {
        imageSearchAdapter = ImageSearchAdapter().apply {
            setClickListener(object : ImageSearchAdapter.OnItemClickListener {
                override fun onItemClicked(position: Int) {
                    binding.searchLayout.visibility = View.GONE
                    binding.detailLayout.visibility = View.VISIBLE
                    binding.vpImageDetail.setCurrentItem(position, false)
                }
            })
        }

        binding.rvSearchInfo.apply {
            adapter = imageSearchAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

        rvPagingEvent()
    }

    private fun initSearchEvent() {
        //searchViewModel.getSearchData("책상", "accuracy", 1, 50)
        lifecycleScope.launch(textCoroutineContext) {
            val editFlow = binding.etSearchImage.textChangesToFlow()
            editFlow
                .debounce(1000)
                .filter {
                    it?.length!! > 1
                }
                .onEach {
                    if (keyword != it.toString()) {
                        sumSearchData.clear()
                        keyword = it.toString()
                    }
                    searchViewModel.getSearchData(keyword, "accuracy", 1, PAGING_SIZE)
                }
                .launchIn(this)
        }
    }

    private fun initImageDetailAdapter() {
        imageDetailViewPagerAdapter = ImageDetailViewPagerAdapter()
        binding.vpImageDetail.adapter = imageDetailViewPagerAdapter

    }

    private fun rvPagingEvent() {
        binding.rvSearchInfo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var pageCount = 1
            var searchEqualNum = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val rvPosition = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    if (rvPosition == layoutManager.itemCount) {
                        //    로딩중이면 실행금
                        if (!rvPagingSet.contains(layoutManager.itemCount)) {
                            pageCount++
                            rvPagingSet.add(layoutManager.itemCount)
                            searchViewModel.getSearchData(keyword, "accuracy", pageCount, PAGING_SIZE)
                        }
                    }
                }

//                val rvPosition = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
//                Log.d("dodo55 ","rvPosition : ${rvPosition}")
//                Log.d("dodo55 ","recyclerView.adapter?.itemCount : ${recyclerView.adapter?.itemCount}")
//                if(rvPosition + 1 == recyclerView.adapter?.itemCount){
//                    if(!rvPagingSet.contains(recyclerView.adapter?.itemCount!!)){
//                        pageCount++
//                        Log.d("dodo55 ","rvPagingSet : ${rvPagingSet}")
//                        rvPagingSet.add(recyclerView.adapter?.itemCount!!)
//                        searchViewModel.getSearchData(keyword, "accuracy", pageCount, 30)
//
//                    }
//                }
                Log.d("dodo55 ", "rvPagingSet123123 : ${rvPagingSet}")
            }
        })
    }

    private fun initDataObserver() {
        repeatLastCollectOnStarted {
            searchViewModel.searchData.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.pbLoading.visibility = View.GONE
//                        binding.rvSearchInfo.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.search_image_slide)
                        state.data.documents?.let { sumSearchData.addAll(it) }
                        imageSearchAdapter.submitList(sumSearchData.toMutableList())
                        imageDetailViewPagerAdapter.submitList(sumSearchData.toMutableList())
                    }
                    is UiState.Error -> {
                        Timber.d("initDataObserver error ${state.error}")
                    }
                    is UiState.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        textCoroutineContext.cancel()
        _binding = null
        super.onDestroyView()

    }

    companion object{
        const val PAGING_SIZE = 20
    }
}