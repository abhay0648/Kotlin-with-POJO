package com.lalamove.lalamovetest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import controllerAll.Controller
import com.google.android.gms.maps.SupportMapFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.map_fragment.*
import staticClasses.DataBaseKeys

// class extend appcompat activity to create an activity to show map data on list click
class MapActivity : AppCompatActivity() {
    private var mController: Controller? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var imageUrl: String? = null
    private var description: String? = null
    private var latitudeDouble: Double? = null
    private var longitudeDouble: Double? = null

    // overrided function of activity lifecycle to create view, initialise controller, getting and setting data on views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_fragment)
        mController = applicationContext as Controller
        getData()
        setDataOnView()
        createMapShowMarker()
    }

    // getting data from the intent passed from the dashboard activity
    private fun getData() {
        val extras = intent.extras
        latitude = extras.getString(DataBaseKeys.LAT)
        longitude = extras.getString(DataBaseKeys.LNG)
        imageUrl = extras.getString(DataBaseKeys.IMAGE_URL)
        description = extras.getString(DataBaseKeys.DESCRIPTION)

        latitudeDouble = latitude!!.toDouble()
        longitudeDouble = longitude!!.toDouble()
    }

    // setting data on the views and setting images from the glide
    private fun setDataOnView() {
        descriptionTextView.text = description

        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(imageShow!!)
    }

    // create marker on map and setting upon the data of latitude and longitude
    private fun createMapShowMarker() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear()
            val googlePlex = CameraPosition.builder()
                .target(LatLng(latitudeDouble!!, longitudeDouble!!))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(latitudeDouble!!, longitudeDouble!!))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
    }
}