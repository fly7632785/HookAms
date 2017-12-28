package com.jafir.hookams

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by jafir on 2017/12/27.
 */
class OtherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        setTitle("other")
        findViewById(R.id.button).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("debug","pause!!!!!")
    }
}