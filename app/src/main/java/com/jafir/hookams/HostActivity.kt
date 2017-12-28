package com.jafir.hookams

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by jafir on 2017/12/27.
 *
 * 可埋在Manifest里面，然后以真实的身份帮助其他人"偷渡"，绕过AMS
 * 注意 它最好launchMode standard
 * 如果是 singleTask会出现 不会跳转的现象，因为在AMS检查的是发现它自身实例存在便不会再进行跳转
 * 类似于MainActivity点击 self的效果
 *
 */
class HostActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
    }

    override fun onPause() {
        super.onPause()
        Log.e("debug","HostActivity pause!!!!!")
    }
}