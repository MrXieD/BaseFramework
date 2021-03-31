package com.example.baseframework

import android.os.Bundle
import android.widget.Toast
import com.example.baseframework.activity.BaseVBActivity
import com.example.baseframework.databinding.ActivityMainBinding
import com.example.baseframework.ex.onClick
import com.example.baseframework.ui.anim.BrokenActivity
import org.jetbrains.anko.startActivity

class MainActivity : BaseVBActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewContainer.btnBroken.onClick {
            startActivity<BrokenActivity>()
        }
    }
}