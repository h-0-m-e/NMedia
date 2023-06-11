package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(
    context: Context
) {
    companion object{
        private const val TOKEN_KEY = "token"
        private const val ID_KEY = "id"

        @Volatile
        private var INSTANCE: AppAuth? = null

        fun initApp(context: Context){
            INSTANCE = AppAuth(context)
        }

        fun getInstance(): AppAuth = requireNotNull(INSTANCE) {
            "You must call to police before (I'm jokin, I mean initApp)"
        }

    }
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data = MutableStateFlow<Token?>(null)
    val data = _data.asStateFlow()

    init{
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        if (token == null || id == 0L){
            _data.value = null
            prefs.edit {
                clear()
                apply()
            }
        } else {
            _data.value = Token(id, token)
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val request = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
                Api.service.sendPushToken(request)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun setToken(token: Token){
        _data.value = token
        prefs.edit{
            putString(TOKEN_KEY, token.token)
            putLong(ID_KEY, token.id)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun clearAuth(){
        _data.value = null
        prefs.edit {
            clear()
            apply()
        }
        sendPushToken()
    }
}
