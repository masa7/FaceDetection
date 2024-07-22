package com.example.facedetection.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance

class SingletonContext: Application() {
    //override fun onCreate(){
    //    super.onCreate()
    //}

    init {
        instance = this
    }

    companion object{
        private var instance: SingletonContext? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
