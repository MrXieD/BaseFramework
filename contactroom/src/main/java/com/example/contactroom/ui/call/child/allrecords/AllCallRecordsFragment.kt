package com.example.contactroom.ui.call.child.allrecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactroom.databinding.FragmentAllcallrecordsBinding
import com.example.contactroom.util.InjectUtil

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllCallRecordsFragment : Fragment() {

    private var _binding: FragmentAllcallrecordsBinding? = null
    private val binding get() = _binding!!
    private val queryWeatherViewModel by viewModels<AllRecordsViewModel>(factoryProducer = {
        InjectUtil.getAllRecordsViewModelFactory(requireContext().applicationContext)
    })

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
            val recordsListAdapter = AllCallRecordsListAdapter()
            adapter = recordsListAdapter
            //
            queryWeatherViewModel.allCallRecordsLiveData.observe(viewLifecycleOwner) { callResultList ->
                recordsListAdapter.submitList(callResultList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}