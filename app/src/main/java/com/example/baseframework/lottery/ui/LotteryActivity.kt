package com.example.baseframework.lottery.ui

import android.util.Log
import android.view.Menu
import com.example.baseframework.R
import com.example.baseframework.activity.BaseDBActivity
import com.example.baseframework.databinding.ActivityLotteryBinding
import com.example.baseframework.lottery.ui.fragment.DataSiwchListener
import com.example.baseframework.lottery.ui.fragment.LotteryFragmentCallBack
import com.example.imlotterytool.util.LOTTERY_TYPE_CJDLT
import com.example.imlotterytool.util.LOTTERY_TYPE_FCSD
import com.example.imlotterytool.util.LOTTERY_TYPE_SSQ
import com.google.android.material.navigation.NavigationView

class LotteryActivity : BaseDBActivity<ActivityLotteryBinding>(), LotteryFragmentCallBack {

    companion object {
        private const val TAG = "LotteryActivity"
    }

    private lateinit var dataSiwchListener: DataSiwchListener

    //fragment显示相应的数据类型
    val onNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            Log.d(TAG, ":onNavigationItemSelected--->")
            item.isChecked = true
            when (item.itemId) {
                R.id.menu_item_fcsd -> {
                    dataSiwchListener.swictchTo(LOTTERY_TYPE_FCSD)
                }

                R.id.menu_item_ssq -> {
                    dataSiwchListener.swictchTo(LOTTERY_TYPE_SSQ)
                }
                R.id.menu_item_cjdlt -> {
                    dataSiwchListener.swictchTo(LOTTERY_TYPE_CJDLT)
                }
            }
            dataBinding?.drawerLayout?.close()
            true
        }

    override fun init() {
        dataBinding?.let {
            it.designNavigationView?.itemIconTintList = null
            it.lotteryActivity = this
            it.root?.post {
                it.designNavigationView.menu.performIdentifierAction(
                    R.id.menu_item_fcsd,
                    Menu.FLAG_PERFORM_NO_CLOSE
                )
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_lottery

    override fun setMainMenuListener(dataSiwchListener: DataSiwchListener) {
        this.dataSiwchListener = dataSiwchListener
    }

}