package com.umbrella.training.okhttp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File

object DownloadFile {


    private val BUFFER_SIZE = 2048L

    private val httpClient = OkHttpClient()

    fun download(url: String, dir: File, listener: (Long) -> Unit?) {
        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        val body = response.body

        val contentLength = body?.contentLength()
        val source = body?.source()
        val sink = getFileByUri(dir, url).sink().buffer()
        try {
            var totalRead = 0L
            var lastRead = 0L
            // 增加 progress monitor
            while (source!!.read(sink.buffer, BUFFER_SIZE).also { lastRead = it } != -1L) {
                totalRead += lastRead
                sink.emitCompleteSegments()
                // calculate progress
                listener.invoke(totalRead)
            }
            sink.writeAll(source)
            sink.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            sink.closeQuietly()
            source?.closeQuietly()
            body?.closeQuietly()
        }
    }

    fun getFileByUri(dirFile: File, url: String): File {
        // 190.heic
        val fileName = url.substring(url.lastIndexOf('/') + 1)
        return File(dirFile, fileName)
    }
}

const val SIMPLE_URL = "http://10.24.1.79:8137/heic_pic/190.heic"