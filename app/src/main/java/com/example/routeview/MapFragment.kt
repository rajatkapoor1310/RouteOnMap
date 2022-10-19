package com.example.routeview

import android.Manifest
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.navigationonmap.R
import com.example.navigationonmap.databinding.FragmentMapBinding

import com.example.yantram.Utilities.AnimationUtils
import com.example.yantram.Utilities.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import org.json.JSONObject


class MapFragment : Fragment(), OnMapReadyCallback {


    var fragment: FragmentMapBinding? = null
    private val binding get() = fragment!!
    private var mMap: GoogleMap? = null
    lateinit var mDestination: LatLng
    private var mDestinationLat: Double? = null
    private var mDestinationLang: Double? = null
    lateinit var mMarkerPoints: ArrayList<LatLng>
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback
    private var currentLatLng: LatLng? = null
    private var destinationMarker: Marker? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var previousLatLngFromServer: LatLng? = null
    private var currentLatLngFromServer: LatLng? = null
    private var movingCabMarker: Marker? = null

    var MY_PERMISSIONS_REQUEST_LOCATION = 99
    var MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66

    companion object {
        @JvmStatic
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragment = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMarkerPoints = ArrayList()
        mDestinationLang = 76.7179
        mDestinationLat = 30.7046
        mDestination = LatLng(mDestinationLat!!, mDestinationLang!!)

        permissionOfMap()
        initMap(savedInstanceState)
        onBackCallBack()
    }


    private fun permissionOfMap() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->

                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }









    private fun onBackCallBack() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private fun initMap(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isRotateGesturesEnabled = true
        mMap!!.uiSettings.isScrollGesturesEnabled = true
        mMap!!.uiSettings.isTiltGesturesEnabled = true
        setUpLocationListener()
    }

    private fun setUpLocationListener() {
        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
        // for getting the current location update after every 2 seconds
        val locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if(!isVisible){
                    return
                }
                for (location in locationResult.locations) {
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    enableMyLocationOnMap()
                    moveCamera(currentLatLng)
                    animateCamera(currentLatLng)
                    updateCabLocation(currentLatLng!!)
                    updateRoute()
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Looper.myLooper()?.let {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                it
            )
        }
    }

    private fun moveCamera(latLng: LatLng?) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng!!))
    }

    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng!!).zoom(15.5f).build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addCarMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(requireActivity()))
        return mMap!!.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))!!
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getDestinationBitmap())
        return mMap!!.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))!!
    }

    private fun updateCabLocation(latLng: LatLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng)
        }
        if (previousLatLngFromServer == null) {
            currentLatLngFromServer = latLng
            previousLatLngFromServer = currentLatLngFromServer
            movingCabMarker?.position = currentLatLngFromServer!!
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLngFromServer)
        } else {
            previousLatLngFromServer = currentLatLngFromServer
            currentLatLngFromServer = latLng
            val valueAnimator = AnimationUtils.cabAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentLatLngFromServer != null && previousLatLngFromServer != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLngFromServer!!.latitude + (1 - multiplier) * previousLatLngFromServer!!.latitude,
                        multiplier * currentLatLngFromServer!!.longitude + (1 - multiplier) * previousLatLngFromServer!!.longitude
                    )
                    movingCabMarker?.position = nextLocation
                    movingCabMarker?.setAnchor(0.5f, 0.5f)
                    val rotation = MapUtils.getRotation(previousLatLngFromServer!!, nextLocation)
                    if (!rotation.isNaN()) {
                        movingCabMarker?.rotation = rotation
                    }
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }


    private fun enableMyLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap!!.isMyLocationEnabled = true
    }

    private fun updateRoute(){
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.api_key))
            .build()
        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(com.google.maps.model.LatLng(currentLatLng!!.latitude, currentLatLng!!.longitude))
        directionsApiRequest.destination(com.google.maps.model.LatLng(mDestination.latitude, mDestination.longitude))

        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d("Track", "onResult : $result")
                val routeList = result.routes
                // Actually it will have zero or 1 route as we haven't asked Google API for multiple paths

                if (routeList.isEmpty()) {

                } else {
                    val pickUpPath = arrayListOf<com.google.maps.model.LatLng>()
                    val latLngList: MutableList<LatLng>  = arrayListOf()
                    for (route in routeList) {
                        val path = route.overviewPolyline.decodePath()
                        pickUpPath.addAll(path)
                        pickUpPath.forEach {
                            latLngList.add(LatLng(it.lat, it.lng))
                        }
                    }

                    Handler(Looper.getMainLooper()).post(
                        Runnable {
                            showPath(latLngList)
                        }
                    )
                }


            }

            override fun onFailure(e: Throwable) {
                Log.d("TAG", "onFailure : ${e.message}")
                val jsonObjectFailure = JSONObject()
                jsonObjectFailure.put("type", "directionApiFailed")
                jsonObjectFailure.put("error", e.message)
            }
        })
    }


    private fun showPath(latLngList: List<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
        greyPolyLine?.remove()
        greyPolyLine = mMap!!.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.width(5f)
        blackPolylineOptions.color(Color.BLACK)
        blackPolyline?.remove()
        blackPolyline = mMap!!.addPolyline(blackPolylineOptions)

        if(destinationMarker==null) {
            destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
            destinationMarker?.setAnchor(0.5f, 0.5f)
        }

        val polylineAnimator = AnimationUtils.polyLineAnimator()
        polylineAnimator.addUpdateListener(fun(valueAnimator: ValueAnimator) {
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (greyPolyLine?.points!!.size * (percentValue / 100.0f)).toInt()
            blackPolyline?.points = greyPolyLine?.points!!.subList(0, index)
        })
        polylineAnimator.start()
        val distance = Utils.instance.getDistanceOfList(latLngList)

        Toast.makeText(requireActivity(),  Utils.instance.convertMeterToKilometer(distance), Toast.LENGTH_SHORT).show()

//         Utils.instance.convertMeterToKilometer(distance)
    }


    override fun onDestroy() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

}