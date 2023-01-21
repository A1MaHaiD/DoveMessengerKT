package com.handroid.dovemessengerkt.data.repository


import com.handroid.dovemessengerkt.domain.User
import com.handroid.dovemessengerkt.util.UiState

interface AuthRepository {

    fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun loginUser(user: User, result: (UiState<String>) -> Unit)
    fun forgotPassword(user: User, result: (UiState<String>) -> Unit)
}