package com.example.geoapp.tools

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.example.geoapp.defines.Defines
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import android.provider.Settings
import androidx.core.content.edit

class LocationManager(
    private val context: Context,
    private val mapView: MapView,
    private val requestPermissionLauncher: ActivityResultLauncher<String>

) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("geoAppPrefs", Context.MODE_PRIVATE)

    fun checkAndEnableLocation() {
        val hasRequestedPermissionBefore =
            sharedPreferences.getBoolean("hasRequestedPermission", false)

        when {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (isLocationEnabled()) {
                    centerUserLocation()
                } else {
                    UIUtils.createDialog(
                        context,
                        Defines.LOCATION_REQUIRED,
                        Defines.ENABLE_LOCATION
                    ) {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            }

            !ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && hasRequestedPermissionBefore -> {
                UIUtils.createDialog(
                    context,
                    Defines.PERMISSION_REQUIRED,
                    Defines.PERMISSION_DISABLED
                ) {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = android.net.Uri.fromParts("package", context.packageName, null)
                        }
                    context.startActivity(intent)
                }
            }

            else -> {
                sharedPreferences.edit { putBoolean("hasRequestedPermission", true) }
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }


    private fun centerUserLocation() {
        mapView.mapboxMap.getStyle {
            val locationComponent = mapView.location
            locationComponent.updateSettings {
                enabled = true
                pulsingEnabled = true
            }

            val positionChangedListener = object : OnIndicatorPositionChangedListener {
                override fun onIndicatorPositionChanged(point: Point) {
                    mapView.mapboxMap.flyTo(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(14.0)
                            .build()
                    )
                    locationComponent.removeOnIndicatorPositionChangedListener(this)
                }
            }

            locationComponent.addOnIndicatorPositionChangedListener(positionChangedListener)
        }
    }
}