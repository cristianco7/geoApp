package com.example.geoapp.tools

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.R
import com.example.geoapp.adapter.FavoritePointAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.geojson.Point
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

    fun showFavoritePointBottomSheet(
        context: Context,
        point: Point,
        mapManager: MapManager,
        favoritePointAdapter: FavoritePointAdapter,
        loadFavoritePoints: () -> Unit,
        onDismiss: () -> Unit
    ): BottomSheetDialog {
        return showBottomSheet(context, R.layout.item_favorite_point) { view, bottomSheetDialog ->
            val edtPointName = view.findViewById<EditText>(R.id.edtPointName)
            val btnSavePoint = view.findViewById<Button>(R.id.btnSavePoint)
            val btnFavorites = view.findViewById<ImageView>(R.id.btnFavorites)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFavoritePoints)

            btnFavorites.setOnClickListener {
                if (edtPointName.visibility != View.VISIBLE) {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    favoritePointAdapter.updatePoints(emptyList())
                    recyclerView.adapter = favoritePointAdapter
                    edtPointName.visibility = View.VISIBLE
                    btnSavePoint.visibility = View.VISIBLE
                    loadFavoritePoints()
                }
            }

            btnSavePoint.setOnClickListener {
                val pointName = edtPointName.text.toString().trim()
                if (pointName.isNotEmpty()) {
                    mapManager.saveFavoritePoint(pointName, point.latitude(), point.longitude())
                    showCustomToast(context, context.getString(R.string.point_saved))
                    bottomSheetDialog.dismiss()
                    onDismiss()
                } else {
                    showCustomToast(context, context.getString(R.string.enter_point_name))
                }
            }

            bottomSheetDialog.setOnDismissListener {
                onDismiss()
            }
        }
    }


    fun showCustomToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.toast_transaparent, null)
        val toastText = toastView.findViewById<TextView>(R.id.toastMessageDoc)
        toastText.text = message

        val toast = Toast(context)
        toast.duration = duration
        toast.view = toastView
        toast.setGravity(Gravity.CENTER,0,600)
        toast.show()
    }
}
