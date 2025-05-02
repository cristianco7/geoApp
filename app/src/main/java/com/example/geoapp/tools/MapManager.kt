package com.example.geoapp.tools

import android.animation.ValueAnimator
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.geoapp.R
import com.example.geoapp.database.AppDatabase
import com.example.geoapp.models.FavoritePoint
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapManager(private val mapView: MapView, private  val context: Context) {

    private var isPointAdded = false

    companion object {
        private const val GEOJSON_URL =
            "https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_populated_places_simple.geojson"
        private const val SOURCE_ID = "clicked-point-source"
        private const val LAYER_ID = "clicked-point-layer"
    }

    fun initializeMap(styleUrl: String, onStyleLoaded: () -> Unit) {
        mapView.mapboxMap.loadStyle(styleUrl) { style ->
            val drawable =
                AppCompatResources.getDrawable(mapView.context, R.drawable.ic_location_marker_icon)
            val bitmap = drawable?.toBitmap()
            if (bitmap != null) {
                style.addImage("ic_location-marker-icon", bitmap)
            }
            loadGeoJson()
            onStyleLoaded()
        }
    }

    private fun loadGeoJson() {
        mapView.mapboxMap.getStyle { style ->
            val geoJsonSource = GeoJsonSource.Builder("geojson-source")
                .data(GEOJSON_URL)
                .build()
            style.addSource(geoJsonSource)

            val symbolLayer = SymbolLayer("geojson-layer", "geojson-source")
            symbolLayer.iconImage("marker-icon")
            symbolLayer.iconAllowOverlap(true)
            symbolLayer.iconIgnorePlacement(true)
            style.addLayer(symbolLayer)
        }
    }


    fun addPointToMap(point: Point, forceAdd: Boolean = false) {
        mapView.mapboxMap.getStyle { style ->
            if (!forceAdd && isPointAdded) {
                removeSourceAndLayer(style)
                isPointAdded = false
            } else {
                addOrUpdateSource(style, point)
                addLayerIfNotExits(style)
                isPointAdded = true
            }
        }
    }

    fun addPointToMapLong(point: Point) {
        mapView.mapboxMap.getStyle { style ->
            addOrUpdateSource(style, point)
            addLayerIfNotExits(style)
        }
    }

    private fun removeSourceAndLayer(style: Style) {
        if (style.styleSourceExists(SOURCE_ID)) style.removeStyleSource(SOURCE_ID)
        if (style.styleLayerExists(LAYER_ID)) style.removeStyleLayer(LAYER_ID)
    }

    private fun addOrUpdateSource(style: Style, point: Point) {
        if (style.styleSourceExists(SOURCE_ID)) {
            style.getSourceAs<GeoJsonSource>(SOURCE_ID)?.geometry(point)
        } else {
            val geoJsonSource = GeoJsonSource.Builder(SOURCE_ID).geometry(point).build()
            style.addSource(geoJsonSource)
        }
    }

    private fun addLayerIfNotExits(style: Style) {
        if (!style.styleLayerExists(LAYER_ID)) {
            val symbolLayer = SymbolLayer(LAYER_ID, SOURCE_ID)
                .iconImage("ic_location-marker-icon")
                .iconAllowOverlap(true)
            style.addLayer(symbolLayer)
        }
    }

    fun saveFavoritePoint(name: String, latitude: Double, longitude: Double) {
        val database = AppDatabase.getDatabase(context)
        val favoritePoint = FavoritePoint(name = name, latitude = latitude, longitude = longitude)

        CoroutineScope(Dispatchers.IO).launch {
            database.favoritePointDao().insertFavoritePoint(favoritePoint)
        }
    }

    fun centerMapOnPoint(latitude: Double, longitude: Double) {
        val point = Point.fromLngLat(longitude, latitude)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build()
        )
        addPointToMap(point, forceAdd = true)
    }

    fun addAlertPointToMap(point: Point) {
        mapView.mapboxMap.getStyle { style ->
            val sourceId = "alert-point-source-${point.hashCode()}"
            val layerId = "alert-point-layer-${point.hashCode()}"

            addGeoJsonSourceIfNotExists(style, sourceId, point)
            addCircleLayerIfNotExists(style, layerId, sourceId)
            animateCircleLayer(style, layerId)

            val database = AppDatabase.getDatabase(context)
            CoroutineScope(Dispatchers.IO).launch {
                val existing = database.favoritePointDao()
                    .getAlertPointByLocation(point.latitude(), point.longitude())

                if (existing == null) {
                    val alertPoint = FavoritePoint(
                        name = context.getString(R.string.alert_point),
                        latitude = point.latitude(),
                        longitude = point.longitude(),
                        isAlertPoint = true
                    )
                    database.favoritePointDao().insertFavoritePoint(alertPoint)
                }
            }
        }
    }

    private fun addGeoJsonSourceIfNotExists(style: Style, sourceId: String, point: Point) {
        if (!style.styleSourceExists(sourceId)) {
            val geoJsonSource = GeoJsonSource.Builder(sourceId).geometry(point).build()
            style.addSource(geoJsonSource)
        }
    }

    private fun addCircleLayerIfNotExists(style: Style, layerId: String, sourceId: String) {
        if (!style.styleLayerExists(layerId)) {
            val circleLayer = CircleLayer(layerId, sourceId)
                .circleRadius(5.0)
                .circleColor("#FF0000")
                .circleOpacity(0.5)
            style.addLayer(circleLayer)
        }
    }

    private fun animateCircleLayer(style: Style, layerId: String) {
        val animator = ValueAnimator.ofFloat(5f, 20f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                style.getLayerAs<CircleLayer>(layerId)?.circleRadius(animatedValue.toDouble())
            }
        }
        animator.start()
    }


}