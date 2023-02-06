package com.kdh.imageconvert.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdh.imageconvert.data.repository.search.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel : ViewModel() {

    private val _searchData = MutableStateFlow<Any>("")
    val searchData : StateFlow<Any> = _searchData

    private val searchRepository: SearchRepository = SearchRepository()
    fun getSearchData(query : String,sort : String,page : Int,size:Int) {
        viewModelScope.launch {
            Timber.d("${searchRepository.getImageData(query,sort ,page,size)}")
            _searchData.value = searchRepository.getImageData(query,sort ,page,size)
        }

    }


}