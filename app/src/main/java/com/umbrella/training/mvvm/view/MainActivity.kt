package com.umbrella.training.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.umbrella.training.fresco.FrescoMainActivity
import com.umbrella.training.mvvm.R
import com.umbrella.training.mvvm.viewmodel.MainViewModel
import com.umbrella.training.okhttp.DownloadFile
import com.umbrella.training.okhttp.SIMPLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private var tipsView: TextView? = null
    private var frescoEntrance: Button? = null
    private var downloadEntrance: Button? = null
    // /storage/sdcard0/Android/data/package/files
    private var dirFile: File? = null
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dirFile = getExternalFilesDir(null)
        initView()
        initListener()
        initObserver()
    }

    private fun initView() {
        tipsView = findViewById(R.id.tv_tips)
        frescoEntrance = findViewById(R.id.m_bt_fresco)
        downloadEntrance = findViewById(R.id.m_bt_download)
    }

    private fun initListener() {
        downloadEntrance?.setOnClickListener {
            dirFile?.let {
                mainViewModel.downloadFile(it)
            }
            mainViewModel.progress.postValue("begin download.")
            lifecycleScope.launch(Dispatchers.IO) {
                getImgUrlFromRawFile()
            }
        }
        frescoEntrance?.setOnClickListener {
            val intent = Intent(this, FrescoMainActivity::class.java)
            intent.putExtra("IMG_PATH", DownloadFile.getFileByUri(dirFile!!, SIMPLE_URL).path)
            startActivity(intent)
        }
    }

    private fun initObserver() {
        // 订阅 LiveData 中 progress 的变化
        mainViewModel.progress.observe(this, Observer { progress -> tipsView?.text = progress })
    }

    private var imgUrlList = ArrayList<String>()

    private fun getImgUrlFromRawFile() {
        try {
            val inputStream = resources.openRawResource(R.raw.heic_comp)
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferReader = BufferedReader(inputStreamReader)
            var rawLine = ""
            while (bufferReader.readLine().also { rawLine = it } != null) {
                imgUrlList.add(rawLine.substring(rawLine.indexOf(' ') + 1))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {

        }
    }
}
