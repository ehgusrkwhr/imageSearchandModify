package com.kdh.imageconvert.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.databinding.FragmentSearchBinding
import com.kdh.imageconvert.textChangesToFlow
import com.kdh.imageconvert.ui.adapter.ImageSearchAdapter
import com.kdh.imageconvert.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class SearchFragment : Fragment() {

    //    private val binding : Sear

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val  searchViewModel: SearchViewModel by viewModels()
    private lateinit var imageSearchAdapter : ImageSearchAdapter
    private var textCoroutineJob : Job = Job()
    private val textCoroutineContext : CoroutineContext
        get() = Dispatchers.IO + textCoroutineJob

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchAdapter()
        initSearchEvent()
        initSearchDataObserver()
    }
    private fun initSearchAdapter(){
        imageSearchAdapter = ImageSearchAdapter()
        imageSearchAdapter.setClickListener(object : ImageSearchAdapter.OnItemClickListener{
            override fun onItemClicked(position: Int, data: Document) {

            }

        })
        binding.rvSearchInfo.apply {
            adapter = imageSearchAdapter
            layoutManager = GridLayoutManager(context,3)
        }



    }

    @OptIn(FlowPreview::class)
    private fun initSearchEvent(){
        //searchViewModel.getSearchData("책상", "accuracy", 1, 50)
        lifecycleScope.launch(textCoroutineContext){
            val editFlow = binding.etSearchImage.textChangesToFlow()
            editFlow
                .debounce(1000)
                .filter {
                    it?.length!! > 1
                }
                .onEach {
                    searchViewModel.getSearchData(it.toString(),"accuracy",1,80)
                }
                .launchIn(this)



        }

    }

    private fun initSearchDataObserver(){
        lifecycleScope.launch {
            searchViewModel.searchData.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collectLatest {
                imageSearchAdapter.submitList(it.documents)
            }
        }
    }


    override fun onDestroyView() {
        textCoroutineContext.cancel()
        _binding = null
        super.onDestroyView()

    }
}