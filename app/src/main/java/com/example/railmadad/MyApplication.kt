package com.example.railmadad

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any global resources or configurations here
        // For example, you can set up logging, crash reporting, etc.
        val config = HashMap<String, String>()
        config["cloud_name"] = "dsk28t0yf"
        config["api_key"] = "878986519461793"
        config["api_secret"] = "rMlm_scE-UDvn3Y0GwkBDbsLFZg"
        MediaManager.init(this,config)
    }
}