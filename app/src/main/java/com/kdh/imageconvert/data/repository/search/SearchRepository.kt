package com.kdh.imageconvert.data.repository.search

import com.kdh.imageconvert.data.remote.RetrofitClient
import retrofit2.Retrofit

class SearchRepository {

    private val client  = RetrofitClient.searchApi

    suspend fun getImageData(query : String,sort : String,page : Int,size:Int) : List<Any> = client.getImageData(query,sort,page,size)

}