package com.umbrella.training.fresco

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.facebook.common.util.UriUtil
import com.facebook.drawee.view.SimpleDraweeView
import java.io.File

class FrescoMainActivity : AppCompatActivity() {
    private var imageView: SimpleDraweeView? = null
    private var loadBt: Button? = null
    private var imgPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fresco_main)
        imgPath = intent.getStringExtra("IMG_PATH").toString()

        imageView = findViewById(R.id.sd_image)
        loadBt = findViewById(R.id.m_bt_load_img)
        loadBt?.setOnClickListener {
            loadImage()
        }
    }

    private fun loadImage() {
//        val uri = UriUtil.getUriForResourceId(R.raw.ying)
//        imageView?.setImageURI(uri)
        val uri = UriUtil.getUriForFile(File(imgPath))
        imageView?.setImageURI(uri)
    }
}
