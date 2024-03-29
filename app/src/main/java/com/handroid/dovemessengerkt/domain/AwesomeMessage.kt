package com.handroid.dovemessengerkt.domain

data class AwesomeMessage(
    val text: String,
    val name: String,
    val sender: String,
    val recipient: String,
    val imageUrl: String,
    val isMine: Boolean
)