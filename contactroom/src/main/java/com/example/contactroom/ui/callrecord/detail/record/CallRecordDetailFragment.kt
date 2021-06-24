package com.example.contactroom.ui.callrecord.detail.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactroom.databinding.FragmentAllcallrecordsBinding
import com.example.contactroom.ui.callrecord.child.SigRecordsViewModel
import com.example.contactroom.util.InjectUtil

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */
class CallRecordDetailFragment : Fragment() {

    companion object {
        private const val TAG = "CallRecordDetailFragmen"
        const val CONTACTNAME_KEY = "CONTACTNAME_KEY"
        const val NUMBAE_KEY = "NUMBAE_KEY"
        fun createInstance(name: String?, number: String?): CallRecordDetailFragment {
            val callRecordDetailFragment = CallRecordDetailFragment()
            val args = Bundle().apply {
                putString(CONTACTNAME_KEY, name)
                putString(NUMBAE_KEY, number)
            }
            callRecordDetailFragment.arguments = args
            return callRecordDetailFragment
        }
    }

    private var _binding: FragmentAllcallrecordsBinding? = null

    private val binding get() = _binding!!

    private val contactName by lazy {
        arguments?.getString(CONTACTNAME_KEY)
    }
    private val number by lazy {
        arguments?.getString(NUMBAE_KEY)
    }

    private val sigRecordsViewModel by viewModels<SigRecordsViewModel>(factoryProducer = {
        InjectUtil.getSigRecordsViewModelFactory(requireContext().applicationContext)
    })


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAllcallrecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recordsListAdapter = RecordDetailAdapter()
        binding.recyclerView?.apply {
            val layout = LinearLayoutManager(this.context)
            layoutManager = layout
            addItemDecoration(DividerItemDecoration(binding.recyclerView.context, layout.orientation))
            adapter = recordsListAdapter
        }

        when {
            contactName != null -> {
                sigRecordsViewModel.getSigRecordsByName(contactName!!)
                //查询此人的所有号码的通话记录
                sigRecordsViewModel.sigNameRecordsLiveData.observe(viewLifecycleOwner) {
                    recordsListAdapter.submitList(it)
                }

            }
            number != null -> {
                sigRecordsViewModel.getSigRecodsByNumber(number!!)
                //查询此号码的所有通话记录
                sigRecordsViewModel.sigNumberRecodsLiveData.observe(viewLifecycleOwner) {
                    recordsListAdapter.submitList(it)
                }
            }
            else -> {
                Log.e(TAG, "onViewCreated: did not find any args!(contactName or number)")
            }
        }
    }

}