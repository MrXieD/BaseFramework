package com.example.baseframework

import android.os.Bundle
import android.widget.Toast
import com.example.baseframework.activity.BaseVBActivity
import com.example.baseframework.databinding.ActivityMainBinding
import com.example.baseframework.databinding.ActivityVoiceBinding
import com.example.baseframework.view.WheelView

class MainActivity : BaseVBActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mViewContainer.btnBroken.onClick {
//            startActivity<BrokenActivity>()
//        }
        val list = mutableListOf<String>()
        for (i in 0..20){
            list.add("$i")
        }
        mViewContainer.wv.setItems(list)

        mViewContainer.wv.setLoop(false)
    }
}