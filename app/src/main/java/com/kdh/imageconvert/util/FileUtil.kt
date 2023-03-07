package com.kdh.imageconvert.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.scaleMatrix
import com.kdh.imageconvert.data.model.FileInfo
import retrofit2.http.Url
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object FileUtil {

    private const val FILE_PATH = "imageconvert/"
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

    //    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageToMediaStore(context: Context, file: File): Uri? {
        // ContentValues 생성
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, FIRST_NAME + file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/${FILE_PATH}")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
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

        contentValues.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(uri, contentValues, null, null)

        // MediaScannerConnection을 사용하여 미디어 스캐너에게 스캔을 요청
        MediaScannerConnection.scanFile(context, arrayOf(uri.path), arrayOf("image/jpeg"), null)
        return uri
    }

    fun fetchImagesToMediaStore(context: Context): List<FileInfo>? {

//        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            MediaStore.Downloads.EXTERNAL_CONTENT_URI
//        } else {
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        }
        val imageUri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Downloads.RELATIVE_PATH
        )

//        val jpgPath = "${imageUri}/%/${FILE_PATH}/%"
        val jpgPath = "%${FILE_PATH}%.jpg"
//        val selection = "${MediaStore.Downloads.RELATIVE_PATH} like ?"
        val selection = "${MediaStore.Files.FileColumns.DATA} like ?"
        val selectionArgs = arrayOf(jpgPath)
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val fileList = mutableListOf<FileInfo>()

        Log.d("dodo55 ", "selection ${selection}")
        Log.d("dodo55 ", "selectionArgs ${selectionArgs}")
        context.contentResolver.query(
            imageUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val dateModified = cursor.getLong(dateModifiedColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val fileInfo =
                    FileInfo(
                        id = id,
                        displayName = displayName,
                        size = size,
                        dateModified = dateModified,
                        contentUri = contentUri
                    )
                fileList.add(fileInfo)
//                if (displayName.endsWith(".jpg") || displayName.endsWith(".png") || displayName.endsWith(".jpeg")) {
//                    val fileInfo =
//                        FileInfo(
//                            id = id,
//                            displayName = displayName,
//                            size = size,
//                            dateModified = dateModified,
//                            contentUri = contentUri
//                        )
//                    fileList.add(fileInfo)
//                }
                // 이미지 정보를 사용하여 작업 수행
            }

        }
        Log.d("dodo55 ", "fileinfo ${fileList}")
        return fileList
    }

    fun deleteImageFile(context: Context, fileInfo: FileInfo): Boolean {
        val uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.Images.Media._ID} like ?"
//        val selection = MediaStore.Images.Media._ID
        val selectionArgs = arrayOf(fileInfo.id.toString())
        val contentResolver = context.contentResolver
        val rowsDeleted = contentResolver.delete(uri, selection, selectionArgs)
        return rowsDeleted > 0

    }

//    fun selectedImageFile(context : Context,fileInfo : FileInfo) {
//        // uri 로 파일찾기
//        // 경로를 리턴??
//    }


    fun resizeBitmap(bitmap: Bitmap,newWidth : Int, newHeight : Int) : Bitmap{
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap,0,0,width,height,matrix,false)
    }


}