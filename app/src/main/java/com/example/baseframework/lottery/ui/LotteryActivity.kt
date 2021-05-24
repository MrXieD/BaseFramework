package com.example.baseframework.lottery.ui

import android.util.Log
import android.view.Menu
import androidx.core.view.get
import com.example.baseframework.R
import com.example.baseframework.activity.BaseDBActivity
import com.example.baseframework.databinding.ActivityLotteryBinding
import com.example.baseframework.lottery.ui.fragment.DataSwitchListener
import com.example.baseframework.lottery.ui.fragment.LotteryFragmentCallBack
import com.example.imlotterytool.db.AppDatabase
import com.example.imlotterytool.util.LOTTERY_TYPE_DLT
import com.example.imlotterytool.util.LOTTERY_TYPE_FCSD
import com.example.imlotterytool.util.LOTTERY_TYPE_SSQ
import com.google.android.material.navigation.NavigationView

class LotteryActivity : BaseDBActivity<ActivityLotteryBinding>(), LotteryFragmentCallBack {

    companion object {
        private const val TAG = "LotteryActivity"
    }

    private lateinit var dataSiwchListener: DataSwitchListener

    //fragment显示相应的数据类型
    val onNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_item_fcsd -> {
                    dataSiwchListener.switchTo(LOTTERY_TYPE_FCSD)
                }
                R.id.menu_item_ssq -> {

                    dataSiwchListener.switchTo(LOTTERY_TYPE_SSQ)
                }
                R.id.menu_item_cjdlt -> {
                    dataSiwchListener.switchTo(LOTTERY_TYPE_DLT)
                }
            }
            dataBinding.drawerLayout.close()
            false//return true to display the item as the selected item
        }

    override fun init() {
        dataBinding.let {
            it.designNavigationView.itemIconTintList = null
            it.lotteryActivity = this
            it.root.post {//默认获取第一种类型数据
                it.designNavigationView.menu.performIdentifierAction(
                    R.id.menu_item_cjdlt,
                    Menu.FLAG_PERFORM_NO_CLOSE
                )
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_lottery

    override fun setMainMenuListener(dataSiwchListener: DataSwitchListener) {
        this.dataSiwchListener = dataSiwchListener
    }

    override fun selectMenuItem(lotteryId: String) {
        val menuItem =
            when (lotteryId) {
                LOTTERY_TYPE_FCSD -> {
                    0
                }
                LOTTERY_TYPE_SSQ -> {
                    1
                }
                LOTTERY_TYPE_DLT -> {
                    2

                }
                else -> {
                    0
                }
            }
        dataBinding?.designNavigationView.menu[menuItem].isChecked = true
    }


    override fun onBackPressed() {
        if (dataBinding.drawerLayout.isOpen) {
            dataBinding.drawerLayout.close()
            AppDatabase.getInstance(applicationContext).close()
        } else {
            super.onBackPressed()
        }
    }
}