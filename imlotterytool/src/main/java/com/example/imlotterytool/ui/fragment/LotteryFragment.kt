package com.example.imlotterytool.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.imlotterytool.repository.Status
import com.example.imlotterytool.util.InjectorUtil

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryFragment : Fragment() {

    private val lotteryViewModel by viewModels<LotteryViewModel>(
        factoryProducer = {
            InjectorUtil.getLotteryViewModelFatory(
                requireContext().applicationContext
            )
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}