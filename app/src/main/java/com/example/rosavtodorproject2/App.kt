package com.example.rosavtodorproject2

import android.R
import android.app.Application
import android.location.Location
import com.example.rosavtodorproject2.ioc.ApplicationComponent
import com.example.rosavtodorproject2.ioc.DaggerApplicationComponent
import com.yandex.mapkit.MapKitFactory
import java.io.IOException
import java.util.Properties


class App : Application() {
    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().build()
    }
    var previousLocation: Location? = null
    override fun onCreate() {
        super.onCreate()
        sInstance = this
        MapKitFactory.setApiKey(BuildConfig.MY_API_KEY)
    }

    companion object {
        private var sInstance: App? = null
        fun getInstance(): App {
            return requireNotNull(sInstance) { "I really don't how you get there." }
        }
    }
}