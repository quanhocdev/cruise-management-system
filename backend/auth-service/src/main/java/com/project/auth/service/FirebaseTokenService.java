
package com.project.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.stereotype.Service;

@Service
public class FirebaseTokenService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public VerifiedGoogleUser verifyIdToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            Object emailVerifiedClaim = decodedToken.getClaims().get("email_verified");

            boolean emailVerified = Boolean.TRUE.equals(emailVerifiedClaim);

            if (uid == null || uid.isBlank()) {
                throw new IllegalArgumentException(
                        "Firebase UID không hợp lệ");
            }

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException(
                        "Tài khoản Google chưa có email");
            }

            if (!emailVerified) {
                throw new IllegalArgumentException(
                        "Email Google chưa được xác minh");
            }

            return new VerifiedGoogleUser(
                    uid,
                    email,
                    decodedToken.getName());

        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Firebase ID token không hợp lệ hoặc đã hết hạn",
                    exception);
        }
    }

    public record VerifiedGoogleUser(
            String firebaseUid,
            String email,
            String displayName) {
    }
}
