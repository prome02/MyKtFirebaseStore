package com.example.myktfirebasestore

import android.view.View
import android.widget.ProgressBar

class ProgressCtrl(private val progressBar: ProgressBar) {
    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }
}