package com.example.geoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.geoapp.tools.LocationManager
import com.example.geoapp.tools.MapManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapManager: MapManager
    private lateinit var locationManager: LocationManager

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
        mapManager = MapManager(mapView)
        locationManager = LocationManager(this, mapView, requestPermissionLauncher)

        mapManager.initializeMap {}

        btnCenterLocation.setOnClickListener {
            locationManager.checkAndEnableLocation()
        }

        mapView.mapboxMap.addOnMapClickListener { point ->
            mapManager.addPointToMap(point)
            true
        }

        btnChangeBaseMap.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_basemap, null)
            bottomSheetDialog.setContentView(view)

            val imgStreets = view.findViewById<ImageView>(R.id.imgStreets)
            val imgSatellite = view.findViewById<ImageView>(R.id.imgSatellite)
            val imgOutdoors = view.findViewById<ImageView>(R.id.imgOutdoors)

            imgStreets.setOnClickListener {
                mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
                bottomSheetDialog.dismiss()
            }

            imgSatellite.setOnClickListener {
                mapView.mapboxMap.loadStyle(Style.SATELLITE)
                bottomSheetDialog.dismiss()
            }

            imgOutdoors.setOnClickListener {
                mapView.mapboxMap.loadStyle(Style.OUTDOORS)
                bottomSheetDialog.dismiss()
            }
            if(!bottomSheetDialog.isShowing) {
                bottomSheetDialog.show()
            }
        }


    }
}