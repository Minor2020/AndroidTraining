package com.umbrella.training.mock

import android.content.Context
import android.widget.Toast

object Communicate {

    fun sayHi(context: Context) {
        Toast.makeText(context, "Hi~", Toast.LENGTH_SHORT).show()
    }
}