package com.umbrella.training.okhttp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

object DownloadFile {

    private val httpClient = OkHttpClient()

    fun download(url: String, dir: String) {
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        val body = response.body

        val inputStream = body?.byteStream()
        val outputStream = FileOutputStream(getFileByUri(url))
        val contentLength = inputStream?.available()
        val buffer = ByteArray(2 * 1024)
        val len = 0
        val readLen = 0


    }


    fun getFileByUri(url: String): File {
        return File("", "")
    }
}