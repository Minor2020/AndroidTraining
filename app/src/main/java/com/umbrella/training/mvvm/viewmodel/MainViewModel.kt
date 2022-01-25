package com.umbrella.training.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrella.training.okhttp.DownloadFile
import com.umbrella.training.okhttp.SIMPLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel : ViewModel() {

    val progress: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val info: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun downloadFile(dirFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            DownloadFile.download(SIMPLE_URL, dirFile) { progress.postValue(it.toString()) }
        }
    }

    fun mergeInfo() {
        viewModelScope.launch {

            val firstInfo =  InfoRepository.loadFirstInfo()
            var secondInfo = ""
            InfoRepository.loadSecondInfo(object : ResultCallback {
                override fun onSuccess(res: String) {
                    secondInfo = res
                }

                override fun onFailure() {
                }
            })
            info.postValue(firstInfo + secondInfo)
        }
    }
}

interface ResultCallback {
    fun onSuccess(res: String)
    fun onFailure()
}

object InfoRepository {

    suspend fun loadFirstInfo() : String  = withContext(Dispatchers.Default) {
        // 模拟 CPU 计算
        delay(1000)
        // 返回计算结果
        "first + "
    }

    suspend fun loadSecondInfo(callback: ResultCallback?) {
        withContext(Dispatchers.IO) {
            // 模拟网络请求
            delay(2000)
            val res = "second"
            // 网络请求后的回调, 抛到UI线程
            withContext(Dispatchers.Main) {
                callback?.onSuccess(res)
            }
        }
    }
}