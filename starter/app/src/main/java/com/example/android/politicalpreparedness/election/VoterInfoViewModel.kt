package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.repository.DataRepository
import com.example.android.politicalpreparedness.repository.network.models.Division
import com.example.android.politicalpreparedness.repository.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.Result
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val politicalDataRepository: DataRepository) : ViewModel() {

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean>
        get() = _isSaved

    private val _votingLocationUrl = MutableLiveData<String>(null)
    val votingLocationUrl: LiveData<String>
        get() = _votingLocationUrl

    private val _ballotUrl = MutableLiveData<String>(null)
    val ballotUrl: LiveData<String>
        get() = _ballotUrl

    private val _navigateToWebView = MutableLiveData<String>(null)
    val navigateToWebView: LiveData<String>
        get() = _navigateToWebView

    fun loadSavedState(electionId: Int) {
        viewModelScope.launch {
            when(politicalDataRepository.getSavedElectionFromDatabase(electionId)){
                is Result.Success -> _isSaved.value = true
                is Result.Error -> _isSaved.value = false
            }
        }
    }

    fun getVoterInfo(division: Division, electionId: Int) {
        viewModelScope.launch {
            when(val result = politicalDataRepository.getRemoteVoterInfo(
                    "${division.country}, ${division.state}",
                    electionId.toLong())
            ) {
                is Result.Success<VoterInfoResponse> -> {
                    _voterInfo.value = result.data
                    if(!result.data.state.isNullOrEmpty()) {
                        _ballotUrl.value = result.data.state[0].electionAdministrationBody.ballotInfoUrl
                        _votingLocationUrl.value = result.data.state[0].electionAdministrationBody.votingLocationFinderUrl
                    }
                }
                is Result.Error -> {
                    //TODO: No info found placeholder
                }
            }
        }
    }

    fun toggleSaveElection() {
        viewModelScope.launch {
            voterInfo.value?.election?.let { election ->
                val result = if(isSaved.value == true) {
                    politicalDataRepository.deleteElectionFromDatabase(election.id)
                } else {
                    politicalDataRepository.saveElectionToDatabase(election)
                }

                when(result) {
                    is Result.Success -> {
                        loadSavedState(election.id)
                        //TODO: Show success message?
                    }
                    is Result.Error -> {
                        //TODO: Show error message
                    }
                }
            } ?: run {
                //TODO: Show error message
            }
        }
    }

    fun onBallotUrlClicked() {
        _ballotUrl.value?.let {
            _navigateToWebView.value = it
        }
    }

    fun onLocationUrlClicked() {
        _votingLocationUrl.value?.let {
            _navigateToWebView.value = it
        }
    }

    fun navigationToWebViewDone() {
        _navigateToWebView.value = null
    }

}