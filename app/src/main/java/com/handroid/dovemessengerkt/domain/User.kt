package com.handroid.dovemessengerkt.domain

data class User(
    var id: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val job_title: String = "",
    val email: String = ""
)