package com.umbrella.training.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrella.training.okhttp.DownloadFile
import com.umbrella.training.okhttp.SIMPLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel : ViewModel() {

    val progress: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun downloadFile(dirFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            DownloadFile.download(SIMPLE_URL, dirFile) { progress.postValue(it.toString()) }
        }
    }

}