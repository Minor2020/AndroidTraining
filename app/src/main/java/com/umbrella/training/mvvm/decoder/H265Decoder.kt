package com.umbrella.training.mvvm.decoder

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.umbrella.training.mvvm.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class H265Decoder(val context: Context, val surface: Surface) {
    private val TAG = "H265Decoder"

    private val HEVC_DECODER = "OMX.qcom.video.decoder.hevc"

    private var mediaCodec: MediaCodec? = null

    companion object {
        var bitmap: Bitmap? = null
    }

    init {
        initMediaCodec()
    }

    private fun initMediaCodec() {
        try {
            mediaCodec = MediaCodec.createByCodecName(HEVC_DECODER)//MediaCodec.createDecoderByType("video/hevc")
            val mediaFormat = MediaFormat.createVideoFormat("video/hevc", 464, 976)
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 29)
            mediaCodec?.configure(mediaFormat, surface, null, 0)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Create error.")
        }
    }

    fun decodePlay() {
        mediaCodec?.start()

        GlobalScope.launch {
            // Do background task
            decodeJob()
            withContext(Dispatchers.Main) {
                // Update UI
            }
            // Do background task
        }

    }

    fun decodeJob() {
        try {
            val bytes = getBytes()
            Log.d(TAG, "bytes size " + bytes?.size)
            val inputBuffer = mediaCodec?.inputBuffers

            var startInex = 0;

            val totalSize = bytes?.size

            while (true) {
                if (totalSize == 0 || startInex >= totalSize!!) {
                    break;
                }

                val nextFrameStartIndex = nextFrame(bytes!!, startInex + 1, totalSize!!)
                if (nextFrameStartIndex == -1) break
                Log.d(TAG, "next frame start $nextFrameStartIndex")
                val info = MediaCodec.BufferInfo()

                val iBufferId = mediaCodec?.dequeueInputBuffer(2000);
                if (iBufferId != null) {
                    if (iBufferId >= 0) {
                        val byteBuffer = inputBuffer?.get(iBufferId)
                        byteBuffer?.clear()
                        byteBuffer?.put(bytes, startInex, nextFrameStartIndex - startInex)
                        mediaCodec?.queueInputBuffer(iBufferId, 0, nextFrameStartIndex - startInex, 0, 0)

                        startInex = nextFrameStartIndex
                    } else {
                        continue
                    }

                    var oBufferId = mediaCodec?.dequeueOutputBuffer(info, 2000)
                    Log.d(TAG, "out buffer index $oBufferId")
                    if (oBufferId != null && oBufferId >= 0) {
                        try {
                            Thread.sleep(5)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (nextFrameStartIndex > 51000 && !hasGotImg) {
                            getImg(mediaCodec?.getOutputBuffer(oBufferId)!!)
                            hasGotImg = true
                        }
                        // 直接输出到 surface 渲染并释放
                        mediaCodec?.releaseOutputBuffer(oBufferId, true)
                    } else {
                        Log.d(TAG, "Decode failure.")
                    }

                }
            }
            mediaCodec?.stop()
            mediaCodec?.release()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var hasGotImg = false

    private fun getImg(buffer: ByteBuffer) {
        // remaining 剩余的元素数
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        buffer.rewind()

//        System.arraycopy(buffer.array(), 0, bytes, 0, bytes.size)
        GlobalScope.run {
//            bitmap = YuvToBitmap.rawByteArray2RGBABitmap(bytes, 200, 200)
        }
    }

    private fun nextFrame(bytes: ByteArray, start: Int, totalSize: Int): Int {
        for (index in start until totalSize - 4) {
            if (bytes[index].compareTo(0) == 0
                && bytes[index + 1].compareTo(0) == 0
                && bytes[index + 2].compareTo(0) == 0
                && bytes[index + 3].compareTo(1) == 0) {
                return index
            }
        }
        return -1
    }

    private fun getBytes(): ByteArray? {
        val inputStream = context.resources?.openRawResource(R.raw.transfer265)
        return inputStream?.readBytes()
    }
}