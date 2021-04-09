package com.example.baseframework

import android.os.Bundle
import android.widget.Toast
import com.example.baseframework.activity.BaseVBActivity
import com.example.baseframework.databinding.ActivityVoiceBinding

class MainActivity : BaseVBActivity<ActivityVoiceBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mViewContainer.btnBroken.onClick {
//            startActivity<BrokenActivity>()
//        }
    }
}