package com.project.cruise.android.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =========================================================
// LOGIN STATE
// =========================================================

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


// =========================================================
// REGISTER STATE
// =========================================================

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


// =========================================================
// VERIFY OTP STATE
// =========================================================

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


// =========================================================
// AUTH VIEW MODEL
// =========================================================

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

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

                _loginState.value =
                    LoginState.Success(response)

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
        email: String,
    ) {

        if (username.isBlank()) {

            _registerState.value =
                RegisterState.Error(
                    "Tài khoản không được để trống"
                )

            return
        }

        if (password.isBlank()) {

            _registerState.value =
                RegisterState.Error(
                    "Mật khẩu không được để trống"
                )

            return
        }

        if (email.isBlank()) {

            _registerState.value =
                RegisterState.Error(
                    "Email không được để trống"
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

        if (otp.isBlank()) {

            _verifyOtpState.value =
                VerifyOtpState.Error(
                    "Vui lòng nhập mã OTP"
                )

            return
        }

        if (otp.length != 6) {

            _verifyOtpState.value =
                VerifyOtpState.Error(
                    "Mã OTP phải gồm 6 chữ số"
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

                val message =
                    response["message"]
                        ?: "Xác thực email thành công"

                _verifyOtpState.value =
                    VerifyOtpState.Success(
                        message
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
}