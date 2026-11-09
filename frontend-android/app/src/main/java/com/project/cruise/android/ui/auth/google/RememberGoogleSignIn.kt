package com.project.cruise.android.ui.auth.google

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.project.cruise.android.R

/** Trạng thái và hành động đăng nhập Google để UI sử dụng. */
data class GoogleSignInState(
    val loading: Boolean,
    val error: String?,
    val launch: () -> Unit,
)

/**
 * Gom toàn bộ luồng Google Sign-In -> Firebase -> Firebase ID Token.
 * [onIdToken] nhận Firebase ID Token khi đăng nhập thành công.
 */
@Composable
fun rememberGoogleSignIn(onIdToken: (String) -> Unit): GoogleSignInState {
    val context = LocalContext.current
    val currentOnIdToken by rememberUpdatedState(onIdToken)

    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val firebaseAuth = remember { FirebaseAuth.getInstance() }

    val client =
        remember(context) {
            val options =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            GoogleSignIn.getClient(context, options)
        }

    fun fail(message: String) {
        error = message
        loading = false
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                // Người dùng hủy chọn tài khoản không phải là lỗi đăng nhập.
                loading = false
                return@rememberLauncherForActivityResult
            }

            try {
                val account =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        .getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken.isNullOrBlank()) {
                    fail("Không nhận được ID Token từ Google.")
                    return@rememberLauncherForActivityResult
                }

                loading = true
                error = null

                firebaseAuth
                    .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                    .addOnSuccessListener {
                        val user = firebaseAuth.currentUser
                        if (user == null) {
                            fail("Không tìm thấy người dùng Firebase.")
                            return@addOnSuccessListener
                        }
                        user
                            .getIdToken(true)
                            .addOnSuccessListener { tokenResult ->
                                val firebaseIdToken = tokenResult.token
                                if (firebaseIdToken.isNullOrBlank()) {
                                    fail("Không lấy được Firebase ID Token.")
                                } else {
                                    loading = false
                                    currentOnIdToken(firebaseIdToken)
                                }
                            }
                            .addOnFailureListener {
                                fail(it.localizedMessage ?: "Không lấy được Firebase ID Token.")
                            }
                    }
                    .addOnFailureListener {
                        fail(it.localizedMessage ?: "Đăng nhập Firebase thất bại.")
                    }
            } catch (exception: ApiException) {
                fail(exception.localizedMessage ?: "Không thể đăng nhập bằng Google.")
            }
        }

    return GoogleSignInState(
        loading = loading,
        error = error,
        launch = {
            error = null
            client.signOut().addOnCompleteListener { launcher.launch(client.signInIntent) }
        },
    )
}