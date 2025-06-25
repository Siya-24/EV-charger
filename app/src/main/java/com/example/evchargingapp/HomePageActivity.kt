package com.example.evchargingapp
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomePageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Map initialization
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup drawer logic and QR code listeners if needed
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableUserLocation()
        loadNearbyStations()
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 101)
        }
    }

    private fun loadNearbyStations() {
        val stations = listOf(
            LatLng(28.5944, 77.0460),
            LatLng(28.5922, 77.0485),
            LatLng(28.5938, 77.0500)
        )

        stations.forEachIndexed { index, location ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Station ${index + 1}")
            )
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stations[0], 15f))
    }
}
