package com.example.rosavtodorproject2.ui.view.roadPlacesInformationFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.databinding.RoadPlacesInformationFragmentBinding
import com.example.rosavtodorproject2.ioc.RoadInformationViewModelFactory
import com.example.rosavtodorproject2.ioc.RoadPlacesInformationViewModelFactory
import com.example.rosavtodorproject2.ioc.applicationInstance
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.Runtime
import com.yandex.runtime.Runtime.getApplicationContext
import javax.inject.Inject

class RoadPlacesInformationFragment : Fragment() {

    private lateinit var binding: RoadPlacesInformationFragmentBinding

    private var roadPlacesAdapter: RoadPlacesListAdapter = RoadPlacesListAdapter(
        roadPlacesDiffUtil = RoadPlacesDiffUtil(),
        getRoadPlaceTitle = ::getRoadPlaceTitle,
        getDistanceToRoadPlace = ::getDistanceToRoadPlace,
        onRoadPlaceItemButtonClick = ::goToYandexMapButtonClick,
    )
    private var eventRoadPlacesAdapter: EventRoadPlacesListAdapter = EventRoadPlacesListAdapter(
        roadPlacesDiffUtil = RoadPlacesDiffUtil(),
        getRoadPlaceTitle = ::getRoadPlaceTitle,
        getDistanceToRoadPlace = ::getDistanceToRoadPlace,
        onRoadPlaceItemButtonClick = ::goToYandexMapButtonClick,
    )
    @Inject
    lateinit var viewModelFactory: RoadPlacesInformationViewModelFactory
    private val viewModel: RoadPlacesInformationFragmentViewModel by viewModels { viewModelFactory }
    private var roadName: String? = null
    private var roadPlaceType: String? = null
    private val roadPlaceTypeToNameResource: Map<String, Int> = mapOf(
        Pair("Cafe", R.string.cafe),
        Pair("Hotel", R.string.guesthouse),
        Pair("GasStation", R.string.petrol_station),
        Pair("CarService", R.string.car_service),
        Pair("ElectricFillingStation", R.string.car_recharge_station),
        Pair("Event", R.string.event),
    )
    private var locationManager: LocationManager? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.applicationInstance.applicationComponent.injectRoadPlacesInformationFragment(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadPlacesInformationFragmentBinding.inflate(layoutInflater, container, false)

        val roadPlacesListTitleStringResource =
            arguments?.getInt("roadPlacesListTitleStringResource")

        binding.roadPlacesListTitle.text =
            getString(roadPlacesListTitleStringResource ?: R.string.error_road_places_list_title)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roadName = arguments?.getString("roadName")
        roadPlaceType = arguments?.getString("roadPlaceType")

        if (roadPlaceType != "Event") {
            setUpRoadPlacesList()
        } else {
            setUpEventRoadPlacesList()
        }

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        updateOnlyRoadPlaces()

        checkLocationPermission()

        binding.backToRoadInformationFragmentPanelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setUpRoadPlacesList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadPlacesRecyclerList

        roadsListRecyclerView.adapter = roadPlacesAdapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        roadsListRecyclerView.layoutManager = layoutManager

        viewModel.roadPlaces.observe(viewLifecycleOwner) { httpResponseState ->
            //Для справки самому себе - в первый раз мы проваливаемся сюда
            //НЕ при изменении значения, за которым следим, а когда фрагмент инициализировался,
            //а уже дальше проваливаемся только при изменениях!

            binding.swipeRefreshLayoutForRoadPlacesList.isRefreshing = false

            when (httpResponseState) {
                is HttpResponseState.Success -> {
                    roadPlacesAdapter.submitList(httpResponseState.value)
                }

                is HttpResponseState.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {

                }
            }
        }

        binding.swipeRefreshLayoutForRoadPlacesList.setOnRefreshListener {
            updateOnlyRoadPlaces()
        }
    }

    private fun setUpEventRoadPlacesList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadPlacesRecyclerList

        roadsListRecyclerView.adapter = eventRoadPlacesAdapter

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        roadsListRecyclerView.layoutManager = layoutManager

        viewModel.roadPlaces.observe(viewLifecycleOwner) { httpResponseState ->
            //Для справки самому себе - в первый раз мы проваливаемся сюда
            //НЕ при изменении значения, за которым следим, а когда фрагмент инициализировался,
            //а уже дальше проваливаемся только при изменениях!

            binding.swipeRefreshLayoutForRoadPlacesList.isRefreshing = false

            when (httpResponseState) {
                is HttpResponseState.Success -> {
                    eventRoadPlacesAdapter.submitList(httpResponseState.value)
                }

                is HttpResponseState.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.swipeRefreshLayoutForRoadPlacesList.setOnRefreshListener {
            updateOnlyRoadPlaces()
        }
    }

    private fun updateOnlyRoadPlaces() {
        if (requireContext().applicationInstance.currentUserPosition == null) {
            return
        }
        viewModel.updateOnlyRoadPlaces(
            roadName ?: "",
            roadPlaceType ?: "",
            Coordinates(
                requireContext().applicationInstance.currentUserPosition!!.latitude,
                requireContext().applicationInstance.currentUserPosition!!.longitude
            )
        )
    }

    private fun updateRoadPlacesAndMapPoints() {
        viewModel.updateRoadPlacesAndMapPoints(
            roadName ?: "",
            roadPlaceType ?: "",
            Coordinates(
                requireContext().applicationInstance.currentUserPosition!!.latitude,
                requireContext().applicationInstance.currentUserPosition!!.longitude
            )
        )
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
                LocationManager.NETWORK_PROVIDER,
                requireContext().applicationInstance.LOCATION_UPDATES_TIME_INTERVAL,
                requireContext().applicationInstance.LOCATION_UPDATES_MIN_DISTANCE,
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
                    LocationManager.NETWORK_PROVIDER,
                    requireContext().applicationInstance.LOCATION_UPDATES_TIME_INTERVAL,
                    requireContext().applicationInstance.LOCATION_UPDATES_MIN_DISTANCE,
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
            //Вернуть костыль, ибо он хоть и костыль но НАДЁЖНЫЙ
            if (getApplicationContext().applicationInstance.currentUserPosition == null) {
                getApplicationContext().applicationInstance.currentUserPosition = location

                updateRoadPlacesAndMapPoints()

                //Оно здесь нужно, на случай если это будет первым окном,
                //в котором мы определили позицию пользователя.
                getApplicationContext().applicationInstance.currentCameraPosition = CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f
                )

                return
            }

            val distance = getApplicationContext().applicationInstance.currentUserPosition!!.distanceTo(location)

            if (distance >= 5000) {
                getApplicationContext().applicationInstance.currentUserPosition = location

                updateRoadPlacesAndMapPoints()
            }
        }
    }

    private fun getRoadPlaceTitle(roadPlaceName: String) =
        getString(
            R.string.verified_point_name_format,
            getString(roadPlaceTypeToNameResource[roadPlaceType]!!),
            roadPlaceName
        )

    private fun getDistanceToRoadPlace(distance: Double) =
        getString(
            R.string.distance_to_road_place_format,
            distance
        )

    private fun goToYandexMapButtonClick(currentRoadPlaceCoordinates: Coordinates) {
        val url =
            "https://yandex.ru/maps/?rtext=" +
                    "${requireContext().applicationInstance.currentUserPosition!!.latitude}," +// точка
                    "${requireContext().applicationInstance.currentUserPosition!!.longitude}" +// начала пути
                    "~" +
                    "${currentRoadPlaceCoordinates.latitude}," +//точка
                    "${currentRoadPlaceCoordinates.longitude}" +//конца пути
                    "&rtt=auto"

        val intent = Intent(Intent.ACTION_VIEW) // Создаем новый Intent

        intent.data = Uri.parse(url) // Устанавливаем URL-адрес для Intent

        startActivity(intent) // Запускаем новое Activity с помощью Intent
    }
}