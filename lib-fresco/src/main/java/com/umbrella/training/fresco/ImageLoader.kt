package com.umbrella.training.fresco

import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco

object ImageLoader {

    fun init(context: Context) {
        Fresco.initialize(context)
    }
}