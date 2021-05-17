package com.example.baseframework.lottery.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.baseframework.R
import com.example.baseframework.databinding.FragmentLotteryBinding
import com.example.imlotterytool.util.InjectorUtil
import com.example.imlotterytool.viewmodel.LotteryViewModel

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryFragment : Fragment() {

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
        binding?.lotteryViewModel = lotteryViewModel
        binding?.lifecycleOwner = this
        binding?.root?.postDelayed({
            lotteryViewModel.requestFcsdHistory("null")
        }, 3000)
        return view
    }


}