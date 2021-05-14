package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.repository.DataRepository
import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.repository.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.Result
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val politicalDataRepository: DataRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

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

    private val _correspondenceAddress = MutableLiveData<String>(null)
    val correspondenceAddress: LiveData<String>
        get() = _correspondenceAddress

    private val _navigateToWebView = MutableLiveData<String>(null)
    val navigateToWebView: LiveData<String>
        get() = _navigateToWebView

    private val _showSnackBar = MutableLiveData<Int>(null)
    val showSnackBar: LiveData<Int>
        get() = _showSnackBar

    private val _showToast = MutableLiveData<Int>(null)
    val showToast: LiveData<Int>
        get() = _showToast

    private lateinit var currentElection: Election

    fun setCurrentElectionFromArgs(election: Election) {
        currentElection = election
    }

    fun loadSavedState() {
        _loadingState.value = true
        viewModelScope.launch {
            when(politicalDataRepository.getSavedElectionFromDatabase(currentElection.id)){
                is Result.Success -> {
                    _isSaved.postValue(true)
                    _loadingState.postValue(false)
                }
                is Result.Error -> {
                    _isSaved.postValue(false)
                    _loadingState.postValue(false)
                }
            }
        }
    }

    fun refreshVoterInfo() {
        _loading.value = true
        viewModelScope.launch {
            when(val result = politicalDataRepository.getRemoteVoterInfo(
                    "${currentElection.division.country}, ${currentElection.division.state}",
                    currentElection.id.toLong())
            ) {
                is Result.Success<VoterInfoResponse> -> {
                    _voterInfo.value = result.data
                    if(!result.data.state.isNullOrEmpty()) {
                        _ballotUrl.value = result.data.state[0].electionAdministrationBody.ballotInfoUrl
                        _votingLocationUrl.value = result.data.state[0].electionAdministrationBody.votingLocationFinderUrl
                        result.data.state[0].electionAdministrationBody.correspondenceAddress?.let{ address ->
                            _correspondenceAddress.value = address.toFormattedString()
                        }
                    }
                    _loading.postValue(false)
                }
                is Result.Error -> {
                    _loading.postValue(false)
                    _showSnackBar.postValue(R.string.voterInfo_error_fetch_data)
                }
            }
        }
    }

    fun toggleSaveElection() {
        viewModelScope.launch {
            val result = if(isSaved.value == true) {
                politicalDataRepository.deleteElectionFromDatabase(currentElection.id)
            } else {
                politicalDataRepository.saveElectionToDatabase(currentElection)
            }

            when(result) {
                is Result.Success -> {
                    if(isSaved.value == true) {
                        _showToast.postValue(R.string.voterInfo_save_success)
                    } else {
                        _showToast.postValue(R.string.voterInfo_remove_success)
                    }

                    loadSavedState()
                }
                is Result.Error -> {
                    if(isSaved.value == true) {
                        _showToast.postValue(R.string.voterInfo_save_error)
                    } else {
                        _showToast.postValue(R.string.voterInfo_remove_error)
                    }
                }
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

    fun snackBarShown() {
        _showSnackBar.value = null
    }

    fun toastShown() {
        _showToast.value = null
    }
}