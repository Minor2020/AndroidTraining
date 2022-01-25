package com.umbrella.training.mvvm.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.umbrella.training.mvvm.R

class CustomEditView(context: Context): FrameLayout(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_dialog_edit_layout, this, true)
    }
}