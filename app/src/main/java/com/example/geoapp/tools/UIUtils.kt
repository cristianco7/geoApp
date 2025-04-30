package com.example.geoapp.tools

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.ref.WeakReference

object UIUtils {
    private var bottomSheetDialogRef: WeakReference<BottomSheetDialog>? = null

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

    fun showBottomSheet(context: Context, layoutResId: Int, setupView: ((View, BottomSheetDialog) -> Unit)? = null): BottomSheetDialog {
        val currentDialog = bottomSheetDialogRef?.get()
        if (currentDialog != null && currentDialog.isShowing) {
            return currentDialog
        }

        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(layoutResId, null)
        setupView?.invoke(view, bottomSheetDialog)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.setOnDismissListener {
            bottomSheetDialogRef = null
        }

        bottomSheetDialogRef = WeakReference(bottomSheetDialog)
        bottomSheetDialog.show()
        return bottomSheetDialog
    }
}