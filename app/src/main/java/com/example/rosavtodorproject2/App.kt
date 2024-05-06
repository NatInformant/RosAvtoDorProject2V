package com.example.rosavtodorproject2

import android.app.Application
import android.location.Location
import com.example.rosavtodorproject2.ioc.ApplicationComponent
import com.example.rosavtodorproject2.ioc.DaggerApplicationComponent
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import java.net.InetAddress


class App : Application() {
    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().build()
    }
    var currentUserPosition: Location? = null
    var currentCameraPosition: CameraPosition? = null
    // Сверху вниз, от АЗС, до происшествий, последнее - это 4 типа в одном
    val listFilterStatesForPointType: MutableList<Boolean> = mutableListOf(
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    )
    override fun onCreate() {
        super.onCreate()
        sInstance = this
        MapKitFactory.setApiKey(BuildConfig.MY_API_KEY)
    }

    fun isInternetAvailable(): Boolean {
        return try {
            val ipAddress = InetAddress.getByName("google.com")
            return !ipAddress.equals("")
        } catch (e: Exception) {
            false
        }
    }
    companion object {
        private var sInstance: App? = null
        fun getInstance(): App {
            return requireNotNull(sInstance) { "I really don't know how you get there." }
        }
    }
}