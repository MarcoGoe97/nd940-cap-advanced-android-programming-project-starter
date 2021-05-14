package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    private val viewModel: ElectionsViewModel by viewModel()

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val remoteAdapter = ElectionListAdapter(ElectionListener { election ->
            this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
        })

        binding.rvRemoteElections.adapter = remoteAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let { elections ->
                remoteAdapter.submitList(elections)
            }
        }

        val savedAdapter = ElectionListAdapter(ElectionListener { election ->
            this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
        })

        binding.rvSavedElections.adapter = savedAdapter

        viewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let { savedElections ->
                savedAdapter.submitList(savedElections)
            }
        }

        //load the elections
        viewModel.getUpcomingElections()

        return binding.root
    }
}