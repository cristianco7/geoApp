package com.example.geoapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.adapter.BaseMapAdapter
import com.example.geoapp.adapter.FavoritePointAdapter
import com.example.geoapp.database.AppDatabase
import com.example.geoapp.models.BaseMapItem
import com.example.geoapp.tools.LocationManager
import com.example.geoapp.tools.MapManager
import com.example.geoapp.tools.UIUtils
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
    private lateinit var favoritePointAdapter: FavoritePointAdapter
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
        mapManager = MapManager(mapView,this)
        locationManager = LocationManager(this, mapView, requestPermissionLauncher)

        favoritePointAdapter = FavoritePointAdapter(emptyList())

        mapManager.initializeMap(Style.MAPBOX_STREETS) {}

        setupBaseMapDialog()

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
            mapManager.addPointToMap(point)
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
            UIUtils.showFavoritePointBottomSheet(
                context = this,
                point = point,
                mapManager = mapManager,
                favoritePointAdapter = favoritePointAdapter,
                loadFavoritePoints = { loadFavoritePoints() },
                onDismiss = { isBottomSheetVisible = false }
            )
            true
        }
    }


    private fun loadFavoritePoints() {
        val database = AppDatabase.getDatabase(this)
        val favoritePointDao = database.favoritePointDao()

        CoroutineScope(Dispatchers.IO).launch {
            val points = favoritePointDao.getAllFavoritePoints()
            runOnUiThread {
                favoritePointAdapter.updatePoints(points)
            }
        }
    }


}