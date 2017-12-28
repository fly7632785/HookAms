package com.jafir.hookams

import android.annotation.SuppressLint
import android.app.Application
import com.jafir.hookams.hook.MyInstrumentation
import com.jafir.hookams.hook.PluginUtil

/**
 * Created by jafir on 2017/12/27.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        hook2(this)
    }

    @SuppressLint("PrivateApi")
    fun hook2(application: Application) {
        try {
            val mMainThread = PluginUtil.getField(application.baseContext, "mMainThread")
            PluginUtil.setField(mMainThread, "mInstrumentation", MyInstrumentation())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}