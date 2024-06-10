package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.databinding.CreateDescriptionForAddingPointPopupWindowBinding
import com.example.rosavtodorproject2.databinding.EventPointPopupWindowBinding
import com.example.rosavtodorproject2.databinding.FilterPointsCheckboxPopupWindowBinding
import com.example.rosavtodorproject2.databinding.FragmentInteractiveMapBinding
import com.example.rosavtodorproject2.databinding.UnverifiedPointPopupWindowBinding
import com.example.rosavtodorproject2.databinding.VerifiedPointPopupWindowBinding
import com.example.rosavtodorproject2.ioc.InteractiveMapViewModelFactory
import com.example.rosavtodorproject2.ioc.applicationInstance
import com.example.rosavtodorproject2.ui.model.PhotoElementModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Runtime.getApplicationContext
import com.yandex.runtime.image.ImageProvider
import javax.inject.Inject


class InteractiveMapFragment : Fragment() {
    private lateinit var binding: FragmentInteractiveMapBinding
    private lateinit var bindingFilterPointsCheckboxPopupWindow: FilterPointsCheckboxPopupWindowBinding
    private lateinit var bindingVerifiedPointPopupWindow: VerifiedPointPopupWindowBinding
    private lateinit var bindingEventPointPopupWindow: EventPointPopupWindowBinding
    private lateinit var bindingUnverifiedPointPopupWindow: UnverifiedPointPopupWindowBinding
    private lateinit var bindingCreateDescriptionForAddingPointPopupWindow: CreateDescriptionForAddingPointPopupWindowBinding

    private lateinit var mapView: MapView //Так, вот это кнч обьект View и он может утечь по памяти,
    // если его не null-ить в OnDestroyView, но в Доке Яндекса, не сказано что так делать нужно,
    // так что ХЗ

    @Inject
    lateinit var viewModelFactory: InteractiveMapViewModelFactory
    private val viewModel: InteractiveMapFragmentViewModel by viewModels { viewModelFactory }

    private var isPointAdding = false
    private var isPointDescriptionCreating = false
    private var currentIconNumber = -1
    private var reliability: Int = 1
    private var addingPointDescriptionPopupWindow: PopupWindow? = null

    private var photosListAdapter: PhotosListAdapter = PhotosListAdapter(
        photosDiffUtil = PhotosDiffUtil(),
    )

    private var currentPointsList = listOf<MyPoint>()

    private var pointIconsList = listOf<ImageProvider>()

    private val mapPointTypeToNameResource: kotlin.collections.Map<Int, Int> = mapOf(
        Pair(0, R.string.petrol_station),
        Pair(1, R.string.cafe),
        Pair(2, R.string.car_service),
        Pair(3, R.string.guesthouse),
        Pair(4, R.string.car_recharge_station),
        Pair(5, R.string.event),
        Pair(6, R.string.road_accident_menu_item_title),
        Pair(7, R.string.pothole_menu_item_title),
        Pair(8, R.string.obstruction_menu_item_title),
        Pair(9, R.string.bandit_menu_item_title),
    )

    private var changedFilterStatePointType: Int = -1
    private var currentIconPlacemark: PlacemarkMapObject? = null

    private val BASE_LATITUDE: Double = 55.154
    private val BASE_LONGITUDE: Double = 61.4291
    private val userAreaCircleRadius = 100000f
    private var locationManager: LocationManager? = null
    private var userLocationLayer: UserLocationLayer? = null
    private var roadName: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.applicationInstance.applicationComponent.injectInteractiveMapFragment(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractiveMapBinding.inflate(layoutInflater, container, false)

        MapKitFactory.initialize(getApplicationContext())
        mapView = binding.mapview

        setUpBindingsForPopupWindows()

        setUpCameraPosition()

        if (requireContext().applicationInstance.currentUserPosition != null) {
            binding.updateMapPointsFab.isEnabled = true
            binding.addPointToMapFab.isEnabled = true
            binding.filtersButton.isEnabled = true
        }

        pointIconsList = listOf(
            ImageProvider.fromResource(requireContext(), R.drawable.petrol_station_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.cafe_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.car_service_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.guesthouse_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.car_recharge_station_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.event_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.image_car_accident_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.image_pothole_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.image_stone_on_road_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.image_pistol_icon),
        )

        return binding.root
    }

    private fun setUpBindingsForPopupWindows() {
        bindingFilterPointsCheckboxPopupWindow =
            FilterPointsCheckboxPopupWindowBinding.inflate(layoutInflater, binding.root, false)

        bindingFilterPointsCheckboxPopupWindow.petrolStationCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[0]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )

        bindingFilterPointsCheckboxPopupWindow.cafeCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[1]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.carServiceCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[2]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.guesthouseCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[3]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.carRechargeStationCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[4]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.eventCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[5]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.incidentsCheckBox.also {
            it.isChecked = requireContext().applicationInstance.listFilterStatesForPointType[6]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )

        bindingVerifiedPointPopupWindow = VerifiedPointPopupWindowBinding.inflate(layoutInflater, binding.root, false)
        bindingUnverifiedPointPopupWindow =
            UnverifiedPointPopupWindowBinding.inflate(layoutInflater, binding.root, false)
        bindingEventPointPopupWindow = EventPointPopupWindowBinding.inflate(layoutInflater, binding.root, false)

        bindingCreateDescriptionForAddingPointPopupWindow =
            CreateDescriptionForAddingPointPopupWindowBinding.inflate(layoutInflater, binding.root, false)

        bindingCreateDescriptionForAddingPointPopupWindow.addedPhotosList.adapter =
            photosListAdapter
        bindingCreateDescriptionForAddingPointPopupWindow.addedPhotosList.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        bindingCreateDescriptionForAddingPointPopupWindow.addPhotoToPointButton.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            launchPhotosPicker()
        }

        bindingCreateDescriptionForAddingPointPopupWindow
            .confirmDescriptionAddingButton.setOnClickListener {
                listenerForConfirmDescriptionAddingButton()
            }
        bindingCreateDescriptionForAddingPointPopupWindow
            .cancelDescriptionAddingButton.setOnClickListener {
                listenerForCancelDescriptionAddingButton()
            }
    }

    private val checkedChangeListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
            if (button?.text == getString(R.string.incidents)) {
                for (x in 6..9) {
                    changedFilterStatePointType = x
                    requireContext().applicationInstance.listFilterStatesForPointType[changedFilterStatePointType] =
                        isChecked
                    if (isChecked) {
                        addCurrentTypePointsToMap()
                    } else {
                        removeCurrentTypePointsFromMap()
                    }
                }
                return
            }

            when (button?.text) {
                getString(R.string.petrol_station) -> changedFilterStatePointType = 0
                getString(R.string.cafe) -> changedFilterStatePointType = 1
                getString(R.string.car_service) -> changedFilterStatePointType = 2
                getString(R.string.guesthouse) -> changedFilterStatePointType = 3
                getString(R.string.car_recharge_station) -> changedFilterStatePointType = 4
                getString(R.string.event) -> changedFilterStatePointType = 5
            }

            requireContext().applicationInstance.listFilterStatesForPointType[changedFilterStatePointType] = isChecked

            if (isChecked) {
                addCurrentTypePointsToMap()
            } else {
                removeCurrentTypePointsFromMap()
            }
        }
    }

    private fun addCurrentTypePointsToMap() {
        currentPointsList.filter { it.type == changedFilterStatePointType }.forEach {
            addCurrentPointToMap(it)
        }
    }

    private fun removeCurrentTypePointsFromMap() {
        mapView.map.mapObjects.traverse(removingMapObjectVisitor)
    }

    private fun setUpCameraPosition() {
        if (requireContext().applicationInstance.currentCameraPosition == null) {
            mapView.map.move(
                CameraPosition(
                    Point(BASE_LATITUDE, BASE_LONGITUDE),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f,
                ),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            mapView.map.move(
                requireContext().applicationInstance.currentCameraPosition!!,
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    //Храним все listener-ы для работы с ЯК в переменных, т.к. слабые ссылки.
    private val removingMapObjectVisitor = object : MapObjectVisitor {
        override fun onPlacemarkVisited(mapObject: PlacemarkMapObject) {
            val currentPointInformation = mapObject.userData as? MyPoint ?: return
            if (currentPointInformation.type == changedFilterStatePointType) {
                mapView.map.mapObjects.remove(mapObject)
            }
        }

        override fun onPolylineVisited(p0: PolylineMapObject) {
            return
        }

        override fun onPolygonVisited(p0: PolygonMapObject) {
            return
        }

        override fun onCircleVisited(p0: CircleMapObject) {
            return
        }

        override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
            return true
        }

        override fun onCollectionVisitEnd(p0: MapObjectCollection) {
            return
        }

        override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
            return true
        }

        override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {
            return
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roadName = arguments?.getString("roadName")

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        checkLocationPermission()

        mapView.map.addInputListener(addingNewPointListener)

        viewModel.points.observe(viewLifecycleOwner)
        { httpResponseState ->
            when (httpResponseState) {
                is HttpResponseState.Success -> {
                    currentPointsList = httpResponseState.value
                    addPointsToInteractiveMap(httpResponseState.value)
                }
                is HttpResponseState.Failure -> {
                    currentPointsList = emptyList()
                    addPointsToInteractiveMap(emptyList())

                    Toast.makeText(
                        requireContext(),
                        "Без доступа к интернету приложение не сможет работать",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    currentPointsList = emptyList()
                    addPointsToInteractiveMap(emptyList())

                }
            }
        }

        if (roadName != null) {
            binding.backToChatsPanelButton.setOnClickListener {
                val action =
                    InteractiveMapFragmentDirections.actionInteractiveMapFragmentToRoadInformationFragment(
                        roadName ?: ""
                    )
                findNavController().navigate(action)
            }
        } else {
            binding.backToChatsPanelButton.setOnClickListener {
                findNavController().navigate(R.id.action_interactiveMapFragment_to_roadsChooseFragment)
            }
        }
        binding.filtersButton.setOnClickListener {
            listenerForFiltersButton(it)
        }
        binding.showCurrentUserPositionFab.setOnClickListener {
            listenerForShowCurrentUserPositionFab()
        }
        binding.addPointToMapFab.setOnClickListener {
            listenerForAddPointToMapFab(binding.anchorViewForPopupMenu)
        }
        binding.updateMapPointsFab.setOnClickListener {
            viewModel.updatePoints(
                requireContext().applicationInstance.currentUserPosition!!.latitude,
                requireContext().applicationInstance.currentUserPosition!!.longitude
            )
        }
        binding.confirmAdditionPointToMapFab.setOnClickListener {
            listenerForConfirmAdditionPointFab()
        }
        binding.cancelAdditionPointToMapFab.setOnClickListener {
            listenerForCancelAdditionPointFab()
        }

        setUpFragmentCurrentState(savedInstanceState)
    }

    private val addingNewPointListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {

            if (!isPointAdding) {
                return
            }

            binding.confirmAdditionPointToMapFab.isEnabled = true
            reliability = 2
            if (currentIconPlacemark == null) {
                setUpCurrentIconPlacemark(point)
            } else {
                currentIconPlacemark?.geometry = Point(point.latitude, point.longitude)
            }
        }

        override fun onMapLongTap(p0: Map, p1: Point) {
            // Обработка долгого нажатия, если нужно
            // Пока не нужно, но может потом что-нибудь покумекаем
        }
    }
    private fun addPointsToInteractiveMap(myPoints: List<MyPoint>) {

        if (currentIconPlacemark != null) {
            val currentIconPlacemarkPoint = currentIconPlacemark!!.geometry
            mapView.map.mapObjects.clear()
            setUpCurrentIconPlacemark(currentIconPlacemarkPoint)
        } else {
            mapView.map.mapObjects.clear()
        }

        if (requireContext().applicationInstance.currentUserPosition != null) {
            setUpUserAreaCircle()
        }

        myPoints.forEach {
            if (requireContext().applicationInstance.listFilterStatesForPointType[it.type]) {
                addCurrentPointToMap(it)
            }
        }
    }
    private fun setUpCurrentIconPlacemark(point: Point) {
        currentIconPlacemark = mapView.map.mapObjects.addPlacemark()
            .apply {
                geometry = Point(point.latitude, point.longitude)
                setIcon(pointIconsList[currentIconNumber])
                setIconStyle(
                    IconStyle().apply {
                        scale = 1.5f
                        zIndex = 10f
                    }
                )
            }
    }
    private fun setUpUserAreaCircle() {
        val userAreaCircle = Circle(
            Point(
                requireContext().applicationInstance.currentUserPosition!!.latitude,
                requireContext().applicationInstance.currentUserPosition!!.longitude
            ), userAreaCircleRadius
        )
        mapView.map.mapObjects.addCircle(userAreaCircle).apply {
            strokeWidth = 1.5f
            strokeColor =
                ContextCompat.getColor(requireContext(), R.color.user_area_circle_stroke_color)
            fillColor =
                ContextCompat.getColor(requireContext(), R.color.user_area_circle_fill_color)
        }
    }

    private fun addCurrentPointToMap(it: MyPoint) {
        mapView.map.mapObjects.addPlacemark()
            .apply {
                userData = it
                geometry = Point(it.coordinates.latitude, it.coordinates.longitude)
                setIcon(pointIconsList[it.type])
                addTapListener(pointTapListener)
            }
    }

    private val pointTapListener = object : MapObjectTapListener {
        override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
            // обработка нажатия на точку

            if (mapObject !is PlacemarkMapObject) {
                return true
            }

            //получение инфы о точке, из обьекта класса, который всегда "лежит рядом с ней"
            val currentPointInformation = mapObject.userData as? MyPoint ?: return true

            //переводим координаты из координат на карте в координаты на экране
            val screenPoint = mapView.mapWindow.worldToScreen(point) ?: return true

            if (currentPointInformation.type < 5) {
                onVerifiedPointTap(currentPointInformation, screenPoint)
            } else if (currentPointInformation.type == 5) {
                onEventPointTap(currentPointInformation, screenPoint)
            } else {
                onUnverifiedPointTap(currentPointInformation, screenPoint)
            }

            return true
        }

        private fun onVerifiedPointTap(
            currentPointInformation: MyPoint,
            screenPoint: ScreenPoint
        ) {
            bindingVerifiedPointPopupWindow.verifiedPointName.text = getString(
                R.string.verified_point_name_format,
                getString(mapPointTypeToNameResource[currentPointInformation.type]!!),
                currentPointInformation.name
            )

            //Можно оптимизировать и не ставить слушатели нажатий каждый раз, а просто менять текущие координаты?
            bindingVerifiedPointPopupWindow.goToButton.setOnClickListener {
                goToYandexMaps(currentPointInformation.coordinates)
            }
            showPopupWindow(bindingVerifiedPointPopupWindow.root, screenPoint)
        }

        private fun onEventPointTap(
            currentPointInformation: MyPoint,
            screenPoint: ScreenPoint
        ) {
            bindingEventPointPopupWindow.eventPointName.text = getString(
                R.string.verified_point_name_format,
                getString(mapPointTypeToNameResource[currentPointInformation.type]!!),
                currentPointInformation.name
            )

            bindingEventPointPopupWindow.eventPointDescription.text =
                currentPointInformation.description

            //Можно оптимизировать и не ставить слушатели нажатий каждый раз, а просто менять текущие координаты?
            bindingEventPointPopupWindow.goToButton.setOnClickListener {
                goToYandexMaps(currentPointInformation.coordinates)
            }

            showPopupWindow(bindingEventPointPopupWindow.root, screenPoint)
        }

        private fun onUnverifiedPointTap(
            currentPointInformation: MyPoint,
            screenPoint: ScreenPoint
        ) {
            bindingUnverifiedPointPopupWindow.unverifiedPointName.text =
                getString(mapPointTypeToNameResource[currentPointInformation.type]!!)

            bindingUnverifiedPointPopupWindow.unverifiedPointDescription.text =
                currentPointInformation.name

            showPopupWindow(bindingUnverifiedPointPopupWindow.root, screenPoint)
        }

        private fun showPopupWindow(popupWindowRoot: LinearLayout, screenPoint: ScreenPoint) {
            val popupWindow = PopupWindow(
                popupWindowRoot,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.contentView.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            val windowWidth = popupWindow.contentView.measuredWidth
            val windowHeight = popupWindow.contentView.measuredHeight
            popupWindow.showAtLocation(
                mapView,
                Gravity.NO_GRAVITY,
                (screenPoint.x - windowWidth / 2).toInt(),
                (screenPoint.y + binding.backToChatsPanel.height + windowHeight / 2).toInt()
            )
        }

        private fun goToYandexMaps(currentPointCoordinates: Coordinates) {
            val url =
                "https://yandex.ru/maps/?rtext=" +
                        "${requireContext().applicationInstance.currentUserPosition?.latitude}," +// точка
                        "${requireContext().applicationInstance.currentUserPosition?.longitude}" +// начала пути
                        "~" +
                        "${currentPointCoordinates.latitude}," +//точка
                        "${currentPointCoordinates.longitude}" +//конца пути
                        "&rtt=auto"

            val intent = Intent(Intent.ACTION_VIEW) // Создаем новый Intent

            intent.data = Uri.parse(url) // Устанавливаем URL-адрес для Intent

            startActivity(intent) // Запускаем новое Activity с помощью Intent
        }
    }
    private fun setUpFragmentCurrentState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }

        isPointAdding = savedInstanceState.getBoolean("isPointAdding")
        isPointDescriptionCreating = savedInstanceState.getBoolean("isPointDescriptionCreating")
        val addingPointPosition = Point(
            savedInstanceState.getDouble("addingPointLatitude"),
            savedInstanceState.getDouble("addingPointLongitude"),
        )
        currentIconNumber = savedInstanceState.getInt("addingPointIconNumber")
        reliability = savedInstanceState.getInt("reliability")
        if (addingPointPosition.latitude != 0.0 && addingPointPosition.longitude != 0.0) {
            binding.confirmAdditionPointToMapFab.isEnabled = true
            /* Здесь ставлю только положение на карте, т.к. в любом случае после этого
             выполниться блок с observe и нет смысла подгружать для этого изображение,
             ведь данная точка будет очищена вместе со всеми и
             потом добавлена обратно, но уже с изображением */
            currentIconPlacemark = mapView.map.mapObjects.addPlacemark()
                .apply {
                    geometry = addingPointPosition
                }
        }

        if (isPointAdding || isPointDescriptionCreating) {
            binding.addPointToMapFab.visibility = View.INVISIBLE
            binding.updateMapPointsFab.visibility = View.INVISIBLE
            binding.showCurrentUserPositionFab.visibility = View.INVISIBLE
            binding.filtersButton.visibility = View.INVISIBLE
            binding.cancelAdditionPointToMapFab.visibility = View.VISIBLE
            binding.confirmAdditionPointToMapFab.visibility = View.VISIBLE
        }
        if (isPointDescriptionCreating) {
            val savedDescription = savedInstanceState.getString("savedDescriptionForAddingPoint")
            binding.root.post {
                if (this.isResumed && isPointDescriptionCreating) {
                    createAddingPointDescriptionPopupWindow(savedDescription ?: "")
                }
            }

            val addedPhotosList = savedInstanceState.getParcelableArray("addedPhotosList")
            if (addedPhotosList?.isArrayOf<PhotoElementModel>() == true) {
                photosListAdapter.submitList((addedPhotosList as Array<PhotoElementModel>).toList())
            }
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
                requireContext().applicationInstance.LOCATION_UPDATES_TIME_INTERVAL,
                requireContext().applicationInstance.LOCATION_UPDATES_MIN_DISTANCE,
                locationListener
            )
            setUpCurrentUserPositionIcon()
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
                setUpCurrentUserPositionIcon()
            } else {
                // Разрешение на использование местоположения не предоставлено
                Toast.makeText(
                    requireContext(),
                    "Без доступа к местоположению мы не сможем отправить вам точки",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun setUpCurrentUserPositionIcon() {
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        userLocationLayer?.isVisible = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(userLocationObjectListener)
    }

    private val userLocationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(p0: UserLocationView) {
            binding.showCurrentUserPositionFab.isEnabled = true
        }

        override fun onObjectRemoved(p0: UserLocationView) {
        }

        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}
    }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            if (getApplicationContext().applicationInstance.currentUserPosition == null) {
                getApplicationContext().applicationInstance.currentUserPosition = location

                viewModel.updatePoints(location.latitude, location.longitude)

                getApplicationContext().applicationInstance.currentCameraPosition = CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f
                )
                mapView.map.move(
                    getApplicationContext().applicationInstance.currentCameraPosition!!,
                    Animation(Animation.Type.SMOOTH, 2f),
                    null
                )

                binding.updateMapPointsFab.isEnabled = true
                binding.addPointToMapFab.isEnabled = true
                binding.filtersButton.isEnabled = true

                return
            }

            binding.updateMapPointsFab.isEnabled = true
            binding.addPointToMapFab.isEnabled = true
            binding.filtersButton.isEnabled = true

            val distance = getApplicationContext().applicationInstance.currentUserPosition!!.distanceTo(location)

            if (distance >= 5000) {
                getApplicationContext().applicationInstance.currentUserPosition = location

                viewModel.updatePoints(location.latitude, location.longitude)
            }
        }
    }

    private fun listenerForFiltersButton(filtersButton: View) {

        val windowWidth: Int = dpToPx(resources.getInteger(R.integer.filters_popup_window_width))
        var windowHeight: Int =
            dpToPx(resources.getInteger(R.integer.filters_popup_window_width_landscape_orientation))

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            windowHeight =
                dpToPx(resources.getInteger(R.integer.filters_popup_window_height_vertical_orientation))
        }

        val popupWindow = PopupWindow(
            bindingFilterPointsCheckboxPopupWindow.root,
            windowWidth,
            windowHeight,
            true
        )

        val pointCoordinationArray = IntArray(2)
        filtersButton.getLocationOnScreen(pointCoordinationArray)
        val x = pointCoordinationArray[0]
        val y = pointCoordinationArray[1]

        popupWindow.showAtLocation(
            mapView,
            Gravity.NO_GRAVITY,
            x + filtersButton.width - windowWidth,
            y - windowHeight - dpToPx(resources.getInteger(R.integer.filters_popup_window_bottom_offset))
        )
    }

    private fun listenerForShowCurrentUserPositionFab() {

        if (userLocationLayer?.cameraPosition() == null) {
            mapView.map.move(
                CameraPosition(
                    Point(
                        requireContext().applicationInstance.currentUserPosition!!.latitude,
                        requireContext().applicationInstance.currentUserPosition!!.longitude
                    ),
                    /* zoom = */requireContext().applicationInstance.currentCameraPosition!!.zoom,
                    /* azimuth = */ requireContext().applicationInstance.currentCameraPosition!!.azimuth,
                    /* tilt = */ requireContext().applicationInstance.currentCameraPosition!!.tilt
                ),
                Animation(Animation.Type.SMOOTH, 2f),
                null
            )

            return
        }

        mapView.map.move(
            CameraPosition(
                userLocationLayer!!.cameraPosition()!!.target,
                /* zoom = */ userLocationLayer!!.cameraPosition()!!.zoom,
                /* azimuth = */ userLocationLayer!!.cameraPosition()!!.azimuth,
                /* tilt = */ userLocationLayer!!.cameraPosition()!!.tilt
            ),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
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
        binding.updateMapPointsFab.visibility = View.INVISIBLE
        binding.addPointToMapFab.visibility = View.INVISIBLE
        binding.showCurrentUserPositionFab.visibility = View.INVISIBLE
        binding.filtersButton.visibility = View.INVISIBLE

        binding.cancelAdditionPointToMapFab.visibility = View.VISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.VISIBLE

        currentIconNumber = menuItem.order + 6

        if (userLocationLayer?.cameraPosition() != null) {
            setUpCurrentIconPlacemark(userLocationLayer?.cameraPosition()!!.target)
            binding.confirmAdditionPointToMapFab.isEnabled = true
        }

        isPointAdding = true
        return true
    }

    private fun listenerForCancelAdditionPointFab(): Boolean {
        binding.updateMapPointsFab.visibility = View.VISIBLE
        binding.addPointToMapFab.visibility = View.VISIBLE
        binding.showCurrentUserPositionFab.visibility = View.VISIBLE
        binding.filtersButton.visibility = View.VISIBLE

        binding.cancelAdditionPointToMapFab.visibility = View.INVISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.INVISIBLE
        isPointAdding = false
        currentIconNumber = -1
        reliability = 1

        if (currentIconPlacemark != null) {
            mapView.map.mapObjects.remove(currentIconPlacemark!!)
            currentIconPlacemark = null
        }
        binding.confirmAdditionPointToMapFab.isEnabled = false

        return true
    }

    private fun listenerForConfirmAdditionPointFab(): Boolean {
        isPointAdding = false
        isPointDescriptionCreating = true

        createAddingPointDescriptionPopupWindow("")
        return true
    }

    private fun createAddingPointDescriptionPopupWindow(savedDescription: String) {
        addingPointDescriptionPopupWindow = PopupWindow(
            bindingCreateDescriptionForAddingPointPopupWindow.root,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            true
        )

        addingPointDescriptionPopupWindow?.softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE

        bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.setText(
            savedDescription
        )

        addingPointDescriptionPopupWindow?.showAtLocation(
            requireView(),
            Gravity.NO_GRAVITY,
            0,
            0
        )
    }

    private fun listenerForCancelDescriptionAddingButton() {
        addingPointDescriptionPopupWindow?.dismiss()
        addingPointDescriptionPopupWindow = null

        isPointDescriptionCreating = false
        isPointAdding = true
        photosListAdapter.submitList(emptyList())
    }

    private fun launchPhotosPicker() {
        pickMultipleMedia.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            photosListAdapter.submitList(uris.map { PhotoElementModel(it) })
        }

    private fun listenerForConfirmDescriptionAddingButton() {
        addingPointDescriptionPopupWindow?.dismiss()

        binding.updateMapPointsFab.visibility = View.VISIBLE
        binding.addPointToMapFab.visibility = View.VISIBLE
        binding.showCurrentUserPositionFab.visibility = View.VISIBLE
        binding.filtersButton.visibility = View.VISIBLE

        binding.cancelAdditionPointToMapFab.visibility = View.INVISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.INVISIBLE

        isPointDescriptionCreating = false

        viewModel.addPoint(
            type = currentIconNumber,
            latitude = currentIconPlacemark!!.geometry.latitude,
            longitude = currentIconPlacemark!!.geometry.longitude,
            text = bindingCreateDescriptionForAddingPointPopupWindow
                .addingPointDescription.text.toString(),
            reliability = reliability,
            filePaths = photosListAdapter.currentList.mapNotNull { getRealPathFromURI(it.uri) }
        )

        reliability = 1
        currentIconNumber = -1
        currentIconPlacemark = null
        binding.confirmAdditionPointToMapFab.isEnabled = false
        addingPointDescriptionPopupWindow = null
        bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.text.clear()
        photosListAdapter.submitList(emptyList())
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            cursor.close()
        }
        return result
    }

    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPointAdding", isPointAdding)
        outState.putBoolean("isPointDescriptionCreating", isPointDescriptionCreating)

        outState.putDouble(
            "addingPointLatitude",
            currentIconPlacemark?.geometry?.latitude ?: 0.0
        )
        outState.putDouble(
            "addingPointLongitude",
            currentIconPlacemark?.geometry?.longitude ?: 0.0
        )

        outState.putInt("addingPointIconNumber", currentIconNumber)
        outState.putInt("reliability", reliability)
        outState.putString(
            "savedDescriptionForAddingPoint",
            if (this::bindingCreateDescriptionForAddingPointPopupWindow.isInitialized) {
                bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.text.toString()
            } else {
                ""
            }
        )
        outState.putParcelableArray("addedPhotosList", photosListAdapter.currentList.toTypedArray())
    }

    override fun onDestroyView() {
        //Не уверен, что теперь в этом вообще есть необходимость, но лучше перебдеть, чем недобдеть.
        addingPointDescriptionPopupWindow?.dismiss()
        addingPointDescriptionPopupWindow = null
        requireContext().applicationInstance.currentCameraPosition = mapView.map.cameraPosition
        super.onDestroyView()
    }
}