package com.example.myhousev3.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.Geocoder
import android.location.Location
import android.net.Network
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Query
import com.example.myhousev3.R
import com.example.myhousev3.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Icon
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.jsoup.Jsoup
import java.io.IOException
import org.json.JSONObject
import java.net.URL
import java.util.Locale


class MapFragment : Fragment(), UserLocationObjectListener, Session.SearchListener, CameraListener{

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    lateinit var locationmapkit: UserLocationLayer
    lateinit var searchEdit: EditText
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.map.move(CameraPosition(Point(64.526387, 40.561472),13.0f, 0.0f,0.0f),
        Animation(Animation.Type.SMOOTH, 0f),null)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        var mapKit:MapKit = MapKitFactory.getInstance()
        requestLocationPermission()
        var probki = mapKit.createTrafficLayer(mapView.mapWindow)
        probki.isTrafficVisible = false

        locationmapkit = mapKit.createUserLocationLayer(mapView.mapWindow)
        locationmapkit.isVisible = true
        locationmapkit.setObjectListener(this)
        SearchFactory.initialize(requireContext())
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)
        searchEdit = binding.searchEdit
        searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                submitQuery(searchEdit.text.toString())
            }
            false
        }


        return binding.root
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    private fun submitQuery(query: String){
        searchSession = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
        SearchOptions(), this)
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }
    private inner class ReverseGeocodingTask {
        fun getCityFromCoordinates(location: Location) {
            GlobalScope.launch(Dispatchers.Main) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                }

                if (addresses != null && addresses.isNotEmpty()) {
                    val city = addresses[0].locality
                    if (city != null) {
                        val message = "Текущий город: $city"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.d("log", message)
                    } else {
                        Toast.makeText(requireContext(), "Не удалось определить город", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Не удалось определить город", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        ReverseGeocodingTask().getCityFromCoordinates(location)
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    Toast.makeText(
                        requireContext(),
                        "Не удалось получить местоположение: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // Если разрешения не предоставлены, запросите их у пользователя
            requestLocationPermission()
        }

        locationmapkit.setAnchor(
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.5).toFloat()),
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.user_arrow))
        val picIcon = userLocationView.pin.useCompositeIcon()
        picIcon.setIcon(
            "icon",
            ImageProvider.fromResource(requireContext(), R.drawable.user_arrow),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(1f)
        )
        picIcon.setIcon(
            "pin",
            ImageProvider.fromResource(requireContext(), R.drawable.nothing),
            IconStyle().setAnchor(PointF(0.5f, 05f)).setRotationType(RotationType.ROTATE).setZIndex(1f).setScale(0.5f)
        )

        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects: MapObjectCollection = mapView.map.mapObjects
        mapObjects.clear()
        for (searchResult in response.collection.children){
            val resultLocation:Point = searchResult.obj!!.geometry[0].point!!
            if (response!=null){
                mapObjects.addPlacemark(resultLocation, ImageProvider.fromResource(requireContext(), R.drawable.search_result))
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage="Неизвестная ошибка"
        if(error is RemoteError){
            errorMessage = "Беспроводная ошибка"
        }else if(error is NetworkError){
            errorMessage = "Проблема с интернетом"
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished){
            submitQuery(searchEdit.text.toString())
        }
    }



}