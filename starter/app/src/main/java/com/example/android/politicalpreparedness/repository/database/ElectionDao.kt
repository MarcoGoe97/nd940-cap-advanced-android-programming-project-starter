package com.example.android.politicalpreparedness.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.repository.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    @Query("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table where id = :electionId")
    suspend fun getElectionById(electionId: Int): Election?

    @Query("DELETE FROM election_table where id = :electionId")
    suspend fun deleteElectionById(electionId: Int)

    @Query("DELETE FROM election_table")
    suspend fun deleteAllReminders()

}