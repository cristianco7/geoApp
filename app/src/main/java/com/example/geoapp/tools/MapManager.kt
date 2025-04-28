package com.example.geoapp.tools

import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource

class MapManager(private val mapView: MapView) {

    companion object {
        private const val GEOJSON_URL =
            "https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_populated_places_simple.geojson"
    }

    fun initializeMap(onStyleLoaded: () -> Unit) {
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
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

}