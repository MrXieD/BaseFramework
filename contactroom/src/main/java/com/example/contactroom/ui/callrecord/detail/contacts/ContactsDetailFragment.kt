package com.example.contactroom.ui.callrecord.detail.contacts

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
@date: 2021/6/23
@desription:联系人详情
 */
class ContactsDetailFragment : Fragment() {


    private val contactDetailViewModel by viewModels<ContactDetailViewModel>(factoryProducer = {
        InjectUtil.getContactDetailViewModelFactory(requireContext().applicationContext)
    })

    private var _binding: FragmentAllcallrecordsBinding? = null
    private val binding get() = _binding!!
    private val contactName by lazy {
        arguments?.getString(NAME_KEY)
    }

    companion object {
        const val NAME_KEY = "NAME_KEY"
        fun createInstance(name: String): ContactsDetailFragment {
            return ContactsDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(NAME_KEY, name)
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAllcallrecordsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contactName?.let {
            val contactDetailAdapter = ContactDetailAdapter()
            binding.recyclerView?.apply {
                val layout = LinearLayoutManager(this.context)
                layoutManager = layout
                addItemDecoration(DividerItemDecoration(binding.recyclerView.context, layout.orientation))
                adapter = contactDetailAdapter
            }
            contactDetailViewModel.getNumbersForContact(it)
            contactDetailViewModel.contactDetailLiveData.observe(viewLifecycleOwner) { numberList ->
                contactDetailAdapter.submitList(numberList)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}