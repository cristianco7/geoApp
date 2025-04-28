package com.example.geoapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.geoapp.tools.LocationManager
import com.example.geoapp.tools.MapManager
import com.mapbox.maps.MapView

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
        mapManager = MapManager(mapView)
        locationManager = LocationManager(this, mapView, requestPermissionLauncher)

        mapManager.initializeMap {}

        btnCenterLocation.setOnClickListener {
            locationManager.checkAndEnableLocation()
        }


    }
}