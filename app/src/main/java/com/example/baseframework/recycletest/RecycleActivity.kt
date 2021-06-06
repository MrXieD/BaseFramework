package com.example.baseframework.recycletest

import android.os.Bundle
import android.widget.BaseAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseframework.R
import com.example.baseframework.activity.BaseDBActivity
import com.example.baseframework.databinding.ActivityRecycleBinding

/**
@author Anthony.H
@date: 2021/5/21
@desription:测试局部刷新[RecyclerView]
 */
class RecycleActivity : BaseDBActivity<ActivityRecycleBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun init() {
        dataBinding?.let { binding ->
            binding.recycleview.layoutManager = LinearLayoutManager(this)
            val list = MutableList<String>(16) { it.toString() }
            binding.adapter = PartialNotifyAdapter(list)
        }
    }

    override fun getLayoutId(): Int {
        val adapter: BaseAdapter
        return R.layout.activity_recycle
    }

}