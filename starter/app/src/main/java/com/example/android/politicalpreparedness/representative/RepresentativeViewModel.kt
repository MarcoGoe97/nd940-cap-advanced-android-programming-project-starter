package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.repository.DataRepository
import com.example.android.politicalpreparedness.repository.network.models.Address
import com.example.android.politicalpreparedness.repository.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.Result
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class RepresentativeViewModel(context: Context, private val politicalDataRepository: DataRepository): ViewModel() {

    //From: https://stackoverflow.com/questions/48381818/this-field-leaks-context-object
    private val weakContext = WeakReference(context)

    val addressLine1 = MutableLiveData("")
    val addressLine2 = MutableLiveData("")
    val city = MutableLiveData("")
    val stateIndex = MutableLiveData(0)
    val zip = MutableLiveData("")

    private val _representatives = MutableLiveData<List<Representative>>(mutableListOf())
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun findRepresentativesWithLocation() {
        println("Test")
    }

    fun findRepresentativesWithForm() {
        weakContext.get()?.resources?.getStringArray(R.array.states)?.let { statesList ->
            val address = Address(addressLine1.value!!, addressLine2.value!!, city.value!!, statesList[stateIndex.value!!], zip.value!!)
            getRepresentatives(address)
        }
    }

    private fun getRepresentatives(address: Address) {
        viewModelScope.launch {
            when(val result = politicalDataRepository.getRemoteRepresentatives(address.toFormattedString())) {
                is Result.Success<RepresentativeResponse> -> {
                    _representatives.value = result.data.offices.flatMap { office -> office.getRepresentatives(result.data.officials) }
                    println(result.data.officials.size)
                }
            }
        }
    }

}
