package com.umbrella.training.mvvm

import android.app.Application
import com.ble.pos.sdk.blereader.BLEReader

class MainApplication : Application() {
    companion object {
        val instance: MainApplication by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MainApplication()
        }
    }

    override fun onCreate() {
        super.onCreate()
        BLEReader.getInstance().setApplication(instance)
    }
}