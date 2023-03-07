package com.kdh.imageconvert.ui.viewmodel

import android.graphics.Bitmap
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdh.imageconvert.SearchApp
import com.kdh.imageconvert.data.model.Document
import com.kdh.imageconvert.data.model.FileInfo
import com.kdh.imageconvert.data.model.SearchData
import com.kdh.imageconvert.data.repository.search.SearchRepository
import com.kdh.imageconvert.ui.state.UiState
import com.kdh.imageconvert.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URL

class SearchViewModel : ViewModel() {

    private val _searchData = MutableStateFlow<UiState<SearchData>>(UiState.Empty)
    val searchData = _searchData.asStateFlow()
    private val searchRepository: SearchRepository = SearchRepository()
    var sumSearchData = mutableListOf<Document>()

    private val _imageFileList: MutableStateFlow<List<FileInfo>> = MutableStateFlow<List<FileInfo>>(mutableListOf())
    var imageFileList = _imageFileList.asStateFlow()

    private val saveImageList = mutableListOf<FileInfo>()
    private var _imageSaveFileList = MutableStateFlow<List<FileInfo>>(mutableListOf())
    var imageSaveFileList = _imageSaveFileList.asStateFlow()
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

    fun removeSearchData() {
        _searchData.value = UiState.Empty
        sumSearchData.clear()
    }

    fun getImageSearchFileList() {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtil.fetchImagesToMediaStore(SearchApp.getInstance())?.let {
                _imageFileList.value = it
            }
        }
    }

    fun selectedSaveConvertImage(fileInfo: FileInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            saveImageList.add(fileInfo)
            _imageSaveFileList.value = saveImageList.toMutableList()
        }
    }

    fun convertFileToBitmap(): List<Bitmap> {
        val bitmapList = mutableListOf<Bitmap>()
        viewModelScope.launch(Dispatchers.IO) {
            _imageSaveFileList.value.forEach { fileInfo ->
                FileUtil.getBitmapFromUrl(fileInfo.contentUri.toString())?.let { bitmap ->
                    bitmapList.add(bitmap)
                }
            }
        }
        return bitmapList
    }

//    fun selectedFileDelete() : Boolean{
//        viewModelScope.launch(Dispatchers.IO) {
//            FileUtil.deleteImageFile()
//        }
//    }

//    fun fetchSaveImageSearchTextList() : List<String>{
//
//    }


//    fun fetchImageFiles() : List<Bitmap?>? {
//        viewModelScope.launch(Dispatchers.IO) {
//            FileUtil.fetchImagesToMediaStore()
//        }
//
//        return null
//    }

}