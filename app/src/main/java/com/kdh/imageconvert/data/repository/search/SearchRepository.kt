package com.kdh.imageconvert.data.repository.search

import com.kdh.imageconvert.data.model.SearchData
import com.kdh.imageconvert.data.remote.RetrofitClient
import com.kdh.imageconvert.ui.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit

class SearchRepository {

    private val client = RetrofitClient.searchApi

    suspend fun getImageData(query: String, sort: String, page: Int, size: Int): Flow<SearchData> = flow { emit(client.getImageData(query, sort, page, size)) }

//    suspend fun getImageData(query : String,sort : String,page : Int,size:Int) : Flow<UiState<SearchData>> {
//
//        return flow {
//            try{
//                client.getImageData(query,sort,page,size).apply {
//                    if(isSuccessful){
//                        emit(UiState.Success(body()!!))
//                    }else{
//                        emit(UiState.Error(message()))
//                    }
//                }
//            }catch (e : Exception){
//                emit(UiState.Error(e.message))
//            }
//
//        }
//    }


}