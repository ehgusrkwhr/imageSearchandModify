package com.kdh.imageconvert.ui.viewmodel

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.data.model.SearchData
import com.kdh.imageconvert.data.repository.search.SearchRepository
import com.kdh.imageconvert.ui.state.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel : ViewModel() {

    private val _searchData = MutableStateFlow<UiState<SearchData>>(UiState.Empty)
    val searchData = _searchData.asStateFlow()
    private val searchRepository: SearchRepository = SearchRepository()
    var sumSearchData = mutableListOf<Document>()
    fun getSearchData(query: String, sort: String, page: Int, size: Int) {
        viewModelScope.launch {
//            Timber.d("${searchRepository.getImageData(query,sort ,page,size)}")
            searchRepository.getImageData(query, sort, page, size)
                .onStart { _searchData.update { UiState.Loading } }
                .catch { e -> _searchData.update { UiState.Error(e.message) } }
                .collectLatest { value ->
                    _searchData.update { UiState.Success(value) }
                }
        }
    }

}