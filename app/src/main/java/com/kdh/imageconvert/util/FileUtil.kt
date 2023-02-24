package com.kdh.imageconvert.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import retrofit2.http.Url
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object FileUtil {

    private const val FIRST_NAME = "dodo_image_"
    fun getBitmapFromUrl(urlString: String): Bitmap? {
        return runCatching {
            URL(urlString).openStream().use {
                BitmapFactory.decodeStream(it)
            }
        }.getOrNull()
    }

    // 비트맵을 파일로 저장하기
    fun saveBitmapToFile(bitmap: Bitmap, context: Context): File? {
        // 파일 이름 생성
        val filename = "${System.currentTimeMillis()}.jpg"
        // 파일 생성
        val file = File(context.externalCacheDir, filename)

        try {
            // 파일에 비트맵 저장
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return file
    }


    // 파일을 MediaStore에 저장하기

    fun saveImageToMediaStore(context: Context, file: File): Uri? {
        // ContentValues 생성
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, FIRST_NAME + file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }
        // MediaStore에 파일 추가
        val resolver = context.contentResolver
        val downloadUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val uri = resolver.insert(downloadUri, contentValues)

        // 파일 데이터 쓰기
        try {
            resolver.openOutputStream(uri!!).use { outputStream ->
                FileInputStream(file).use { inputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead = inputStream.read(buffer)
                    while (bytesRead != -1) {
                        outputStream!!.write(buffer, 0, bytesRead)
                        bytesRead = inputStream.read(buffer)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return uri
    }


}