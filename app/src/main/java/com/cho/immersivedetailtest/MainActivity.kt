package com.cho.immersivedetailtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cho.immersivedetail.util.LollipopCompatSingleton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        LollipopCompatSingleton.translucentStatusBar(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.content_frame, MainFragment.INSTANCE,
                MainFragment::class.java.simpleName).commit()
    }
}
