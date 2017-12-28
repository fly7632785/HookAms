package com.jafir.hookams.hook

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created by jafir on 2017/12/27.
 */
class HookInvocationHandler(val mAmsObject: Any, val mPackageName: String, val cls: String) : InvocationHandler {


    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>): Any? {
        //todo 对startActivity 进行hook
        if (method?.name == "startActivity") {
            var index = 0
            // 找到我们启动的intent
            for (i in 0..args!!.size) {
                if (args[i] is Intent) {
                    index = i
                    break
                }
            }

            //取出真正的intent
            val originalIntent: Intent = args[index] as Intent
            Log.d("AMSHookUtil", "hookInvocationHanlder:${originalIntent.component.className}")
            // 伪造一个自己已经注册过的intent就是 host
            val proxyIntent = Intent()
            // 使用这个已经注册过的来进行调用  使用一个假的Intent
            val componentName = ComponentName(mPackageName, cls)
            proxyIntent.component = componentName
            // 在这里把未注册的intent先存起来，后面会取出来用
            proxyIntent.putExtra("originallyIntent", originalIntent)
            args[index] = proxyIntent
        }
        return method?.invoke(mAmsObject, *args)
    }


}