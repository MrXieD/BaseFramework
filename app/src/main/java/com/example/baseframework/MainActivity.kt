package com.example.baseframework

import android.os.Bundle
import com.example.baseframework.activity.BaseVBActivity
import com.example.baseframework.databinding.ActivityMainBinding
import com.example.baseframework.databinding.ActivityVoiceBinding

class MainActivity : BaseVBActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mViewContainer.btnBroken.onClick {
//            startActivity<BrokenActivity>()
//        }
    }
}