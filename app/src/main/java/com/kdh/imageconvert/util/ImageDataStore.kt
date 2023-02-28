package com.kdh.imageconvert.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first


private val Context.dataStore by preferencesDataStore("dodo")

class ImageDataStore(context: Context) {

    private val dataStore = context.dataStore
    private val imageSearchKey: Preferences.Key<String> = stringPreferencesKey("image_search_key")

    suspend fun saveImageSearchData(imageSearchData: String) {

        val saveListString = getListImageSearchData()?.let {
            Log.d("dodo33 ","리스트 빈값아님 여기 ?")
            val list = it.toMutableList()
            list.add(imageSearchData)
            Gson().toJson(list)
        } ?: Gson().toJson(listOf(imageSearchData))

        Log.d("dodo33 ","saveListString::class.java : ${saveListString::class.java}")
        Log.d("dodo33 ","saveListString : ${saveListString}")

        dataStore.edit { preference ->
            preference[imageSearchKey] = saveListString
        }
    }

    suspend fun deleteImageSearchData(imageSearchData : String){
        val saveListString = getListImageSearchData()?.let {
            Log.d("dodo33 ","리스트 빈값아님 여기 ?")
            val list = it.toMutableList()
            list.add(imageSearchData)
            Gson().toJson(list)
        } ?: ""

        dataStore.edit { preference ->
            preference[imageSearchKey] = saveListString
        }

    }

    suspend fun getListImageSearchData(): List<String>? {
        val preferences = dataStore.data.first()
        val jsonString = preferences[imageSearchKey] ?: return null
        return Gson().fromJson(jsonString, Array<String>::class.java).toSet().toList()
    }
}