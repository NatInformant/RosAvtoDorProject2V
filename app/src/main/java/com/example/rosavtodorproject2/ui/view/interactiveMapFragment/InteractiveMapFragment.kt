package com.example.rosavtodorproject2.ui.view.interactiveMapFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Point
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

    //Пять типов верифицированных, т.к. заготовка под достопримечательности
    private val iconsResources = listOf(
        R.drawable.petrol_station_icon,
        R.drawable.cafe_icon_24dp,
        R.drawable.petrol_station_icon,
        R.drawable.petrol_station_icon,
        R.drawable.petrol_station_icon,
        R.drawable.image_car_accident_24dp,
        R.drawable.image_car_accident_24dp,
        R.drawable.image_car_accident_24dp,
        R.drawable.image_car_accident_24dp,
    )
    private val mapUnverifiedPointTypeToNameResource: kotlin.collections.Map<Int, Int> = mapOf(
        Pair(5, R.string.road_accident_menu_item_title),
        Pair(6, R.string.pothole_menu_item_title),
        Pair(7, R.string.obstruction_menu_item_title),
        Pair(8, R.string.bandit_menu_item_title),
    )

    private var changedFilterStatePointType: Int = -1
    private var currentIconPlacemark: PlacemarkMapObject? = null

    private val BASE_LATITUDE: Double = 55.154
    private val BASE_LONGITUDE: Double = 61.4291

    private var locationManager: LocationManager? = null
    private var savedDescription: String? = null
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

        setUpFragmentCurrentState(savedInstanceState)

        viewModel.points.observe(viewLifecycleOwner)
        {
            currentPointsList = it
            addPointsToInteractiveMap(it)
        }

        return binding.root
    }

    private fun setUpFragmentCurrentState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            isPointAdding = savedInstanceState.getBoolean("isPointAdding")
            isPointDescriptionCreating = savedInstanceState.getBoolean("isPointDescriptionCreating")
            val addingPointGeometry = Point(
                savedInstanceState.getDouble("addingPointLatitude"),
                savedInstanceState.getDouble("addingPointLongitude"),
            )

            currentIconNumber = savedInstanceState.getInt("addingPointIconNumber")

            if (addingPointGeometry.latitude != 0.0 && addingPointGeometry.longitude != 0.0) {
                binding.confirmAdditionPointToMapFab.isEnabled = true
                // Здесь ставлю только положение на карте, т.к. в любом случае после этого
                // выполниться блок с observe и нет смысла подгружать для этого изображение,
                // ведь данная точка будет очищена вместе со всеми и
                // потом добавлена обратно но уже с изображением
                currentIconPlacemark = mapView.map.mapObjects.addPlacemark()
                    .apply {
                        geometry = addingPointGeometry
                    }
            }

            if (isPointAdding) {
                binding.addPointToMapFab.visibility = View.INVISIBLE
                binding.cancelAdditionPointToMapFab.visibility = View.VISIBLE
                binding.confirmAdditionPointToMapFab.visibility = View.VISIBLE
            }
        }
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
        if (App.getInstance().previousLocation == null) {
            mapView.map.move(
                CameraPosition(
                    Point(BASE_LATITUDE, BASE_LONGITUDE),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f
                )
            )
        } else {
            mapView.map.move(
                CameraPosition(
                    Point(
                        App.getInstance().previousLocation!!.latitude,
                        App.getInstance().previousLocation!!.longitude
                    ),
                    /* zoom = */ 8f,
                    /* azimuth = */ 0f,
                    /* tilt = */ 0f
                )
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

    private fun setUpCurrentIconPlacemark(point: Point) {
        currentIconPlacemark = mapView.map.mapObjects.addPlacemark()
            .apply {
                geometry = Point(point.latitude, point.longitude)
                setIcon(
                    ImageProvider.fromResource(
                        requireContext(),
                        iconsResources[currentIconNumber]
                    )
                )
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
            bindingVerifiedPointPopupWindow.verifiedPointName.text =
                currentPointInformation.description

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
                getString(mapUnverifiedPointTypeToNameResource[currentPointInformation.type]!!)

            bindingUnverifiedPointPopupWindow.unverifiedPointDescription.text =
                currentPointInformation.description

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
            if (App.getInstance().previousLocation == null) {
                Toast
                    .makeText(
                        requireContext(),
                        "не зная вашего местоположения мы не можем сделать это",
                        Toast.LENGTH_SHORT
                    ).show()
                return
            }
            val url =
                "https://yandex.ru/maps/?rtext=" +
                        "${App.getInstance().previousLocation?.latitude}," +// точка
                        "${App.getInstance().previousLocation?.longitude}" +// начала пути
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

        myPoints.forEach {
            if (App.getInstance().listFilterStatesForPointType[it.type]) {
                addCurrentPointToMap(it)
            }
        }
    }

    private fun addCurrentPointToMap(it: MyPoint) {
        //СЛОЖИТЬ В МАПУ IMAGE PROVIDER-ы////////////////////////////////////////////////
        val imageProvider =
            ImageProvider.fromResource(requireContext(), iconsResources[it.type])
        mapView.map.mapObjects.addPlacemark()
            .apply {
                userData = it
                geometry = Point(it.coordinates.latitude, it.coordinates.longitude)
                setIcon(imageProvider)
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
        binding.addPointToMapFab.setOnClickListener {
            listenerForAddPointToMapFab(binding.anchorViewForPopupMenu)
        }
        binding.confirmAdditionPointToMapFab.setOnClickListener {
            listenerForConfirmAdditionPointFab()
        }
        binding.cancelAdditionPointToMapFab.setOnClickListener {
            listenerForCancelAdditionPointFab()
        }

        if (isPointDescriptionCreating) {
            savedDescription = savedInstanceState?.getString("savedDescriptionForAddingPoint")
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
            if (App.getInstance().previousLocation == null) {
                App.getInstance().previousLocation = location
                mapView.map.move(
                    CameraPosition(
                        Point(location.latitude, location.longitude),
                        /* zoom = */ 8f,
                        /* azimuth = */ 0f,
                        /* tilt = */ 0f
                    )
                )
                viewModel.updatePoints(location.latitude, location.longitude)
                return
            }

            val distance = App.getInstance().previousLocation!!.distanceTo(location)

            if (distance >= 2000) {
                // Изменить под самый конец работы с картой!!!
                /*mapView.map.move(
                    CameraPosition(
                        Point(location.latitude, location.longitude),
                        *//* zoom = *//* 8f,
                        *//* azimuth = *//* 0f,
                        *//* tilt = *//* 0f
                    )
                )*/

                viewModel.updatePoints(location.latitude, location.longitude)
                App.getInstance().previousLocation = location
            }
        }
    }

    private fun listenerForFiltersButton(filtersButton: View) {
        val popupWindow = PopupWindow(
            bindingFilterPointsCheckboxPopupWindow.root,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.contentView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        val a = IntArray(2)
        filtersButton.getLocationOnScreen(a)
        val x = a[0]
        val y = a[1]
        val windowWidth = popupWindow.contentView.measuredWidth
        val windowHeight = popupWindow.contentView.measuredHeight
        popupWindow.showAtLocation(
            mapView,
            Gravity.NO_GRAVITY,
            x + filtersButton.width - windowWidth,
            y - windowHeight - dpToPx(5)
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
        binding.cancelAdditionPointToMapFab.visibility = View.VISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.VISIBLE

        currentIconNumber = menuItem.order + 5
        isPointAdding = true
        return true
    }

    private fun listenerForCancelAdditionPointFab(): Boolean {
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

        /*addingPointDescriptionPopupWindow?.softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE*/
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
        binding.cancelAdditionPointToMapFab.visibility = View.INVISIBLE
        binding.confirmAdditionPointToMapFab.visibility = View.INVISIBLE

        isPointDescriptionCreating = false

        viewModel.addPoint(
            type = currentIconNumber,
            latitude = currentIconPlacemark!!.geometry.latitude,
            longitude = currentIconPlacemark!!.geometry.longitude,
            text = bindingCreateDescriptionForAddingPointPopupWindow
                .addingPointDescription.text.toString()
        )

        currentIconNumber = -1
        currentIconPlacemark = null
        binding.confirmAdditionPointToMapFab.isEnabled = false
        addingPointDescriptionPopupWindow = null
        bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.text.clear()
    }

    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.root.post {
            if (this.isResumed && isPointDescriptionCreating) {
                createAddingPointDescriptionPopupWindow(savedDescription?:"")
            }
        }
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
            bindingCreateDescriptionForAddingPointPopupWindow.addingPointDescription.text.toString()
        )
    }

    override fun onDestroyView() {
        //Не уверен, что теперь в этом вообще есть необходимость, но лучше перебдеть, чем недобдеть.
        addingPointDescriptionPopupWindow?.dismiss()
        addingPointDescriptionPopupWindow = null
        super.onDestroyView()
    }
}