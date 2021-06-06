package com.example.baseframework

import android.app.AlertDialog
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.baseframework.activity.BaseVBActivity
import com.example.baseframework.databinding.ActivityMainBinding
import com.example.baseframework.ex.onClick
import com.example.baseframework.lottery.ImportData
import com.example.baseframework.ui.anim.BrokenActivity
import com.example.imlotterytool.db.table.LotteryItem
import org.jetbrains.anko.startActivity

class MainActivity : BaseVBActivity<ActivityMainBinding>() {

    companion object {

        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewContainer.btnBroken.onClick {
            startActivity<BrokenActivity>()
        }
        val list = mutableListOf<String>()
        for (i in 0..20) {
            list.add("$i")
        }
        mViewContainer.wv.setItems(list)

        mViewContainer.wv.setLoop(false)
        //
        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .create()

        ImportData.run(applicationContext, object : ImportData.ImportDataListener {
            override fun onProgress(index: Long) {
                Log.e(TAG, "onProgress: $index")
                alertDialog.setMessage("正在加载第${index}条")
                if (!alertDialog.isShowing) {
                    alertDialog.show()
                }
            }

            override fun onSuccced(list: ArrayList<LotteryItem>) {
                alertDialog.dismiss()
                Log.e(TAG, "onSuccced: ")
                //大乐透
                val numberTitleList = ArrayList<String>()
                for (ball in 1..47) {
                    if (ball <= 35) {
                        numberTitleList.add(ball.toString())
                    } else {
                        numberTitleList.add((ball - 35).toString())
                    }
                }
                mViewContainer.lotteryView.refreshData(list, numberTitleList)
            }

            override fun onError(e: Exception) {
                Log.e(TAG, "onError: ${e.message}")
                alertDialog.setMessage(e.message)
                if (!alertDialog.isShowing) {
                    alertDialog.show()
                }
            }

        })


    }


}