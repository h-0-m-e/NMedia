package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModelState
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth
): ViewModel() {



    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState

    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _dataState.value = AuthModelState(loading = true)
        try {
            val response = apiService.updateUser(login,pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val token: Token = requireNotNull( response.body())
            appAuth.setToken(token)
            _dataState.value = AuthModelState(success = true)
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true)
        }
    }

    fun clean(){
        _dataState.value = AuthModelState(loading = false,error = false,success = false)
    }

}
