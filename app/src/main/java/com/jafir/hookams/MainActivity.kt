package com.jafir.hookams

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jafir.hookams.hook.AMSHookUtil
import com.jafir.hookams.hook.MyInstrumentation
import com.jafir.hookams.hook.ReflectUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //测试 singletask 跳转自身无效果
        findViewById(R.id.self).setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//            startActivity(Intent(this, this.javaClass))
            startActivity(Intent(this, this::class.java))
        }
        // 方案一 较为复杂
        findViewById(R.id.button).setOnClickListener {
            AMSHookUtil.hook(this)
            startActivity(Intent(this, OtherActivity::class.java))
        }
        // 方案二 超简单
        // 两种方式不能混用
        findViewById(R.id.button1).setOnClickListener {
            hook2(this)
            startActivity2(this, Intent(this, OtherActivity::class.java))
        }
    }

    @SuppressLint("PrivateApi")
    fun hook2(context: Context) {
        try {
            val mMainThread = ReflectUtil.getField(context, "mMainThread")
            ReflectUtil.setField(mMainThread, "mInstrumentation", MyInstrumentation())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startActivity2(activity: Activity, intent: Intent) {
        val componentName = intent.component
        // com.jafir.hookams.HostActivity是预埋在Manifest中的桩（插桩绕过）
        intent.setClassName(componentName!!.packageName, "com.jafir.hookams.PreBuryActivity")
        // 把实际要加载的放入extra，以便倒是直接拿出来创建加载
        intent.putExtra("realActivity", componentName.className)
        activity.startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        Log.e("debug", "MainActivity pause!!!!!")
    }
}
