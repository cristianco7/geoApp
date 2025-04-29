package com.example.geoapp.tools

import android.graphics.BitmapFactory
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.geoapp.R
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs

class MapManager(private val mapView: MapView) {

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


    fun addPointToMap(point: Point) {
        mapView.mapboxMap.getStyle { style ->
            if (isPointAdded) {
                removeSourceAndLayer(style)
                isPointAdded = false
            } else {
                addOrUpdateSource(style, point)
                addLayerIfNotExits(style)
                isPointAdded = true
            }
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

}