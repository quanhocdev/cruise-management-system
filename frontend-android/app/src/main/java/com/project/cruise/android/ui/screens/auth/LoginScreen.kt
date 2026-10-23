
package com.project.cruise.android.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.project.cruise.android.R

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLogin: (String, String) -> Unit,
    onGoogleIdToken: (String) -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    if (!com.project.cruise.android.BuildConfig.GOOGLE_SIGN_IN_ENABLED) {
        OceanAuthForm(
            register = false, onBack = onBackClick,
            onSubmit = { username, password, _ -> onLogin(username, password) },
            loading = isLoading, error = errorMessage
        )
        return
    }
    val context = LocalContext.current
    var googleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var googleLoading by remember {
        mutableStateOf(false)
    }

    val firebaseAuth = remember {
        FirebaseAuth.getInstance()
    }

    val googleSignInClient = remember(context) {
        val options = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(
                context.getString(R.string.default_web_client_id)
            )
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, options)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken.isNullOrBlank()) {
                    googleError = "Không nhận được ID Token từ Google."
                    googleLoading = false
                } else {
                    val credential = GoogleAuthProvider.getCredential(
                        idToken,
                        null
                    )

                    googleLoading = true
                    googleError = null

                    firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            firebaseAuth.currentUser
                                ?.getIdToken(true)
                                ?.addOnSuccessListener { tokenResult ->
                                    val firebaseIdToken = tokenResult.token

                                    if (firebaseIdToken.isNullOrBlank()) {
                                        googleError =
                                            "Không lấy được Firebase ID Token."
                                        googleLoading = false
                                    } else {
                                        googleLoading = false
                                        onGoogleIdToken(firebaseIdToken)
                                    }
                                }
                                ?.addOnFailureListener { exception ->
                                    googleError =
                                        exception.localizedMessage
                                            ?: "Không lấy được Firebase ID Token."
                                    googleLoading = false
                                }
                                ?: run {
                                    googleError =
                                        "Không tìm thấy người dùng Firebase."
                                    googleLoading = false
                                }
                        }
                        .addOnFailureListener { exception ->
                            googleError =
                                exception.localizedMessage
                                    ?: "Đăng nhập Firebase thất bại."
                            googleLoading = false
                        }
                }
            } catch (exception: ApiException) {
                googleError =
                    exception.localizedMessage
                        ?: "Không thể đăng nhập bằng Google."
                googleLoading = false
            }
        } else {
            // Người dùng hủy chọn tài khoản không phải là lỗi đăng nhập.
            googleLoading = false
        }
    }

    OceanAuthForm(
        register = false,
        onBack = onBackClick,
        onSubmit = { username, password, _ ->
            onLogin(username, password)
        },
        loading = isLoading || googleLoading,
        error = errorMessage ?: googleError,
        onGoogleLogin = {
            googleError = null

            googleSignInClient.signOut()
                .addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
        }
    )
}
