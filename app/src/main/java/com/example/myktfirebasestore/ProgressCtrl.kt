package com.example.myktfirebasestore

import android.view.View
import android.widget.ProgressBar

interface IProgressCtrl {
    fun showProgressBar()
    fun hideProgressBar()
}
class ProgressCtrl(private val progressBar: ProgressBar) {
    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }
}

class ProgressCtrlImpl(private val progressBar: ProgressBar) : IProgressCtrl {
    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }
}