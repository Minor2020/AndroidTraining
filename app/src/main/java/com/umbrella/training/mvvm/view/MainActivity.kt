package com.umbrella.training.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.umbrella.training.fresco.FrescoMainActivity
import com.umbrella.training.mvvm.R

class MainActivity : AppCompatActivity() {
    private var frescoEntrance: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initListener()
    }

    private fun initView() {
        frescoEntrance = findViewById(R.id.m_bt_fresco)
    }

    private fun initListener() {
        frescoEntrance?.setOnClickListener {
            startActivity(Intent(this, FrescoMainActivity::class.java))
        }
    }
}
