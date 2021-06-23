package com.example.contactroom.ui.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.contactroom.databinding.FragmentCallBinding
import com.example.contactroom.ui.call.child.SigRecordsViewModel
import com.example.contactroom.ui.call.child.allrecords.AllRecordsViewModel
import com.example.contactroom.util.InjectUtil

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */
class CallRecordDetailFragment : Fragment() {

    private var _binding: FragmentCallBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sigRecordsViewModel by viewModels<SigRecordsViewModel>(factoryProducer = {
        InjectUtil.getSigRecordsViewModelFactory(requireContext().applicationContext)
    })

    companion object {
        fun createInstance(): CallRecordDetailFragment {
            return CallRecordDetailFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }

}