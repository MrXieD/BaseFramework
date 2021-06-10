package com.example.contactroom.ui.call.child.allrecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactroom.databinding.FragmentAllcallrecordsBinding

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllCallRecordsFragment : Fragment() {

    private var _binding: FragmentAllcallrecordsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun createInstance(): AllCallRecordsFragment {
            return AllCallRecordsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAllcallrecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView?.apply {
            val layout = LinearLayoutManager(this.context)
            layoutManager = layout
            addItemDecoration(DividerItemDecoration(binding.recyclerView.context, layout.orientation))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}