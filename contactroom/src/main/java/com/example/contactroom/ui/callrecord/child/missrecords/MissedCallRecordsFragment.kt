package com.example.contactroom.ui.callrecord.child.missrecords

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactroom.MainActivity
import com.example.contactroom.R
import com.example.contactroom.databinding.FragmentAllcallrecordsBinding
import com.example.contactroom.ui.callrecord.child.allrecords.AllCallRecordsListAdapter

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class MissedCallRecordsFragment : Fragment() {
    private var _binding: FragmentAllcallrecordsBinding? = null
    private val binding get() = _binding!!
    private var listEmpty: Boolean = true

    companion object {
        private const val TAG = "MissedCallRecordsFragme"
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
        setHasOptionsMenu(true)
        binding.recyclerView?.apply {
            val layout = LinearLayoutManager(this.context)
            layoutManager = layout
            addItemDecoration(DividerItemDecoration(binding.recyclerView.context, layout.orientation))
            val recordsListAdapter = AllCallRecordsListAdapter()
            adapter = recordsListAdapter
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.delete_batch).isEnabled = !listEmpty
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.call_action, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.e(TAG, "onOptionsItemSelected: ${item.itemId},${item.title}")
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}