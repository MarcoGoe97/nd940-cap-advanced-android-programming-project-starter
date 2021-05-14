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
import com.google.android.material.snackbar.Snackbar

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
            this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
        })

        binding.rvRemoteElections.adapter = remoteAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let { elections ->
                remoteAdapter.submitList(elections)
            }
        }

        val savedAdapter = ElectionListAdapter(ElectionListener { election ->
            this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
        })

        binding.rvSavedElections.adapter = savedAdapter

        viewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let { savedElections ->
                savedAdapter.submitList(savedElections)
            }
        }

        viewModel.showSnackBar.observe(viewLifecycleOwner) {
            it?.let { messageId ->
                Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_LONG).show()
                viewModel.snackBarShown()
            }
        }

        //load the elections
        viewModel.refreshUpcomingElections()

        return binding.root
    }
}