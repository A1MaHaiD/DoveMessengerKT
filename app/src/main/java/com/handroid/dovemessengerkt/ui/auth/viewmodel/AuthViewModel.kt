package com.handroid.dovemessengerkt.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.handroid.dovemessengerkt.data.repository.AuthRepository
import com.handroid.dovemessengerkt.domain.User
import com.handroid.dovemessengerkt.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    fun register(
        email: String,
        password: String,
        user: User
    ) {
        _register.value = UiState.Loading
        repository.registerUser(
            email = email,
            password = password,
            user = user
        ) {
            _register.value = it
        }
    }

    fun login(
        email: String, password: String
    ) {
        _login.value = UiState.Loading
        repository.loginUser(
            email = email,
            password = password
        )
        {
            _login.value = it
        }
    }
}