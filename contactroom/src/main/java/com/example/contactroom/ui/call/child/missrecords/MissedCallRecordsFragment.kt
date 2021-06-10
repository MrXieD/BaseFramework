package com.example.contactroom.ui.call.child.missrecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.contactroom.databinding.FragmentAllcallrecordsBinding

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class MissedCallRecordsFragment : Fragment() {
    private var _binding: FragmentAllcallrecordsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun createInstance(): MissedCallRecordsFragment {
            return MissedCallRecordsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAllcallrecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}