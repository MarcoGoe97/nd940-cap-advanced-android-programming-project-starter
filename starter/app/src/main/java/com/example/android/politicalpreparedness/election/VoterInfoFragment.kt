package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoterInfoFragment : Fragment() {

    private val viewModel: VoterInfoViewModel by viewModel()
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val election = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElection

        viewModel.navigateToWebView.observe(viewLifecycleOwner) {
            it?.let { url ->
                startWebView(url)
                viewModel.navigationToWebViewDone()
            }
        }

        viewModel.showSnackBar.observe(viewLifecycleOwner) {
            it?.let { messageId ->
                Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_LONG).show()
                viewModel.snackBarShown()
            }
        }

        viewModel.showToast.observe(viewLifecycleOwner) {
            it?.let { messageId ->
                Toast.makeText(requireContext(), getString(messageId), Toast.LENGTH_SHORT).show()
                viewModel.toastShown()
            }
        }

        viewModel.setCurrentElectionFromArgs(election)
        viewModel.loadSavedState()
        //More info about call parameters https://knowledge.udacity.com/questions/507353
        viewModel.refreshVoterInfo()

        return binding.root
    }

    //From https://stackoverflow.com/a/2201999
    private fun startWebView(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}