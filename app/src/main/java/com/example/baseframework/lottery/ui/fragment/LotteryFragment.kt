package com.example.baseframework.lottery.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.baseframework.R
import com.example.baseframework.databinding.FragmentLotteryBinding
import com.example.imlotterytool.repository.Status
import com.example.imlotterytool.util.InjectorUtil
import com.example.imlotterytool.util.getTitleListByLotteryType
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

    private val lotteryViewModel by viewModels<LotteryViewModel>(
        factoryProducer = {
            InjectorUtil.getLotteryViewModelFatory(
                requireContext().applicationContext
            )
        }
    )


    var binding: FragmentLotteryBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lottery, container, false)
        binding = DataBindingUtil.bind<FragmentLotteryBinding>(view)
//        binding?.lotteryViewModel = lotteryViewModel
        binding?.lifecycleOwner = this
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LotteryFragmentCallBack) {
            context.setMainMenuListener(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        lotteryViewModel.lotteryLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let { resource ->
                when (resource.status) {
                    Status.LOADING
                    -> {
                        showSnackBar("正在获取数据...")
                    }
                    Status.SUCCESS -> {
                        val data = resource.data
                        data?.let { data ->
                            val dataList = data.list
                            dataList?.let {
                                showSnackBar("获取数据成功！")
                                //显示数据
                                binding?.lottertView?.refreshData(dataList, getTitleListByLotteryType(data.lotteryId))
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

    override fun switchTo(lotteryId: String) {
        Log.d(TAG, "query: $lotteryId")
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
}


interface DataSwitchListener {
    fun switchTo(lotteryId: String)
}

