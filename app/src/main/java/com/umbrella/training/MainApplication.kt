package com.umbrella.training

import android.app.Application
import com.umbrella.training.fresco.ImageLoader

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
    }
}