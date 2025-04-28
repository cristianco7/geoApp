package com.example.geoapp.tools

import android.app.AlertDialog
import android.content.Context

object UIUtils {
    fun createDialog(
        context: Context,
        title: String,
        message: String,
        onPositiveClick: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
            .create()
            .show()
    }
}