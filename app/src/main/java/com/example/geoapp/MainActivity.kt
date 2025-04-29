package com.example.geoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.adapter.BaseMapAdapter
import com.example.geoapp.adapter.FavoritePointsAdapter
import com.example.geoapp.database.AppDatabase
import com.example.geoapp.models.BaseMapItem
import com.example.geoapp.tools.LocationManager
import com.example.geoapp.tools.MapManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapManager: MapManager
    private lateinit var locationManager: LocationManager
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                locationManager.checkAndEnableLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        val btnCenterLocation = findViewById<ImageButton>(R.id.btnCenterLocation)
        val btnChangeBaseMap = findViewById<ImageButton>(R.id.btnChangeBaseMap)
        mapManager = MapManager(mapView,this)
        locationManager = LocationManager(this, mapView, requestPermissionLauncher)

        mapManager.initializeMap(Style.MAPBOX_STREETS) {}

        btnCenterLocation.setOnClickListener {
            locationManager.checkAndEnableLocation()
        }

        setupMapClickListener()

        btnChangeBaseMap.setOnClickListener {
            if (bottomSheetDialog == null || !bottomSheetDialog!!.isShowing) {
                bottomSheetDialog = BottomSheetDialog(this)
                val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_basemap, null)
                bottomSheetDialog!!.setContentView(view)

                val recyclerBaseMap = view.findViewById<RecyclerView>(R.id.recyclerBaseMap)
                recyclerBaseMap.layoutManager = GridLayoutManager(this, 3)

                val baseMapItems = listOf(
                    BaseMapItem(R.drawable.ic_basemap_street, getString(R.string.streets), Style.MAPBOX_STREETS),
                    BaseMapItem(R.drawable.ic_basemap_satellite, getString(R.string.satellite), Style.SATELLITE),
                    BaseMapItem(R.drawable.ic_basemap_outdoor, getString(R.string.outdoors), Style.OUTDOORS),
                    BaseMapItem(R.drawable.ic_basemap_light, getString(R.string.light), Style.LIGHT),
                    BaseMapItem(R.drawable.ic_basemap_dark, getString(R.string.dark), Style.DARK),
                )

                val currentStyle = mapView.mapboxMap.style?.styleURI ?: Style.MAPBOX_STREETS

                val adapter = BaseMapAdapter(baseMapItems, currentStyle) { item ->
                    mapManager.initializeMap(item.styleUrl) {
                        setupMapClickListener()
                    }
                    bottomSheetDialog?.dismiss()
                }
                recyclerBaseMap.adapter = adapter

                bottomSheetDialog!!.setOnDismissListener {
                    bottomSheetDialog = null
                }

                bottomSheetDialog!!.show()
            }
        }

    }

    private fun setupMapClickListener() {
        mapView.mapboxMap.addOnMapClickListener { point ->
            mapManager.addPointToMap(point)
            true
        }
    }
}