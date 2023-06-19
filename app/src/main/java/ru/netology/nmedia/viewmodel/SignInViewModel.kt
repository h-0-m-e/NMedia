package ru.netology.nmedia.viewmodel

import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModelState
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    @ApplicationContext
    context: Context
): ViewModel() {



    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState


    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface SignInViewModelEntryPoint {
        fun getApiService(): ApiService
    }

    private val entryPoint = EntryPointAccessors.fromApplication(context, SignInViewModelEntryPoint::class.java)
    fun signIn(login: String, pass: String) = viewModelScope.launch {
        _dataState.value = AuthModelState(loading = true)
        try {
            val response = entryPoint.getApiService().updateUser(login,pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val token: Token = requireNotNull( response.body())
            AppAuth.getInstance().setToken(token)
            _dataState.value = AuthModelState(success = true)
        } catch (e: Exception) {
            _dataState.value = AuthModelState(error = true)
        }
    }

    fun clean(){
        _dataState.value = AuthModelState(loading = false,error = false,success = false)
    }

}
