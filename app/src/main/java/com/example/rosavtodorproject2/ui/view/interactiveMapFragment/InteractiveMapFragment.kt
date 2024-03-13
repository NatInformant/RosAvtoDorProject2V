package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rosavtodorproject2.App

import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.databinding.FragmentInteractiveMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Runtime.getApplicationContext
import com.yandex.runtime.image.ImageProvider


class InteractiveMapFragment : Fragment() {
    private lateinit var binding: FragmentInteractiveMapBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private lateinit var mapView: MapView

    private val viewModel: InteractiveMapFragmentViewModel by viewModels { applicationComponent.getInteractiveMapViewModelFactory() }

    private var isPointAdding = false
    private var currentIconNumber: Int = -1
    private val iconsResources = listOf(
        R.drawable.image_car_accident_24dp,
        R.drawable.image_car_accident_24dp,
        R.drawable.image_car_accident_24dp,
        R.drawable.image_car_accident_24dp,
        R.drawable.petrol_station_icon,
        R.drawable.petrol_station_icon,
        R.drawable.petrol_station_icon,
        R.drawable.petrol_station_icon,
        R.drawable.petrol_station_icon,
    )
    private var currentIconPlacemark: com.yandex.mapkit.map.PlacemarkMapObject? = null

    private val BASE_LATITUDE: Double = 55.154
    private val BASE_LONGITUDE: Double = 61.4291

    private var locationManager: LocationManager? = null
    private var previousLocation: Location? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractiveMapBinding.inflate(layoutInflater, container, false)

        MapKitFactory.initialize(getApplicationContext())
        mapView = binding.mapview

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        mapView.map.move(
            CameraPosition(
                Point(BASE_LATITUDE, BASE_LONGITUDE),
                /* zoom = */ 8f,
                /* azimuth = */ 0f,
                /* tilt = */ 0f
            )
        )

        mapView.map.addInputListener(addingPointListener)

        viewModel.points.observe(viewLifecycleOwner)
        {
            addVerifiedPointsToInteractiveMap(it)
        }

        return binding.root
    }
    private val addingPointListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {

            if (!isPointAdding) {
                return
            }

            binding.confirmAdditionPointToMapFab.isEnabled = true

            val imageProvider =
                ImageProvider.fromResource(requireContext(), iconsResources[currentIconNumber])

            if (currentIconPlacemark == null) {
                currentIconPlacemark = mapView.map.mapObjects.addPlacemark()
                    .apply {
                        geometry = Point(point.latitude, point.longitude)
                        setIcon(imageProvider)
                    }

            } else {
                currentIconPlacemark?.geometry = Point(point.latitude, point.longitude)
            }

        }

        override fun onMapLongTap(p0: Map, p1: Point) {
            // Обработка долгого нажатия, если нужно
            // Пока не нужно, но может потом что-нибудь покумекаем
        }
    }

    private fun addVerifiedPointsToInteractiveMap(myPoints: List<MyPoint>) {

        mapView.map.mapObjects.clear()
        myPoints.forEach {
            val imageProvider =
                ImageProvider.fromResource(requireContext(), iconsResources[it.type])
            mapView.map.mapObjects.addPlacemark()
                .apply {
                    geometry = Point(it.latitude, it.longitude)
                    setIcon(imageProvider)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLocationPermission()

        binding.backToChatsPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_interactiveMapFragment_to_chatsFragment)
        }
        binding.addPointToMapFab.setOnClickListener {
            listenerForAddPointToMapFab(binding.anchorViewForPopupMenu)
        }
        binding.confirmAdditionPointToMapFab.setOnClickListener {
            listenerForConfirmAdditionPointFab(it)
        }
        binding.cancelAdditionPointToMapFab.setOnClickListener {
            listenerForCancelAdditionPointFab(it)
        }
    }
    private fun checkLocationPermission() {
        // Проверяем разрешение на использование местоположения
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Если разрешение не предоставлено, запрашиваем его
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Если разрешение предоставлено, запрашиваем обновления местоположения
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        }
    }
    @SuppressLint("MissingPermission")
    val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Разрешение на использование местоположения предоставлено
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
            } else {
                // Разрешение на использование местоположения не предоставлено
                Toast.makeText(
                    requireContext(),
                    "Без доступа к местоположению мы не сможем отправить вам точки",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (previousLocation == null) {
                previousLocation = location
                mapView.map.move(
                    CameraPosition(
                        Point(location.latitude, location.longitude),
                        /* zoom = */ 8f,
                        /* azimuth = */ 0f,
                        /* tilt = */ 0f
                    )
                )
                return
            }
            val distance = previousLocation!!.distanceTo(location)

            if (distance >= 2000) {
                mapView.map.move(
                    CameraPosition(
                        Point(location.latitude, location.longitude),
                        /* zoom = */ 8f,
                        /* azimuth = */ 0f,
                        /* tilt = */ 0f
                    )
                )
                previousLocation = location
            }
        }
    }
    private fun listenerForAddPointToMapFab(anchorView: View) {
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.MyPopupMenuStyle)
        val popupMenu = PopupMenu(wrapper, anchorView, Gravity.END)

        popupMenu.inflate(R.menu.add_point_menu)
        popupMenu.setOnMenuItemClickListener {
            listenerForMenuItemClick(it)
        }

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("Main", "Error showing menu icons", e)
        } finally {
            popupMenu.show()
        }
    }

    private fun listenerForMenuItemClick(menuItem: MenuItem): Boolean {
        binding.addPointToMapFab.visibility = View.INVISIBLE
        binding.cancelAdditionPointToMapFab.visibility = View.VISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.VISIBLE

        currentIconNumber = menuItem.order
        isPointAdding = true
        return true
    }

    private fun listenerForCancelAdditionPointFab(fabView: View): Boolean {
        binding.addPointToMapFab.visibility = View.VISIBLE
        binding.cancelAdditionPointToMapFab.visibility = View.INVISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.INVISIBLE
        isPointAdding = false
        currentIconNumber = -1

        if (currentIconPlacemark != null) {
            mapView.map.mapObjects.remove(currentIconPlacemark!!)
            currentIconPlacemark = null
        }
        binding.confirmAdditionPointToMapFab.isEnabled = false

        return true
    }

    private fun listenerForConfirmAdditionPointFab(fabView: View): Boolean {
        binding.addPointToMapFab.visibility = View.VISIBLE
        binding.cancelAdditionPointToMapFab.visibility = View.INVISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.INVISIBLE
        isPointAdding = false

        viewModel.addPoint(
            type = currentIconNumber,
            latitude = currentIconPlacemark?.geometry!!.latitude,
            longitude = currentIconPlacemark?.geometry!!.longitude,
            text = "Зачем я существую?"
        )

        currentIconNumber = -1
        currentIconPlacemark = null
        binding.confirmAdditionPointToMapFab.isEnabled = false

        return true
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}