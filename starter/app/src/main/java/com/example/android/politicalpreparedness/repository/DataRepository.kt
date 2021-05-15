package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.repository.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.Result

interface DataRepository {

    val savedElections: LiveData<List<Election>>

    suspend fun getRemoteElections(): Result<List<Election>>

    suspend fun getRemoteVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse>

    suspend fun getRemoteRepresentatives(address: String): Result<RepresentativeResponse>

    suspend fun saveElectionToDatabase(election: Election): Result<Unit>

    suspend fun deleteElectionFromDatabase(electionId: Int): Result<Unit>

    suspend fun getSavedElectionFromDatabase(electionId: Int): Result<Election>
}