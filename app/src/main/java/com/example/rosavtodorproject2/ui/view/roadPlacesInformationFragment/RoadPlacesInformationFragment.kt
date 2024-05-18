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
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition

class RoadPlacesInformationFragment : Fragment() {

    private lateinit var binding: RoadPlacesInformationFragmentBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private var adapter: RoadPlacesListAdapter = RoadPlacesListAdapter(
        roadPlacesDiffUtil = RoadPlacesDiffUtil(),
        getRoadPlaceTitle = ::getRoadPlaceTitle,
        getDistanceToRoadPlace = ::getDistanceToRoadPlace,
        onRoadPlaceItemButtonClick = ::goToYandexMapButtonClick,
    )

    private val viewModel: RoadPlacesInformationFragmentViewModel by viewModels { applicationComponent.getRoadPlacesInformationViewModelFactory() }
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RoadPlacesInformationFragmentBinding.inflate(layoutInflater, container, false)
        roadName = arguments?.getString("roadName")
        roadPlaceType = arguments?.getString("roadPlaceType")
        val roadPlacesListTitleStringResource =
            arguments?.getInt("roadPlacesListTitleStringResource")

        setUpRoadPlacesList()

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        //А надо это здесь делать?
        updateOnlyRoadPlaces()

        binding.roadPlacesListTitle.text =
            getString(roadPlacesListTitleStringResource ?: R.string.error_road_places_list_title)
        return binding.root
    }

    private fun setUpRoadPlacesList() {
        val roadsListRecyclerView: RecyclerView =
            binding.roadPlacesRecyclerList

        roadsListRecyclerView.adapter = adapter

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
            when (httpResponseState) {
                is HttpResponseState.Success -> {
                    adapter.submitList(httpResponseState.value)
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
            //А это точно здесь должно быть?.....
            //Скорее всего нет
            binding.swipeRefreshLayoutForRoadPlacesList.isRefreshing = false
        }
    }

    private fun updateOnlyRoadPlaces() {
        if (App.getInstance().currentUserPosition == null) {
            return
        }
        viewModel.updateOnlyRoadPlaces(
            roadName ?: "",
            roadPlaceType ?: "",
            Coordinates(
                App.getInstance().currentUserPosition!!.latitude,
                App.getInstance().currentUserPosition!!.longitude
            )
        )

    }

    private fun updateRoadPlacesAndMapPoints() {
        viewModel.updateRoadPlacesAndMapPoints(
            roadName ?: "",
            roadPlaceType ?: "",
            Coordinates(
                App.getInstance().currentUserPosition!!.latitude,
                App.getInstance().currentUserPosition!!.longitude
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLocationPermission()

        binding.backToRoadInformationFragmentPanelButton.setOnClickListener {
            val action =
                RoadPlacesInformationFragmentDirections.actionRoadPlacesInformationFragmentToRoadInformationFragment(
                    roadName ?: ""
                )
            findNavController().navigate(action)
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
                LocationManager.NETWORK_PROVIDER,
                App.getInstance().LOCATION_UPDATES_TIME_INTERVAL,
                App.getInstance().LOCATION_UPDATES_MIN_DISTANCE,
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
                    App.getInstance().LOCATION_UPDATES_TIME_INTERVAL,
                    App.getInstance().LOCATION_UPDATES_MIN_DISTANCE,
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
            if (App.getInstance().currentUserPosition == null) {
                App.getInstance().currentUserPosition = location

                updateRoadPlacesAndMapPoints()

                //Оно здесь нужно, на случай если это будет первым окном,
                //в котором мы определили позицию пользователя.
                App.getInstance().currentCameraPosition = CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f
                )

                return
            }

            val distance = App.getInstance().currentUserPosition!!.distanceTo(location)

            if (distance >= 5000) {
                App.getInstance().currentUserPosition = location

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
                    //пока так, потом верну как было
                    "${App.getInstance().currentUserPosition!!.latitude}," +// точка
                    "${App.getInstance().currentUserPosition!!.longitude}" +// начала пути
                    "~" +
                    "${currentRoadPlaceCoordinates.latitude}," +//точка
                    "${currentRoadPlaceCoordinates.longitude}" +//конца пути
                    "&rtt=auto"

        val intent = Intent(Intent.ACTION_VIEW) // Создаем новый Intent

        intent.data = Uri.parse(url) // Устанавливаем URL-адрес для Intent

        startActivity(intent) // Запускаем новое Activity с помощью Intent
    }
}