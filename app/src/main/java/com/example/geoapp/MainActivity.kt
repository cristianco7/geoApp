package com.example.geoapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.adapter.BaseMapAdapter
import com.example.geoapp.database.AppDatabase
import com.example.geoapp.database.DatabaseHelper.loadPoints
import com.example.geoapp.database.FavoritePointDao
import com.example.geoapp.models.BaseMapItem
import com.example.geoapp.models.FavoritePoint
import com.example.geoapp.tools.LocationManager
import com.example.geoapp.tools.MapManager
import com.example.geoapp.tools.UIUtils
import com.mapbox.geojson.Point
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
    private lateinit var baseMapAdapter: BaseMapAdapter
    private var isBottomSheetVisible = false

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
        val btnFavoritePoints = findViewById<LinearLayout>(R.id.btnFavoritesPoints)
        val iconExplore = findViewById<ImageView>(R.id.iconExplore)

        mapManager = MapManager(mapView,this)
        locationManager = LocationManager(this, mapView, requestPermissionLauncher)

        mapManager.initializeMap(Style.MAPBOX_STREETS) {
            loadAlertPoints()
        }

        setupBaseMapDialog()
        iconExplore.background = ContextCompat.getDrawable(this, R.drawable.rounded_background)

        btnFavoritePoints.setOnClickListener {
            loadFavoritePoints { points ->
                UIUtils.showFavoritePointsRecyclerView(mapManager,this,points)
            }

        }


        btnCenterLocation.setOnClickListener {
            locationManager.checkAndEnableLocation()
        }

        setupMapClickListener()
        setupMapLongClickListener()

        btnChangeBaseMap.setOnClickListener {
            UIUtils.showBottomSheet(this, R.layout.bottom_sheet_basemap) { view, _ ->
                val recyclerBaseMap = view.findViewById<RecyclerView>(R.id.recyclerBaseMap)
                recyclerBaseMap.layoutManager = GridLayoutManager(this, 3)
                recyclerBaseMap.adapter = baseMapAdapter
            }
        }

    }

    private fun setupMapClickListener() {
        mapView.mapboxMap.addOnMapClickListener { point ->
            UIUtils.showPointTypeDialog(this) { isAlertPoint ->
                if (isAlertPoint) {
                    mapManager.addAlertPointToMap(point)
                } else {
                    mapManager.addPointToMap(point, false)
                }
            }
            true
        }
    }

    private fun setupBaseMapDialog() {
        val baseMapItems = listOf(
            BaseMapItem(R.drawable.ic_basemap_street, getString(R.string.streets), Style.MAPBOX_STREETS),
            BaseMapItem(R.drawable.ic_basemap_satellite, getString(R.string.satellite), Style.SATELLITE),
            BaseMapItem(R.drawable.ic_basemap_outdoor, getString(R.string.outdoors), Style.OUTDOORS),
            BaseMapItem(R.drawable.ic_basemap_light, getString(R.string.light), Style.LIGHT),
            BaseMapItem(R.drawable.ic_basemap_dark, getString(R.string.dark), Style.DARK),
        )

        val currentStyle = mapView.mapboxMap.style?.styleURI ?: Style.MAPBOX_STREETS

        baseMapAdapter = BaseMapAdapter(baseMapItems, currentStyle) { item ->
            mapManager.initializeMap(item.styleUrl) {
                setupMapClickListener()
            }
        }
    }


    private fun setupMapLongClickListener() {
        mapView.mapboxMap.addOnMapLongClickListener { point ->
            mapManager.addPointToMapLong(point)
            loadFavoritePoints { points ->
                UIUtils.showFavoritePointBottomSheet(
                    context = this,
                    point = point,
                    mapManager = mapManager,
                    points = points,
                    onDismiss = { isBottomSheetVisible = false }
                )
            }
            true
        }
    }


    private fun loadFavoritePoints(onLoaded: (List<FavoritePoint>) -> Unit) {
        loadPoints(this, { getAllFavoritePoints() }, onLoaded)
    }

    private fun loadAlertPoints() {
        loadPoints(this, { getAlertPoints() }) { alertPoints ->
            alertPoints.forEach { point ->
                val mapPoint = Point.fromLngLat(point.longitude, point.latitude)
                mapManager.addAlertPointToMap(mapPoint)
            }
        }
    }

}