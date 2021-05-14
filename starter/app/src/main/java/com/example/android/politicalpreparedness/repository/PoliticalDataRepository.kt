package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.repository.database.ElectionDao
import com.example.android.politicalpreparedness.repository.network.CivicsApiService
import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.util.Result

class PoliticalDataRepository(
        private val electionDao: ElectionDao,
        private val civicsApiService: CivicsApiService
): DataRepository {

    override suspend fun getElections(): Result<List<Election>> {
        return try {
            val result = civicsApiService.getElections()
            Result.Success(result.elections)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

}