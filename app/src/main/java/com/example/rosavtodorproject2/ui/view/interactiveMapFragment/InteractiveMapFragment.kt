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
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rosavtodorproject2.App
import com.example.rosavtodorproject2.R
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.databinding.CreateDescriptionForAddingPointPopupWindowBinding
import com.example.rosavtodorproject2.databinding.FilterPointsCheckboxPopupWindowBinding
import com.example.rosavtodorproject2.databinding.FragmentInteractiveMapBinding
import com.example.rosavtodorproject2.databinding.UnverifiedPointPopupWindowBinding
import com.example.rosavtodorproject2.databinding.VerifiedPointPopupWindowBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
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


class InteractiveMapFragment : Fragment() {
    private lateinit var binding: FragmentInteractiveMapBinding
    private lateinit var bindingFilterPointsCheckboxPopupWindow: FilterPointsCheckboxPopupWindowBinding
    private lateinit var bindingVerifiedPointPopupWindow: VerifiedPointPopupWindowBinding
    private lateinit var bindingUnverifiedPointPopupWindow: UnverifiedPointPopupWindowBinding
    private lateinit var bindingCreateDescriptionForAddingPointPopupWindow: CreateDescriptionForAddingPointPopupWindowBinding
    private val applicationComponent
        get() = App.getInstance().applicationComponent

    private lateinit var mapView: MapView //Так, вот это кнч обьект View и он может утечь по памяти,
    // если его не null-ить в OnDestroyView, но в Доке Яндекса, не сказано что так делать нужно,
    // так что ХЗ

    private val viewModel: InteractiveMapFragmentViewModel by viewModels { applicationComponent.getInteractiveMapViewModelFactory() }

    private var isPointAdding = false
    private var isPointDescriptionCreating = false
    private var currentIconNumber = -1
    private var addingPointDescriptionPopupWindow: PopupWindow? = null

    private var currentPointsList = listOf<MyPoint>()

    private var pointIconsList = listOf<ImageProvider>()

    private val mapPointTypeToNameResource: kotlin.collections.Map<Int, Int> = mapOf(
        Pair(0, R.string.petrol_station),
        Pair(1, R.string.cafe),
        Pair(2, R.string.car_service),
        Pair(3, R.string.guesthouse),
        Pair(4, R.string.car_recharge_station),
        Pair(5, R.string.road_accident_menu_item_title),
        Pair(6, R.string.pothole_menu_item_title),
        Pair(7, R.string.obstruction_menu_item_title),
        Pair(8, R.string.bandit_menu_item_title),
    )

    private var changedFilterStatePointType: Int = -1
    private var currentIconPlacemark: PlacemarkMapObject? = null

    private val BASE_LATITUDE: Double = 55.154
    private val BASE_LONGITUDE: Double = 61.4291
    private val userAreaCircleRadius = 100000f
    private var locationManager: LocationManager? = null
    private var userLocationLayer: UserLocationLayer? = null
    private val LOCATION_UPDATES_TIME_INTERVAL: Long = 1000 // 1 секунда
    private val LOCATION_UPDATES_MIN_DISTANCE: Float = 10f // 10 метров
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractiveMapBinding.inflate(layoutInflater, container, false)

        //Хз правильно ли, но так хотя бы всегда обьект для popupWindow подгруженный будет и
        //останется только сам обьект view создать и всё
        setUpBindingsForPopupWindows()

        MapKitFactory.initialize(getApplicationContext())
        mapView = binding.mapview

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        setUpCameraPosition()

        mapView.map.addInputListener(addingNewPointListener)

        viewModel.points.observe(viewLifecycleOwner)
        {
            currentPointsList = it
            addPointsToInteractiveMap(it)
        }

        if (App.getInstance().currentUserPosition != null) {
            binding.addPointToMapFab.isEnabled = true
            binding.filtersButton.isEnabled = true
        }

        pointIconsList = listOf(
            ImageProvider.fromResource(requireContext(), R.drawable.petrol_station_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.cafe_icon_24dp),
            ImageProvider.fromResource(requireContext(), R.drawable.petrol_station_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.petrol_station_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.car_recharge_station_icon),
            ImageProvider.fromResource(requireContext(), R.drawable.image_car_accident_24dp),
            ImageProvider.fromResource(requireContext(), R.drawable.image_car_accident_24dp),
            ImageProvider.fromResource(requireContext(), R.drawable.image_car_accident_24dp),
            ImageProvider.fromResource(requireContext(), R.drawable.image_car_accident_24dp),
        )

        return binding.root
    }

    private fun setUpBindingsForPopupWindows() {
        bindingFilterPointsCheckboxPopupWindow =
            FilterPointsCheckboxPopupWindowBinding.inflate(layoutInflater)

        bindingFilterPointsCheckboxPopupWindow.petrolStationCheckBox.also {
            it.isChecked = App.getInstance().listFilterStatesForPointType[0]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )

        bindingFilterPointsCheckboxPopupWindow.cafeCheckBox.also {
            it.isChecked = App.getInstance().listFilterStatesForPointType[1]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.carServiceCheckBox.also {
            it.isChecked = App.getInstance().listFilterStatesForPointType[2]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.guesthouseCheckBox.also {
            it.isChecked = App.getInstance().listFilterStatesForPointType[3]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.carRechargeStationCheckBox.also {
            it.isChecked = App.getInstance().listFilterStatesForPointType[4]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )
        bindingFilterPointsCheckboxPopupWindow.incidentsCheckBox.also {
            it.isChecked = App.getInstance().listFilterStatesForPointType[5]
        }.setOnCheckedChangeListener(
            checkedChangeListener
        )

        bindingVerifiedPointPopupWindow = VerifiedPointPopupWindowBinding.inflate(layoutInflater)
        bindingUnverifiedPointPopupWindow =
            UnverifiedPointPopupWindowBinding.inflate(layoutInflater)

        bindingCreateDescriptionForAddingPointPopupWindow =
            CreateDescriptionForAddingPointPopupWindowBinding.inflate(layoutInflater)
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
                for (x in 5..8) {
                    changedFilterStatePointType = x
                    App.getInstance().listFilterStatesForPointType[changedFilterStatePointType] =
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
            }

            App.getInstance().listFilterStatesForPointType[changedFilterStatePointType] = isChecked

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
        if (App.getInstance().currentCameraPosition == null) {
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
                App.getInstance().currentCameraPosition!!,
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
    private val addingNewPointListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {

            if (!isPointAdding) {
                return
            }

            binding.confirmAdditionPointToMapFab.isEnabled = true

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
    private val userLocationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(p0: UserLocationView) {
            binding.showCurrentUserPositionFab.isEnabled = true
        }

        override fun onObjectRemoved(p0: UserLocationView) {}
        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}
    }

    private fun setUpCurrentIconPlacemark(point: Point) {
        currentIconPlacemark = mapView.map.mapObjects.addPlacemark()
            .apply {
                geometry = Point(point.latitude, point.longitude)
                setIcon(pointIconsList[currentIconNumber])
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
            val popupWindow = PopupWindow(
                bindingVerifiedPointPopupWindow.root,
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

        private fun onUnverifiedPointTap(
            currentPointInformation: MyPoint,
            screenPoint: ScreenPoint
        ) {
            bindingUnverifiedPointPopupWindow.unverifiedPointName.text =
                getString(mapPointTypeToNameResource[currentPointInformation.type]!!)

            bindingUnverifiedPointPopupWindow.unverifiedPointDescription.text =
                currentPointInformation.name

            val popupWindow = PopupWindow(
                bindingUnverifiedPointPopupWindow.root,
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
            if (App.getInstance().currentUserPosition == null) {
                Toast
                    .makeText(
                        requireContext(),
                        "не зная вашего местоположения мы не можем работать",
                        Toast.LENGTH_SHORT
                    ).show()
                return
            }
            val url =
                "https://yandex.ru/maps/?rtext=" +
                        "${App.getInstance().currentUserPosition?.latitude}," +// точка
                        "${App.getInstance().currentUserPosition?.longitude}" +// начала пути
                        "~" +
                        "${currentPointCoordinates.latitude}," +//точка
                        "${currentPointCoordinates.longitude}" +//конца пути
                        "&rtt=auto"

            val intent = Intent(Intent.ACTION_VIEW) // Создаем новый Intent

            intent.data = Uri.parse(url) // Устанавливаем URL-адрес для Intent

            startActivity(intent) // Запускаем новое Activity с помощью Intent
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

        if (App.getInstance().currentUserPosition != null) {
            setUpUserAreaCircle()
        }

        myPoints.forEach {
            if (App.getInstance().listFilterStatesForPointType[it.type]) {
                addCurrentPointToMap(it)
            }
        }
    }

    private fun setUpUserAreaCircle() {
        val userAreaCircle = Circle(
            Point(
                App.getInstance().currentUserPosition!!.latitude,
                App.getInstance().currentUserPosition!!.longitude
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLocationPermission()

        binding.backToChatsPanelButton.setOnClickListener {
            findNavController().navigate(R.id.action_interactiveMapFragment_to_chatsFragment)
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
        binding.confirmAdditionPointToMapFab.setOnClickListener {
            listenerForConfirmAdditionPointFab()
        }
        binding.cancelAdditionPointToMapFab.setOnClickListener {
            listenerForCancelAdditionPointFab()
        }

        setUpFragmentCurrentState(savedInstanceState)
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
                LOCATION_UPDATES_TIME_INTERVAL,
                LOCATION_UPDATES_MIN_DISTANCE,
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
                    LOCATION_UPDATES_TIME_INTERVAL,
                    LOCATION_UPDATES_MIN_DISTANCE,
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

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (App.getInstance().currentUserPosition == null) {
                App.getInstance().currentUserPosition = location

                updatePoints(location)

                App.getInstance().currentCameraPosition = CameraPosition(
                    Point(location.latitude, location.longitude),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f
                )
                mapView.map.move(
                    App.getInstance().currentCameraPosition!!,
                    Animation(Animation.Type.SMOOTH, 2f),
                    null
                )

                binding.addPointToMapFab.isEnabled = true
                binding.filtersButton.isEnabled = true

                return
            }

            binding.addPointToMapFab.isEnabled = true
            binding.filtersButton.isEnabled = true

            val distance = App.getInstance().currentUserPosition!!.distanceTo(location)

            if (distance >= 5000) {
                updatePoints(location)
                App.getInstance().currentUserPosition = location
            }
        }
    }

    private fun updatePoints(location: Location) {
        viewModel.updatePoints(
            Toast.makeText(
                requireContext(),
                "Без доступа к интернету приложение не сможет работать",
                Toast.LENGTH_LONG
            ),
            location.latitude, location.longitude
        )
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
        binding.addPointToMapFab.visibility = View.INVISIBLE
        binding.showCurrentUserPositionFab.visibility = View.INVISIBLE
        binding.filtersButton.visibility = View.INVISIBLE

        binding.cancelAdditionPointToMapFab.visibility = View.VISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.VISIBLE

        currentIconNumber = menuItem.order + 5
        isPointAdding = true
        return true
    }

    private fun listenerForCancelAdditionPointFab(): Boolean {
        binding.addPointToMapFab.visibility = View.VISIBLE
        binding.showCurrentUserPositionFab.visibility = View.VISIBLE
        binding.filtersButton.visibility = View.VISIBLE

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
        bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.requestFocus()

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
    }

    private fun listenerForConfirmDescriptionAddingButton() {
        addingPointDescriptionPopupWindow?.dismiss()

        binding.addPointToMapFab.visibility = View.VISIBLE
        binding.showCurrentUserPositionFab.visibility = View.VISIBLE
        binding.filtersButton.visibility = View.VISIBLE

        binding.cancelAdditionPointToMapFab.visibility = View.INVISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.INVISIBLE

        isPointDescriptionCreating = false

        addPoint()

        currentIconNumber = -1
        currentIconPlacemark = null
        binding.confirmAdditionPointToMapFab.isEnabled = false
        addingPointDescriptionPopupWindow = null
        bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.text.clear()
    }

    private fun addPoint() {
        viewModel.addPoint(
            toast = Toast.makeText(
                requireContext(),
                "Без доступа к интернету приложение не сможет работать",
                Toast.LENGTH_LONG
            ),
            type = currentIconNumber,
            latitude = currentIconPlacemark!!.geometry.latitude,
            longitude = currentIconPlacemark!!.geometry.longitude,
            text = bindingCreateDescriptionForAddingPointPopupWindow
                .addingPointDescription.text.toString()
        )
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
        outState.putString(
            "savedDescriptionForAddingPoint",
            if (this::bindingCreateDescriptionForAddingPointPopupWindow.isInitialized) {
                bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.text.toString()
            } else {
                ""
            }
        )
    }

    override fun onDestroyView() {
        //Не уверен, что теперь в этом вообще есть необходимость, но лучше перебдеть, чем недобдеть.
        addingPointDescriptionPopupWindow?.dismiss()
        addingPointDescriptionPopupWindow = null
        App.getInstance().currentCameraPosition = mapView.map.cameraPosition
        super.onDestroyView()
    }
}