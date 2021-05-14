package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.repository.network.models.Election
import com.example.android.politicalpreparedness.util.Result

interface DataRepository {
    suspend fun getElections(): Result<List<Election>>
}