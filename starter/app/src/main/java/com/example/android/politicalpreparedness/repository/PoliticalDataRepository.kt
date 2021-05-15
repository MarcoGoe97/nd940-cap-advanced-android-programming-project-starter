package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.repository.database.ElectionDao
import com.example.android.politicalpreparedness.repository.network.CivicsApiService
import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.repository.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.Result

class PoliticalDataRepository(
        private val electionDao: ElectionDao,
        private val civicsApiService: CivicsApiService
): DataRepository {

    override val savedElections: LiveData<List<Election>> = electionDao.getElections()

    override suspend fun getRemoteElections(): Result<List<Election>> {
        return try {
            val result = civicsApiService.getElections()
            Result.Success(result.elections)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun getRemoteVoterInfo(address: String, electionId: Long): Result<VoterInfoResponse> {
        return try {
            val result = civicsApiService.getVoterInfo(address, electionId)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun getRemoteRepresentatives(address: String): Result<RepresentativeResponse> {
        return try {
            val result = civicsApiService.getRepresentatives(address)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun saveElectionToDatabase(election: Election): Result<Unit> {
        return try {
            electionDao.insert(election)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun deleteElectionFromDatabase(electionId: Int): Result<Unit> {
        return try {
            electionDao.deleteElectionById(electionId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    override suspend fun getSavedElectionFromDatabase(electionId: Int): Result<Election> {
        return try {
            val result = electionDao.getElectionById(electionId)
            if(result != null) {
                Result.Success(result)
            } else {
                Result.Error("", 404)
            }
        } catch (e: Exception) {
            Result.Error(e.message, 0)
        }
    }


}