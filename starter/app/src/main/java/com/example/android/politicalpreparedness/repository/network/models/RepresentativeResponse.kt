package com.example.android.politicalpreparedness.repository.network.models

data class RepresentativeResponse(
        val offices: List<Office>,
        val officials: List<Official>
)