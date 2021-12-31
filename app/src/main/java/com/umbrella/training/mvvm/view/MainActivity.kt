package com.umbrella.training.mvvm.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.umbrella.training.cmake.CMakeTrainingActivity
import com.umbrella.training.fresco.FrescoMainActivity
import com.umbrella.training.mvvm.R
import com.umbrella.training.mvvm.decoder.H265Decoder
import com.umbrella.training.mvvm.viewmodel.MainViewModel
import com.umbrella.training.okhttp.DownloadFile
import com.umbrella.training.okhttp.SIMPLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var tipsView: TextView? = null
    private var frescoEntrance: Button? = null
    private var downloadEntrance: Button? = null
    private var surface: SurfaceView? = null
    private var dirFile: File? = null
    private val mainViewModel: MainViewModel by viewModels()
    private var decodeH265Button: Button? = null
    private var jsonTraining: Button? = null
    private var fDialogButton: Button? = null
    private var cmake: Button? = null

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
        surface = findViewById(R.id.surface)
        decodeH265Button = findViewById(R.id.m_bt_decode_h265)
        jsonTraining = findViewById(R.id.m_bt_json)
        fDialogButton = findViewById(R.id.m_bt_fragment_dialog)
        cmake = findViewById(R.id.m_bt_cmake)
    }

    private fun initListener() {
        downloadEntrance?.setOnClickListener {
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

        var surfaceHolder: SurfaceHolder? = null
        surface?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(sHolder: SurfaceHolder) {
                surfaceHolder = sHolder
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
            }
        })

        decodeH265Button?.setOnClickListener {
            surfaceHolder?.let {
                H265Decoder(getThisContext(), it.surface).decodePlay()
            }
        }
        jsonTraining?.setOnClickListener {
            // 已废弃
        }
        fDialogButton?.setOnClickListener {
            CustomDialogFragment().show(supportFragmentManager, CustomDialogFragment.TAG)
//            val phoneNum = "12312341234"
//            val cuid = "35F5A41ACEDC8BACE071827F6F121263|VAGZEPTKA"
//            val md5 = EncryptUtil.hash(EncryptUtil.HASH_MD5, cuid.toByteArray(), false)
//            val key = md5.subSequence(0, 16).toString()
//            val iv = md5.subSequence(16, 32).toString()
//            val encryptNum = EncryptUtil.aesEncrypt(key, phoneNum, EncryptUtil.AES_CBC_PKCS7_PADDING, iv)
//            Log.d("MainActivity", "encrypt num $encryptNum key $key iv $iv")
//            val decyptNum = EncryptUtil.aesDecrypt(key, encryptNum, EncryptUtil.AES_CBC_PKCS7_PADDING, iv)
//            Log.d("MainActivity", "decrypt num $decyptNum")
        }

        cmake?.setOnClickListener {
            val intent = Intent(this, CMakeTrainingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getThisContext(): Context {
        return this
    }

    private fun initObserver() {
        // 订阅 LiveData 中 progress 的变化
        mainViewModel.progress.observe(this, Observer { progress -> tipsView?.text = progress })
    }

    private var imgUrlList = ArrayList<String>()

    private fun getImgUrlFromRawFile() {
        try {
            val inputStream = resources.openRawResource(R.raw.heic_comp)
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach {  rawLine ->
                    imgUrlList.add(rawLine.substring(rawLine.indexOf(' ') + 1))
                }
            }
            mainViewModel.progress.postValue("imgUrlListSize ${imgUrlList.size}")
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
        }
    }
}
