package com.example.contactroom.ui.callrecord.child.allrecords

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactroom.R
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
    private var listEmpty: Boolean = true

    companion object {
        private const val TAG = "AllCallRecordsFragment"
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
        setHasOptionsMenu(true)
        binding.recyclerView?.apply {
            val layout = LinearLayoutManager(this.context)
            layoutManager = layout
            addItemDecoration(DividerItemDecoration(binding.recyclerView.context, layout.orientation))
            val recordsListAdapter = AllCallRecordsListAdapter()
            adapter = recordsListAdapter
            queryWeatherViewModel.allCallRecordsLiveData.observe(viewLifecycleOwner) { callResultList ->
                listEmpty = callResultList.isEmpty()
                //用这种正确的方式去刷新activity toolbar的action菜单项
                //其实这里就算不调用这个也不会出错，因为onPrepareOptionsMenu方法会在每次菜单
                //显示的时候都被调用，只是说有时候需要在菜单展开状态下刷新
//                requireActivity().invalidateOptionsMenu()
                recordsListAdapter.submitList(callResultList)
            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Log.e(TAG, "onPrepareOptionsMenu: ")
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