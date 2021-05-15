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

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _showSnackBar = MutableLiveData<Int>(null)
    val showSnackBar: LiveData<Int>
        get() = _showSnackBar

    private val _showToast = MutableLiveData<Int>(null)
    val showToast: LiveData<Int>
        get() = _showToast

    private val _representatives = MutableLiveData<List<Representative>>(mutableListOf())
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun clearAll() {
        addressLine1.value = ""
        addressLine2.value = ""
        city.value = ""
        stateIndex.value = 0
        zip.value = ""
    }

    fun setFormFields(address: Address) {
        weakContext.get()?.resources?.getStringArray(R.array.states)?.let { statesList ->
            addressLine1.value = address.line1
            addressLine2.value = address.line2
            city.value = address.city
            val addressIndex = statesList.indexOf(address.state)
            if(addressIndex != -1) {
                stateIndex.value = addressIndex
            }
            zip.value = address.zip
        }
    }

    fun findRepresentativesWithForm() {
        weakContext.get()?.resources?.getStringArray(R.array.states)?.let { statesList ->
            val address = Address(addressLine1.value!!, addressLine2.value!!, city.value!!, statesList[stateIndex.value!!], zip.value!!)
            getRepresentatives(address)
        }
    }

    fun getRepresentatives(address: Address) {
        _loading.value = true
        viewModelScope.launch {
            when(val result = politicalDataRepository.getRemoteRepresentatives(address.toFormattedString())) {
                is Result.Success<RepresentativeResponse> -> {
                    _representatives.value = result.data.offices.flatMap { office -> office.getRepresentatives(result.data.officials) }
                    _loading.postValue(false)
                }
                is Result.Error -> {
                    if(result.statusCode == 404) {
                        _showToast.value = R.string.representatives_error_not_found
                    } else {
                        _showSnackBar.value = R.string.representatives_error_fetch
                    }
                    _representatives.value = mutableListOf()
                    _loading.postValue(false)
                }
            }
        }
    }

    fun snackBarShown() {
        _showSnackBar.value = null
    }

    fun toastShown() {
        _showToast.value = null
    }

}
