package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.repository.DataRepository
import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.util.Result
import kotlinx.coroutines.launch

class ElectionsViewModel (private val politicalDataRepository: DataRepository): ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _upcomingElections = MutableLiveData<List<Election>>(mutableListOf())
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    val savedElections = politicalDataRepository.savedElections

    fun getUpcomingElections() {
        _loading.value = true
        viewModelScope.launch {
            when(val result = politicalDataRepository.getRemoteElections()) {
                is Result.Success<List<Election>> -> {
                    _upcomingElections.value = result.data
                    _loading.postValue(false)
                }
                is Result.Error -> {
                    //TODO: Error message
                    _loading.postValue(false)
                }
            }
        }
    }
}