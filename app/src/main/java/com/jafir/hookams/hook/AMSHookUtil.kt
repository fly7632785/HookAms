package com.jafir.hookams.hook

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@SuppressLint("StaticFieldLeak")
/**
 * Created by jafir on 2017/12/27.
 * 绕过AMS检查 开启Manifest中没有注册的Activity
 * 方案1  反射了多个对象，ams activityThread mH  mCallback等
 * 方案2 最简单的 反射Instrumentation的newActivity
 *
 */
object AMSHookUtil {
    private var sContext: Context? = null
    private lateinit var sPackageName: String
    private lateinit var sHostClassName: String
    private val TAG = "AMSHookUtil"

    @SuppressLint("PrivateApi")
    fun hook(context: Context) {
        // 判断只hook一次
        if (sContext != null) {
            return
        }
        try {
            val application: Context = context.applicationContext
            sContext = application
            val manager = application.packageManager
            sPackageName = application.packageName
            val packgeInfo = manager.getPackageInfo(sPackageName, PackageManager.GET_ACTIVITIES)
            val activities = packgeInfo.activities
            if (activities.isEmpty()) {
                return
            }
            //获取host activity 这里 其实0 代表MainActivity（第一个)，其实我们并不需要hostActivity
            //只要是存在于Manifest中的Activity都可以
            //但是这样的话，如果MainActivity是SingleTask的就不行，因为实例存在不会进行跳转，所以推荐还是最好使用hostActivity
            //并且 hostActivity launchMode为standard
            val activityInfo = activities[1]
            sHostClassName = activityInfo.name
            Log.d(TAG, "packageName:$sPackageName\tHostClassName:$sHostClassName")

            //获取AMN类
            val amnClass = Class.forName("android.app.ActivityManagerNative")
            //获取其gDefault field 也就是一个IActivityManager的子类   其实就是AMS
            val defaultField = amnClass.getDeclaredField("gDefault")
            defaultField.isAccessible = true
            //获取到ams的单例的对象
            val gDefaultObj = defaultField.get(null) // 所有静态对象的反射 可以通过传null获取。如果是实例必须传实例

            //获取ams的单例类
            val singletonClass = Class.forName("android.util.Singleton")
            //获取单例类的instance实例
            val amsField = singletonClass.getDeclaredField("mInstance")
            amsField.isAccessible = true
            //反射获取ams对象
            var amsObj = amsField.get(gDefaultObj)

            // todo hook  绕过AMS检查
            // 利用代理对象，在代理的过程中加入hook
            amsObj = Proxy.newProxyInstance(context.javaClass.classLoader,
                    amsObj.javaClass.interfaces,
                    HookInvocationHandler(amsObj, sPackageName, sHostClassName))
            // todo hook
            //注入了代理hook之后再把对象设回去
            amsField.set(gDefaultObj, amsObj)
            hookLaunchActivity()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * hook 开启Activity的activityThread
     * 需要把原来的originalActivity真正调用开启
     *
     */
    @Throws(Exception::class)
    private fun hookLaunchActivity() {
        // todo 获取hanlder 然后反射 callback对象
        val activityThread = Class.forName("android.app.ActivityThread")
        // 获取ActivityThread对象
        val sCurrentActivityThreadField = activityThread.getDeclaredField("sCurrentActivityThread")
        sCurrentActivityThreadField.isAccessible = true
        val sCurrentActivityThreadObj = sCurrentActivityThreadField.get(null) // 静态实例直接null获取

        //获取mH对象，hook消息传递
        val mHField = activityThread.getDeclaredField("mH")
        mHField.isAccessible = true
        //获取mH  hanlder
        val mH = mHField.get(sCurrentActivityThreadObj)
        //获取 mCallBack
        val callBackField = Handler::class.java.getDeclaredField("mCallback")
        callBackField.isAccessible = true
        //设置mH的callBack为我们自己想要的callBack
        callBackField.set(mH, ActivityThreadHanlderCallback())
    }

    class ActivityThreadHanlderCallback : android.os.Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            var LAUNCH_ACTIVITY = 0
            try {
                val clazz = Class.forName("android.app.ActivityThread\$H")
                val field = clazz.getField("LAUNCH_ACTIVITY")
                LAUNCH_ACTIVITY = field.getInt(null)
            } catch (e: Exception) {
            }

            if (msg?.what == LAUNCH_ACTIVITY) {
                handleLaunchActivity(msg)
            }
            return false
        }

    }

    private fun handleLaunchActivity(msg: Message) {
        try {
            // todo 把之前的intent换回你想要启动的intent
            val obj = msg.obj
            val intentField = obj.javaClass.getDeclaredField("intent")
            intentField.isAccessible = true
            var proxyIntent = intentField.get(obj) as Intent
            //拿到之前真实要被启动的Intent 然后把Intent换掉
            // 来源直接是extra里面
            val originallyIntent = proxyIntent.getParcelableExtra<Intent>("originallyIntent") ?: return

//            proxyIntent = originallyIntent
            proxyIntent.component = originallyIntent.component


            Log.e(TAG, "handleLaunchActivity:" + originallyIntent.component!!.className)

            //todo:兼容AppCompatActivity
            val forName = Class.forName("android.app.ActivityThread")
            val field = forName.getDeclaredField("sCurrentActivityThread")
            field.isAccessible = true
            val activityThread = field.get(null)
            val getPackageManager = activityThread.javaClass.getDeclaredMethod("getPackageManager")
            val iPackageManager = getPackageManager.invoke(activityThread)
            val handler = PackageManagerHandler(iPackageManager)
            val iPackageManagerIntercept = Class.forName("android.content.pm.IPackageManager")
            val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
                    arrayOf(iPackageManagerIntercept), handler)
            // 获取 sPackageManager 属性
            val iPackageManagerField = activityThread.javaClass.getDeclaredField("sPackageManager")
            iPackageManagerField.isAccessible = true
            iPackageManagerField.set(activityThread, proxy)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    class PackageManagerHandler(val mActivityManagerObject: Any?) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method?, args: Array<Any>): Any? {
            if (method?.getName() == "getActivityInfo") {
                val componentName = ComponentName(sPackageName, sHostClassName)
                args[0] = componentName
            }
            return method?.invoke(mActivityManagerObject, *args)
        }

    }

}