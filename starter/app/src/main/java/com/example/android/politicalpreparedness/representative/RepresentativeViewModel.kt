package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.repository.DataRepository
import com.example.android.politicalpreparedness.repository.network.models.Address
import com.example.android.politicalpreparedness.repository.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.Result
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val politicalDataRepository: DataRepository): ViewModel() {

    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    private val _representatives = MutableLiveData<List<Representative>>(mutableListOf())
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields
    fun getRepresentativesFromFields() {
        viewModelScope.launch {
            val address = Address("Amphitheatre Parkway", "1600", "Mountain View", "California", "94043")
            when(val result = politicalDataRepository.getRemoteRepresentatives(address.toFormattedString())) {
                is Result.Success<RepresentativeResponse> -> {
                    _representatives.value = result.data.offices.flatMap { office -> office.getRepresentatives(result.data.officials) }
                    println(result.data.officials.size)
                }
            }
        }
    }

}
