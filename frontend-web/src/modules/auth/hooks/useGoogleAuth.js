import { useState } from "react";
import { signInWithPopup, getIdToken, signOut } from "firebase/auth";
import { useLocation, useNavigate } from "react-router-dom";

import { firebaseAuth, googleProvider } from "../../../config/firebase";
import { useAuth } from "../../../context/AuthContext";
import googleAuthService from "../services/googleAuthService";
import { getRedirectPathByRole } from "../../../routes/roleRoutes";

export default function useGoogleAuth() {
  const [googleLoading, setGoogleLoading] = useState(false);
  const [googleError, setGoogleError] = useState("");

  const { refreshUser } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const getTargetPath = (role) => {
    const fromPath = location.state?.from;

    if (role === "PASSENGER" && fromPath) {
      return `${fromPath.pathname}${fromPath.search || ""}`;
    }

    return role === "PASSENGER" ? "/" : getRedirectPathByRole(role);
  };

  const loginWithGoogle = async () => {
    setGoogleError("");
    setGoogleLoading(true);

    let firebaseUser = null;

    try {
      const credential = await signInWithPopup(firebaseAuth, googleProvider);

      firebaseUser = credential.user;

      // Chỉ gửi Firebase ID token đến backend.
      const idToken = await getIdToken(firebaseUser, true);

      const result = await googleAuthService.login(idToken);

      if (result.status === "OTP_REQUIRED") {
        // OTP do backend gửi qua email; không lưu Firebase ID token
        // vào router state hoặc localStorage.
        await signOut(firebaseAuth);

        navigate("/google-verify-otp", {
          replace: true,
          state: {
            userId: result.userId,
            email: firebaseUser.email || "",
            from: location.state?.from,
          },
        });

        return;
      }

      // Backend đã cấp cookie phiên nội bộ.
      // Lấy user và role chính thức từ /auth/me.
      const user = await refreshUser();

      if (!user?.role) {
        throw new Error(
          "Đăng nhập thành công nhưng không lấy được thông tin tài khoản.",
        );
      }

      await signOut(firebaseAuth);

      navigate(getTargetPath(user.role), {
        replace: true,
      });
    } catch (err) {
      // Popup bị đóng hoặc người dùng hủy đăng nhập.
      if (
        err.code === "auth/popup-closed-by-user" ||
        err.code === "auth/cancelled-popup-request"
      ) {
        setGoogleError("Bạn đã hủy đăng nhập Google.");
      } else {
        setGoogleError(
          err.response?.data?.message ||
            err.message ||
            "Không thể đăng nhập bằng Google.",
        );
      }

      // Chỉ dọn phiên Firebase phía client.
      // Không xóa cookie phiên nội bộ của backend.
      if (firebaseUser) {
        try {
          await signOut(firebaseAuth);
        } catch {
          // Không ghi đè lỗi đăng nhập ban đầu.
        }
      }
    } finally {
      setGoogleLoading(false);
    }
  };

  return {
    loginWithGoogle,
    googleLoading,
    googleError,
    clearGoogleError: () => setGoogleError(""),
  };
}
