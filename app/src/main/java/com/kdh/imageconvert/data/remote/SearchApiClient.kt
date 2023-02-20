package com.kdh.imageconvert.data.remote

import com.kdh.imageconvert.data.model.SearchData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiClient {

    @GET("v2/search/image")
    suspend fun getImageData(
        @Query("query") query : String,
        @Query("sort") sort : String,
        @Query("page") page : Int,
        @Query("size") size : Int) : SearchData

}