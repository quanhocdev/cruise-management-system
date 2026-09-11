package com.project.cruise.android.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =====================================================
// LOGIN STATE
// =====================================================

sealed class LoginState {

    object Idle : LoginState()

    object Loading : LoginState()

    data class Success(
        val response: JwtResponse
    ) : LoginState()

    data class Error(
        val message: String
    ) : LoginState()
}


// =====================================================
// REGISTER STATE
// =====================================================

sealed class RegisterState {

    object Idle : RegisterState()

    object Loading : RegisterState()

    data class Success(
        val response: RegisterResponse
    ) : RegisterState()

    data class Error(
        val message: String
    ) : RegisterState()
}


// =====================================================
// VERIFY OTP STATE
// =====================================================

sealed class VerifyOtpState {

    object Idle : VerifyOtpState()

    object Loading : VerifyOtpState()

    data class Success(
        val message: String
    ) : VerifyOtpState()

    data class Error(
        val message: String
    ) : VerifyOtpState()
}

// xem cá nhân
sealed class MeState {
    object Idle : MeState()
    object Loading : MeState()

    data class Success(
        val username: String,
        val role: String
    ) : MeState()

    data class Error(
        val message: String
    ) : MeState()
}

sealed class SessionState {
    object Checking : SessionState()
    data class Authenticated(val username: String, val role: String) : SessionState()
    object Unauthenticated : SessionState()
}

// =====================================================
// AUTH VIEW MODEL
// =====================================================

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Checking)
    val sessionState: StateFlow<SessionState> = _sessionState

    init {
        restoreSession()
    }

    private fun restoreSession() {
        if (!repository.hasStoredSession()) {
            _sessionState.value = SessionState.Unauthenticated
            return
        }

        viewModelScope.launch {
            runCatching { repository.getCurrentUser() }
                .onSuccess { user ->
                    if (user.role.isPassengerRole()) {
                        _sessionState.value = SessionState.Authenticated(user.username, user.role)
                    } else {
                        repository.logout()
                        _sessionState.value = SessionState.Unauthenticated
                    }
                }
                .onFailure {
                    repository.logout()
                    _sessionState.value = SessionState.Unauthenticated
                }
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private val _loginState =
        MutableStateFlow<LoginState>(LoginState.Idle)

    val loginState: StateFlow<LoginState> =
        _loginState


    fun login(
        username: String,
        password: String
    ) {

        if (username.isBlank() || password.isBlank()) {

            _loginState.value =
                LoginState.Error(
                    "Vui lòng nhập tài khoản và mật khẩu"
                )

            return
        }

        viewModelScope.launch {

            _loginState.value =
                LoginState.Loading

            try {

                val response =
                    repository.login(
                        username = username,
                        password = password
                    )

                if (!response.role.isPassengerRole()) {
                    repository.logout()
                    _loginState.value = LoginState.Error("Ứng dụng này chỉ dành cho hành khách")
                } else {
                    _sessionState.value = SessionState.Authenticated(response.username, response.role)
                    _loginState.value = LoginState.Success(response)
                }

            } catch (e: Exception) {

                _loginState.value =
                    LoginState.Error(
                        e.message ?: "Đăng nhập thất bại"
                    )
            }
        }
    }


    fun resetLoginState() {

        _loginState.value =
            LoginState.Idle
    }


    // =====================================================
    // REGISTER
    // =====================================================

    private val _registerState =
        MutableStateFlow<RegisterState>(RegisterState.Idle)

    val registerState: StateFlow<RegisterState> =
        _registerState


    fun register(
        username: String,
        password: String,
        email: String
    ) {

        if (
            username.isBlank() ||
            password.isBlank() ||
            email.isBlank()
        ) {

            _registerState.value =
                RegisterState.Error(
                    "Vui lòng nhập đầy đủ thông tin"
                )

            return
        }

        viewModelScope.launch {

            _registerState.value =
                RegisterState.Loading

            try {

                val response =
                    repository.register(
                        username = username,
                        password = password,
                        email = email
                    )

                _registerState.value =
                    RegisterState.Success(response)

            } catch (e: Exception) {

                _registerState.value =
                    RegisterState.Error(
                        e.message ?: "Đăng ký thất bại"
                    )
            }
        }
    }


    fun resetRegisterState() {

        _registerState.value =
            RegisterState.Idle
    }


    // =====================================================
    // VERIFY EMAIL / OTP
    // =====================================================

    private val _verifyOtpState =
        MutableStateFlow<VerifyOtpState>(
            VerifyOtpState.Idle
        )

    val verifyOtpState: StateFlow<VerifyOtpState> =
        _verifyOtpState


    fun verifyEmail(
        userId: Long,
        otp: String
    ) {

        if (otp.length != 6) {

            _verifyOtpState.value =
                VerifyOtpState.Error(
                    "OTP phải có 6 số"
                )

            return
        }

        viewModelScope.launch {

            _verifyOtpState.value =
                VerifyOtpState.Loading

            try {

                val response =
                    repository.verifyEmail(
                        userId = userId,
                        otp = otp
                    )

                _verifyOtpState.value =
                    VerifyOtpState.Success(
                        response["message"]
                            ?: "Xác thực email thành công"
                    )

            } catch (e: Exception) {

                _verifyOtpState.value =
                    VerifyOtpState.Error(
                        e.message
                            ?: "Xác thực OTP thất bại"
                    )
            }
        }
    }


    fun resetVerifyOtpState() {

        _verifyOtpState.value =
            VerifyOtpState.Idle
    }
    private val _meState =
        MutableStateFlow<MeState>(MeState.Idle)

    val meState: StateFlow<MeState> =
        _meState

    fun getCurrentUser() {

        viewModelScope.launch {

            _meState.value = MeState.Loading

            try {

                val response =
                    repository.getCurrentUser()

                _meState.value =
                    MeState.Success(
                        username = response.username,
                        role = response.role
                    )

            } catch (e: Exception) {

                _meState.value =
                    MeState.Error(
                        e.message ?: "Không thể lấy thông tin người dùng"
                    )
            }
        }
    }

    fun resetMeState() {
        _meState.value = MeState.Idle
    }
    // =====================================================
    // LOGOUT
    // =====================================================

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Xóa token lưu trong SharedPreferences / DataStore
            repository.logout()

            _sessionState.value = SessionState.Unauthenticated

            // 2. Reset toàn bộ các StateAuth về Idle
            resetLoginState()
            resetRegisterState()
            resetVerifyOtpState()
            resetMeState()

            // 3. Thực thi callback chuyển màn hình
            onLogoutSuccess()
        }
    }
}

private fun String.isPassengerRole(): Boolean =
    removePrefix("ROLE_").equals("PASSENGER", ignoreCase = true)
