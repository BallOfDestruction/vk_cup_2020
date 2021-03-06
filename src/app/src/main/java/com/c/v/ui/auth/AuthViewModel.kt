package com.c.v.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.c.v.ui.base.BaseViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

class AuthViewModel : BaseViewModel() {

    private val _isLogin = MutableLiveData<Boolean>()

    val isLogin : LiveData<Boolean> = _isLogin

    val vkCallback = object : VKAuthCallback {

        override fun onLogin(token: VKAccessToken) {
            _isLogin.postValue(true)
        }

        override fun onLoginFailed(errorCode: Int) {
            setErrorState(Throwable(errorCode.toString()))
        }
    }

    init {
        _isLogin.postValue(VK.isLoggedIn())
    }
}