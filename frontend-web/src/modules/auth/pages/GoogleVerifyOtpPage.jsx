// src/modules/auth/pages/GoogleVerifyOtpPage.jsx

import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ShieldCheck } from "lucide-react";

import googleAuthService from "../services/googleAuthService";
import { useAuth } from "../../../context/AuthContext";
import { getRedirectPathByRole } from "../../../routes/roleRoutes";

import AuthLayout from "../components/AuthLayout";
import OtpVerifyForm from "../components/OtpVerifyForm";
import { SessionExpired } from "../components/AuthControls";

export default function GoogleVerifyOtpPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { refreshUser } = useAuth();

  const userId = location.state?.userId;
  const email = location.state?.email || "";

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!userId) {
    return (
      <SessionExpired
        icon={<ShieldCheck size={26} />}
        title="Phiên xác minh không hợp lệ"
        message="Phiên xác minh Google không hợp lệ hoặc đã bị mất. Vui lòng đăng nhập Google lại."
        actionText="Quay lại đăng nhập"
        onAction={() => navigate("/login", { replace: true })}
      />
    );
  }

  const handleVerify = async (otp) => {
    setError("");
    setLoading(true);

    try {
      await googleAuthService.verifyOtp(userId, otp);

      // Backend vừa xác minh OTP và cấp cookie phiên nội bộ
      const user = await refreshUser();

      if (!user?.role) {
        throw new Error(
          "Xác minh thành công nhưng không lấy được thông tin tài khoản.",
        );
      }

      const fromPath = location.state?.from;

      const targetPath =
        user.role === "PASSENGER"
          ? fromPath
            ? `${fromPath.pathname}${fromPath.search || ""}`
            : "/"
          : getRedirectPathByRole(user.role);

      navigate(targetPath, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.message ||
          "Xác minh OTP thất bại. Vui lòng thử lại.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout
      icon={<ShieldCheck size={26} />}
      title="Xác minh đăng nhập Google"
      subtitle="Nhập mã OTP được gửi đến email của bạn"
    >
      <OtpVerifyForm
        email={email}
        error={error}
        loading={loading}
        onSubmit={handleVerify}
        onBack={() => navigate("/login", { replace: true })}
        onClearError={() => setError("")}
      />
    </AuthLayout>
  );
}
