package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.repository.DataRepository
import com.example.android.politicalpreparedness.repository.PoliticalDataRepository
import com.example.android.politicalpreparedness.repository.database.ElectionDatabase
import com.example.android.politicalpreparedness.repository.network.CivicsApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                ElectionsViewModel(
                        get() as DataRepository
                )
            }
            viewModel {
                VoterInfoViewModel(
                        get() as DataRepository
                )
            }
            single { PoliticalDataRepository(get(), get()) as DataRepository }
            single { ElectionDatabase.getInstance(this@MyApp).electionDao }
            single { CivicsApi.retrofitService }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}