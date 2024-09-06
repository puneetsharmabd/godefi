package com.keyboardai42.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import com.keyboardai42.R


class ShowProgress(context : Context) : Dialog(context) {
    init {
        dialog = Dialog(context)
    }

    fun showPopup() {

        val dialogview = LayoutInflater.from(context)
            .inflate(R.layout.plugin_dialog, null, false)
        //initializing dialog screen

        dialog?.setCancelable(true)
        dialog?.setContentView(dialogview as View)
        val window: Window? = dialog?.window
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog?.show()

        dialog?.findViewById<Button>(R.id.cancel)?.setOnClickListener {
            dialog?.dismiss()
        }

    }
    companion object{
        var dialog: Dialog? = null
        fun dismissPopup() = dialog?.let { dialog!!.dismiss() }
    }
}