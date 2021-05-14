package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.util.Result

interface DataRepository {

    val savedElections: LiveData<List<Election>>

    suspend fun getRemoteElections(): Result<List<Election>>

    suspend fun saveElectionToDatabase(election: Election): Result<Unit>
}