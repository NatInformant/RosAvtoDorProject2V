package com.example.rosavtodorproject2.ioc

import android.content.Context
import com.example.rosavtodorproject2.App


val Context.applicationInstance: App
    get() = when (this) {
        is App -> this
        else -> this.applicationContext.applicationInstance
    }