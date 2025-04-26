package com.example.geoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            loadGeoJson()
        }


    }

    private fun loadGeoJson() {
        mapView.getMapboxMap().getStyle { style ->
            val geoJsonSource = GeoJsonSource.Builder("geojson-source")
                .url("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_populated_places_simple.geojson")
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