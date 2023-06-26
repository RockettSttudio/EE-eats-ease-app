package com.rockettsttudio.eatsease

import android.app.Application
import com.rockettsttudio.eatsease.ui.ViewModelFactory

class App : Application() {
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}