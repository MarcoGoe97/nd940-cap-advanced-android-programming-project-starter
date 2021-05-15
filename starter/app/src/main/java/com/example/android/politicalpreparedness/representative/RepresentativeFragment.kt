package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.repository.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class RepresentativeFragment : Fragment() {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private val viewModel: RepresentativeViewModel by viewModel()

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = RepresentativeListAdapter()
        binding.rvRepresentatives.adapter = adapter

        viewModel.representatives.observe(viewLifecycleOwner) {
            it?.let { representatives ->
                //Without data we should not be able to hide the search
                //From https://stackoverflow.com/a/58077083
                if(representatives.isNotEmpty()){
                    binding.mlMain.getTransition(R.id.hideSearch).setEnable(true)
                } else {
                    binding.mlMain.getTransition(R.id.hideSearch).setEnable(false)
                }

                adapter.submitList(representatives)
            } ?: run {
                binding.mlMain.getTransition(R.id.hideSearch).setEnable(false)
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

        binding.buttonSearch.setOnClickListener {
            //Scroll to the top otherwise motionLayout has bugs when result empty
            binding.rvRepresentatives.scrollToPosition(0)

            hideKeyboard()
            viewModel.findRepresentativesWithForm()
        }

        binding.buttonLocation.setOnClickListener {
            //Scroll to the top otherwise motionLayout has bugs when result empty
            binding.rvRepresentatives.scrollToPosition(0)

            if(checkLocationPermissions()) getLocation()
        }

        binding.ivExpand.setOnClickListener {
            binding.mlMain.transitionToStart()
            binding.rvRepresentatives.scrollToPosition(0)
        }

        binding.buttonClearAll.setOnClickListener {
            viewModel.clearAll()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Snackbar.make(requireView(), getString(R.string.representatives_permission_denied_explanation), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationResult: Task<Location> = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location: Location = task.result!!
                val currentAddress = geoCodeLocation(location)
                viewModel.setFormFields(currentAddress)
                viewModel.getRepresentatives(currentAddress)
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}