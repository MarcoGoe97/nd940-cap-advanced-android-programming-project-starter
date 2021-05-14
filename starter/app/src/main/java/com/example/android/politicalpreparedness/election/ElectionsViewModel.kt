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

    init {
        getUpcomingElections()
    }

    private val _upcomingElections = MutableLiveData<List<Election>>(mutableListOf())
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    private fun getUpcomingElections() {
        viewModelScope.launch {
            val result = politicalDataRepository.getElections()
            when(result) {
                is Result.Success<List<Election>> -> {
                    _upcomingElections.value = result.data
                }
                is Result.Error -> {

                }
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}