package com.example.baseframework.lottery.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.baseframework.R
import com.example.baseframework.databinding.FragmentLotteryBinding
import com.example.imlotterytool.repository.Status
import com.example.imlotterytool.util.*
import com.example.imlotterytool.viewmodel.LotteryViewModel
import com.google.android.material.snackbar.Snackbar

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryFragment : Fragment(), DataSwitchListener {

    companion object {
        private const val TAG = "LotteryFragment"
    }
    private lateinit var fragmentCallBack: LotteryFragmentCallBack
    private val lotteryViewModel by viewModels<LotteryViewModel>(
        factoryProducer = { InjectorUtil.getLotteryViewModelFatory(requireContext().applicationContext) }
    )


    var binding: FragmentLotteryBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lottery, container, false)
        binding = DataBindingUtil.bind<FragmentLotteryBinding>(view)
        binding?.lifecycleOwner = this
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LotteryFragmentCallBack) {
            fragmentCallBack = context
            fragmentCallBack.setMainMenuListener(this)
        }
    }

    private var index = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lotteryViewModel.lotteryLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> showSnackBar("正在获取数据...")
                    Status.SUCCESS -> {
                        val data = resource.data
                        data?.let { data ->
                            val dataList = data.list
                            dataList?.let {
                                showSnackBar("获取数据成功！")
                                //显示数据
                                when (lotteryType) {
                                    LOTTERY_TYPE_SSQ -> {
                                        binding?.lottertView?.isShowBrokenLine = false
                                        binding?.lottertView?.setBitCountList(listOf(IntRange(0, 32), IntRange(33, 48)))
                                    }
                                    LOTTERY_TYPE_DLT -> {
                                        binding?.lottertView?.isShowBrokenLine = false
                                        binding?.lottertView?.setBitCountList(listOf(IntRange(0, 34), IntRange(35, 47)))
                                    }
                                    LOTTERY_TYPE_FCSD, LOTTERY_TYPE_PL3 -> {
                                        binding?.lottertView?.isShowBrokenLine = true
                                        binding?.lottertView?.setBitCountList(listOf(IntRange(0, 9), IntRange(10, 19), IntRange(20, 29)))
                                    }
                                    LOTTERY_TYPE_PL5 -> {
                                        binding?.lottertView?.isShowBrokenLine = true
                                        binding?.lottertView?.setBitCountList(
                                            listOf(IntRange(0, 9), IntRange(10, 19), IntRange(20, 29), IntRange(30, 39), IntRange(40, 49))
                                        )
                                    }

                                    LOTTERY_TYPE_7XC -> {
                                        binding?.lottertView?.isShowBrokenLine = true
                                        binding?.lottertView?.setBitCountList(
                                            listOf(
                                                IntRange(0, 9),
                                                IntRange(10, 19),
                                                IntRange(20, 29),
                                                IntRange(30, 39),
                                                IntRange(40, 49),
                                                IntRange(50, 59),
                                                IntRange(60, 74)
                                            )
                                        )

                                    }
                                    else -> {
                                        binding?.lottertView?.isShowBrokenLine = false

                                    }
                                }
                                fragmentCallBack.selectMenuItem(data.lotteryId)

                                binding?.lottertView?.refreshData(it, getTitleListByLotteryType(data.lotteryId))
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding?.let {
                            var msg = "error:"
                            resource.message?.let { message -> msg = message }
                            showSnackBar(msg)
                        }
                    }
                }
            }
        }
    }

    private var lotteryType = ""
    override fun switchTo(lotteryId: String) {
        Log.d(TAG, "query: $lotteryId")
        lotteryType = lotteryId
        lotteryViewModel.requestHistory(lotteryId)
    }


    private fun showSnackBar(message: String) {
        binding?.let {
            Snackbar.make(it.lottertView, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}


interface LotteryFragmentCallBack {
    fun setMainMenuListener(dataSiwchListener: DataSwitchListener)
    fun selectMenuItem(lotteryId: String)
}


interface DataSwitchListener {
    fun switchTo(lotteryId: String)
}

