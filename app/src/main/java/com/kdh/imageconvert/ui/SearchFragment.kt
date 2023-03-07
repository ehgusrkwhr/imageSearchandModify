package com.kdh.imageconvert.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kdh.imageconvert.SearchApp
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.repeatLastCollectOnStarted
import com.kdh.imageconvert.textChangesToFlow
import com.kdh.imageconvert.ui.adapter.ImageSearchAdapter
import com.kdh.imageconvert.ui.adapter.SaveImageSearchTextAdapter
import com.kdh.imageconvert.ui.adapter.decoration.GridSpacingItemDecoration
import com.kdh.imageconvert.ui.custom.DialogImageDetail
import com.kdh.imageconvert.ui.`interface`.OnItemClickListener
import com.kdh.imageconvert.ui.state.UiState
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import com.kdh.imageconvert.util.ImageDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by activityViewModels()
    private lateinit var imageSearchAdapter: ImageSearchAdapter
    private lateinit var saveImageSearchTextAdapter: SaveImageSearchTextAdapter
    private var textCoroutineJob: Job? = null
    private var textCoroutineContext: CoroutineContext? = null
    private var backEvent: OnBackPressedCallback? = null
    private var dialogFragment: DialogImageDetail? = null
    private val imageDataStore by lazy { ImageDataStore(SearchApp.getInstance()) }
    private var searchEventFlag = true
    private var keyword = ""
    private val rvPagingSet = mutableSetOf<Int>()

    // 미리보기 text
    private var previewSearchTextList: List<String>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backEvent = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (dialogFragment == null) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    dialogFragment = null
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backEvent!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        initCoroutineContext()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSaveImageSearchText()
        initSearchAdapter()
        initSearchEvent()
        initDataObserver()
    }

    private fun initCoroutineContext() {
        textCoroutineJob = Job()
        textCoroutineContext = Dispatchers.IO + textCoroutineJob as CompletableJob
    }

    private fun initSearchAdapter() {

        imageSearchAdapter = ImageSearchAdapter(requireContext()).apply {
            setClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int) {
                    dialogFragment = DialogImageDetail(position)
                    dialogFragment?.show(parentFragmentManager, "custom_dialog")
                }
            })
        }

        binding.rvSearchInfo.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = imageSearchAdapter
        }
        rvPagingEvent()
    }

    private fun initSaveImageSearchText() {
        saveImageSearchTextAdapter = SaveImageSearchTextAdapter()
        saveImageSearchTextAdapter.setClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int) {
                searchEventFlag = false
                binding.etSearchImage.text = Editable.Factory.getInstance().newEditable(saveImageSearchTextAdapter.currentList[position])
            }

        })
        binding.apply {
            ivSaveText.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            ivSaveText.adapter = saveImageSearchTextAdapter
            searchLayout.bringChildToFront(ivSaveText)
        }

    }

    private fun initSearchEvent() {
        //searchViewModel.getSearchData("책상", "accuracy", 1, 50)
        textCoroutineContext?.let { coroutineContext ->
            lifecycleScope.launch(coroutineContext) {
                val editFlow = binding.etSearchImage.textChangesToFlow()
                editFlow
                    .debounce(if (searchEventFlag) 1000 else 0)
                    .filter {
                        it?.length!! > 1
                    }
                    .onEach { text ->
                        if (keyword != text.toString()) {
                            searchViewModel.sumSearchData.clear()
                            rvPagingSet.clear()
                            keyword = text.toString()
                        }
                        // 검색어 미리보여주기
                        search(keyword, imageDataStore.getListImageSearchData()).let {
                            withContext(Dispatchers.Main) {
                                if (it.size > 1) {
                                    binding.ivSaveText.visibility = View.VISIBLE
                                    saveImageSearchTextAdapter.submitList(it)
                                } else {
                                    binding.ivSaveText.visibility = View.GONE
                                }
                            }
                        }

                        imageDataStore.saveImageSearchData(keyword)
                        searchViewModel.getSearchData(keyword, "accuracy", 1, PAGING_SIZE)
                        searchEventFlag = true
                    }
                    .launchIn(this)
            }
        }

        binding.etSearchImage.setOnFocusChangeListener { _, hasFocus ->
            binding.rvSearchInfo.alpha = if (hasFocus) 1f else 0.5f
        }

        binding.ivDeleteSearchText.setOnClickListener {
            binding.etSearchImage.apply {
                if (!text.isNullOrBlank()) {
                    searchViewModel.removeSearchData()
                    binding.etSearchImage.text.clear()
                    binding.etSearchImage.requestFocus()
//                    imageSearchAdapter.submitList(null)
                    binding.ivEmptyIcon.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun search(query: String, data: List<String>?): List<String> {
        val pattern = query.toRegex(RegexOption.IGNORE_CASE)
        return data?.filter { it.contains(pattern) } ?: listOf()
    }


    private fun rvPagingEvent() {
        binding.rvSearchInfo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var pageCount = 1
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (binding.ivSaveText.isVisible) {
                        binding.ivSaveText.visibility = View.GONE
                    }
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    // 마지막 아이템이 보이는지 체크
                    val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                    val totalItemCount = layoutManager.itemCount
                    // 페이징 처리˜
                    if (totalItemCount - 1 == lastVisibleItemPosition) {
                        if (!rvPagingSet.contains(layoutManager.itemCount)) {
                            imageSearchAdapter.loadingProgressShow()
                            pageCount++
                            rvPagingSet.add(layoutManager.itemCount)
                            searchViewModel.getSearchData(keyword, "accuracy", pageCount, PAGING_SIZE)
                        }
                    }
                }
            }
        })
    }

    // 마지막 뷰 인덱스 계산
    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    private fun initDataObserver() {
        repeatLastCollectOnStarted {
            searchViewModel.searchData.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.ivEmptyIcon.visibility = View.GONE
                        binding.pbLoading.visibility = View.GONE
                        state.data.documents?.let {
                            searchViewModel.sumSearchData.addAll(it)
                        }

                        imageSearchAdapter.submitList(searchViewModel.sumSearchData.toMutableList())
                    }
                    is UiState.Error -> {
                        Timber.d("initDataObserver error ${state.error}")
                    }
                    is UiState.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.ivEmptyIcon.visibility = View.VISIBLE
                        imageSearchAdapter.submitList(null)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d("dodo55 ", "searchfragment onDestroyView")
        binding.etSearchImage.text.clear()
        textCoroutineContext?.cancel()
        dialogFragment?.isVisible.let {
            dialogFragment?.dismiss()
        }
        dialogFragment = null
        backEvent = null
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("dodo55 ", "searchfragment onDestroy")

        super.onDestroy()
    }

    companion object {
        const val PAGING_SIZE = 25
    }
}