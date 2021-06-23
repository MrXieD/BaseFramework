package com.example.contactroom.ui.call

import androidx.fragment.app.Fragment
import com.example.contactroom.databinding.FragmentCallBinding

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */
class ContactDetailFragment : Fragment() {

    private var _binding: FragmentCallBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun createInstance(name: String): ContactDetailFragment {
            return ContactDetailFragment()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}