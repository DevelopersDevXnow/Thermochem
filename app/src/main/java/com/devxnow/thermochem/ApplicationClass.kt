package com.devxnow.thermochem

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class ApplicationClass : Application() {


    companion object {

        private const val TAG = "MY_APPLICATION_TAG"
    }

    override fun onCreate() {
        super.onCreate()
//Enabling Offline Capabilities on Android
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }

}