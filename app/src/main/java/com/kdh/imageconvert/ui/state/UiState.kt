package com.kdh.imageconvert.ui.state

sealed class UiState<out T : Any>{
    object Loading : UiState<Nothing>()
    data class Success<T : Any>(val data : T) : UiState<T>()
    data class Error(val error : String?) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

//sealed class UiState{
//    object Loading : UiState()
//    data class Success(val data : Any) : UiState()
//    data class Error(val error : String?) : UiState()
//}