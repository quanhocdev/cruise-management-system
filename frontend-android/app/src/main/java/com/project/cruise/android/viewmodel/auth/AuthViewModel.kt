package com.project.cruise.android.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.auth.JwtResponse
import com.project.cruise.android.data.dto.auth.RegisterResponse
import com.project.cruise.android.data.repository.AuthRepository
import com.project.cruise.android.ui.screens.pos.PosRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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


sealed class GoogleLoginState {
    object Idle : GoogleLoginState()
    object Loading : GoogleLoginState()

    data class OtpRequired(
        val userId: String,
        val message: String
    ) : GoogleLoginState()

    data class Success(
        val response: JwtResponse
    ) : GoogleLoginState()

    data class Error(
        val message: String
    ) : GoogleLoginState()
}

sealed class GoogleOtpState {
    object Idle : GoogleOtpState()
    object Loading : GoogleOtpState()

    data class Success(
        val response: JwtResponse
    ) : GoogleOtpState()

    data class Error(
        val message: String
    ) : GoogleOtpState()
}



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

sealed class PosLoginState {
    object Idle : PosLoginState()
    object Loading : PosLoginState()
    data class Success(val response: JwtResponse) : PosLoginState()
    data class Error(val message: String) : PosLoginState()
}

sealed class SessionState {
    object Checking : SessionState()
    data class Authenticated(val username: String, val role: String) : SessionState()
    object Unauthenticated : SessionState()
}

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
                    if (user.role.isPassengerRole() || user.role.isSupportedPosRole()) {
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



    private val _googleLoginState =
        MutableStateFlow<GoogleLoginState>(GoogleLoginState.Idle)

    val googleLoginState: StateFlow<GoogleLoginState> =
        _googleLoginState

    private val _googleOtpState =
        MutableStateFlow<GoogleOtpState>(GoogleOtpState.Idle)

    val googleOtpState: StateFlow<GoogleOtpState> =
        _googleOtpState

    fun googleLogin(firebaseIdToken: String) {
        if (firebaseIdToken.isBlank()) {
            _googleLoginState.value =
                GoogleLoginState.Error("Không nhận được token từ Google")
            return
        }

        viewModelScope.launch {
            _googleLoginState.value = GoogleLoginState.Loading

            try {
                val result = repository.googleLogin(firebaseIdToken)

                if (result.isOtpRequired) {
                    val userId = result.userId
                    if (userId == null) {
                        _googleLoginState.value =
                            GoogleLoginState.Error(
                                "Backend yêu cầu OTP nhưng không trả về userId"
                            )
                        return@launch
                    }

                    _googleLoginState.value =
                        GoogleLoginState.OtpRequired(
                            userId = userId,
                            message = result.message
                                ?: "Vui lòng nhập mã OTP được gửi đến email"
                        )
                } else {
                    val jwt = result.toJwtResponse()
                    if (jwt == null) {
                        _googleLoginState.value =
                            GoogleLoginState.Error(
                                "Không nhận được thông tin đăng nhập hợp lệ"
                            )
                        return@launch
                    }

                    _sessionState.value =
                        SessionState.Authenticated(jwt.username, jwt.role)

                    _googleLoginState.value =
                        GoogleLoginState.Success(jwt)
                }
            } catch (e: Exception) {
                _googleLoginState.value =
                    GoogleLoginState.Error(
                        e.message ?: "Đăng nhập Google thất bại"
                    )
            }
        }
    }

    fun verifyGoogleOtp(userId: String, otp: String) {
        if (!otp.matches(Regex("^\\d{6}$"))) {
            _googleOtpState.value =
                GoogleOtpState.Error("OTP phải gồm đúng 6 chữ số")
            return
        }

        viewModelScope.launch {
            _googleOtpState.value = GoogleOtpState.Loading

            try {
                val response = repository.verifyGoogleOtp(userId, otp)

                _sessionState.value =
                    SessionState.Authenticated(
                        response.username,
                        response.role
                    )

                _googleOtpState.value =
                    GoogleOtpState.Success(response)
            } catch (e: Exception) {
                _googleOtpState.value =
                    GoogleOtpState.Error(
                        e.message ?: "Xác thực OTP Google thất bại"
                    )
            }
        }
    }

    fun resetGoogleLoginState() {
        _googleLoginState.value = GoogleLoginState.Idle
    }

    fun resetGoogleOtpState() {
        _googleOtpState.value = GoogleOtpState.Idle
    }


    fun resetLoginState() {

        _loginState.value =
            LoginState.Idle
    }

    private val _posLoginState = MutableStateFlow<PosLoginState>(PosLoginState.Idle)
    val posLoginState: StateFlow<PosLoginState> = _posLoginState

    fun loginPos(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _posLoginState.value = PosLoginState.Error("Vui lòng nhập tài khoản và mật khẩu")
            return
        }

        viewModelScope.launch {
            _posLoginState.value = PosLoginState.Loading
            try {
                val response = repository.login(username, password)
                if (!response.role.isSupportedPosRole()) {
                    repository.logout()
                    _posLoginState.value = PosLoginState.Error(
                        "Máy POS chỉ dành cho FINANCE, CONVENIENCE, ONBOARD hoặc SHORE"
                    )
                } else {
                    _sessionState.value = SessionState.Authenticated(response.username, response.role)
                    _posLoginState.value = PosLoginState.Success(response)
                }
            } catch (exception: Exception) {
                _posLoginState.value = PosLoginState.Error(
                    exception.message ?: "Đăng nhập máy POS thất bại"
                )
            }
        }
    }

    fun resetPosLoginState() {
        _posLoginState.value = PosLoginState.Idle
    }

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

    private val _verifyOtpState =
        MutableStateFlow<VerifyOtpState>(
            VerifyOtpState.Idle
        )

    val verifyOtpState: StateFlow<VerifyOtpState> =
        _verifyOtpState


    fun verifyEmail(
        userId: String,
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
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Xóa token lưu trong SharedPreferences / DataStore
            repository.logout()

            _sessionState.value = SessionState.Unauthenticated

            // 2. Reset toàn bộ các StateAuth về Idle
            resetLoginState()
            resetPosLoginState()
            resetRegisterState()
            resetVerifyOtpState()
            resetMeState()

            resetGoogleLoginState()
            resetGoogleOtpState()

            // 3. Thực thi callback chuyển màn hình
            onLogoutSuccess()
        }
    }
}

private fun String.isPassengerRole(): Boolean =
    trim().uppercase().removePrefix("ROLE_") == "PASSENGER"

private fun String.isSupportedPosRole(): Boolean =
    PosRole.fromApiRole(this) != null
