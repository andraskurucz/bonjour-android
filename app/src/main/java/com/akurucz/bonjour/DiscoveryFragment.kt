package com.akurucz.bonjour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DiscoveryFragment : Fragment() {

    private val viewModel: DiscoveryViewModel by activityViewModels()
    private val listAdapter = ServicesAdapter(this::onItemClicked)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.service_list).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        }

        viewModel.discoveredServices.observe(viewLifecycleOwner, Observer {
            listAdapter.submitList(it)
        })
    }

    private fun onItemClicked(item: BonjourService) {
        findNavController().navigate(R.id.action_DiscoveryFragment_to_ServiceDetailFragment)
    }
}