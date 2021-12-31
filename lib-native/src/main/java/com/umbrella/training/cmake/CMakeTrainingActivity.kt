package com.umbrella.training.cmake

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity




class CMakeTrainingActivity : AppCompatActivity() {

    private var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cmake_training)

        textView = findViewById(R.id.tv_text);
        textView?.text = stringFromJNI()

    }

    /**
     * A native method that is implemented by the 'hello_cmake' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'hello_cmake' library on application startup.
        init {
            System.loadLibrary("hello_cmake")
        }

        // implementend by libplasma.so
        external fun renderPlasma(bitmap: Bitmap?, time_ms: Long)

    }


    fun describe(): String {
        return "Normal func."
    }
}