package com.example.geoapp.tools

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.R
import com.example.geoapp.adapter.FavoritePointAdapter
import com.example.geoapp.models.FavoritePoint
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
        points: List<FavoritePoint>,
        onDismiss: () -> Unit
    ): BottomSheetDialog {
        return showBottomSheet(context, R.layout.item_favorite_point) { view, bottomSheetDialog ->
            val edtPointName = view.findViewById<EditText>(R.id.edtPointName)
            val btnSavePoint = view.findViewById<AppCompatButton>(R.id.btnSavePoint)
            val btnFavorites = view.findViewById<LinearLayout>(R.id.btnFavorites)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFavoritePoints)


            val adapter = FavoritePointAdapter(points) { selectedPoint ->
                mapManager.centerMapOnPoint(selectedPoint.latitude, selectedPoint.longitude)
                bottomSheetDialog.dismiss()
            }

            btnFavorites.setOnClickListener {
                if (edtPointName.visibility != View.VISIBLE) {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = adapter
                    recyclerView.visibility = View.VISIBLE
                    edtPointName.visibility = View.VISIBLE
                    btnSavePoint.visibility = View.VISIBLE
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


    private fun showCustomToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.toast_transaparent, null)
        val toastText = toastView.findViewById<TextView>(R.id.toastMessageDoc)
        toastText.text = message

        val toast = Toast(context)
        toast.duration = duration
        toast.view = toastView
        toast.setGravity(Gravity.CENTER,0,500)
        toast.show()
    }

    fun showFavoritePointsRecyclerView(
        mapManager: MapManager,
        context: Context,
        points: List<FavoritePoint>
    ) {
        if (points.isEmpty()) {
            showCustomToast(context, context.getString(R.string.no_favorite_points))
            return
        }
        showBottomSheet(context, R.layout.item_favorite_point) { view, bottomSheetDialog ->
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFavoritePoints)
            val linearLayoutBaseMap = view.findViewById<LinearLayout>(R.id.linearLayoutBaseMap)
            linearLayoutBaseMap.visibility = View.GONE
            val adapter = FavoritePointAdapter(points) { selectedPoint ->
                mapManager.centerMapOnPoint(selectedPoint.latitude, selectedPoint.longitude)
                bottomSheetDialog.dismiss()
            }

            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            recyclerView.visibility = View.VISIBLE
        }
    }

    fun showPointTypeDialog(context: Context, onPointSelected: (isAlertPoint: Boolean) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_point_type_selector, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val btnNormalPoint = dialogView.findViewById<AppCompatButton>(R.id.btnNormalPoint)
        val btnAlertPoint = dialogView.findViewById<AppCompatButton>(R.id.btnAlertPoint)

        btnNormalPoint.setOnClickListener {
            onPointSelected(false)
            dialog.dismiss()
        }

        btnAlertPoint.setOnClickListener {
            onPointSelected(true)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

}
