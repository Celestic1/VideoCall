package com.raymondliang.myapplication

import android.app.Application
import com.raymondliang.myapplication.utils.NetworkClient

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(this)
    }
}