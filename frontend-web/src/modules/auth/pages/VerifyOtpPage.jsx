// src/modules/auth/pages/VerifyOtpPage.jsx

import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { MailCheck } from "lucide-react";

import authService from "../services/authService";

import AuthLayout from "../components/AuthLayout";
import OtpVerifyForm from "../components/OtpVerifyForm";
import { SessionExpired } from "../components/AuthControls";

export default function VerifyOtpPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const { userId, email } = location.state || {};

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  // Xác thực xong thì tự chuyển sang đăng nhập
  useEffect(() => {
    if (!success) return;

    const timer = setTimeout(() => navigate("/login", { replace: true }), 1500);
    return () => clearTimeout(timer);
  }, [success, navigate]);

  if (!userId) {
    return (
      <SessionExpired
        icon={<MailCheck size={26} />}
        title="Phiên xác thực không hợp lệ"
        message="Không tìm thấy thông tin đăng ký. Vui lòng đăng ký lại để nhận mã OTP."
        actionText="Quay lại đăng ký"
        onAction={() => navigate("/register", { replace: true })}
      />
    );
  }

  const handleVerify = async (otp) => {
    setError("");
    setLoading(true);

    try {
      await authService.verifyEmail(userId, otp);
      setSuccess("Xác thực thành công, đang chuyển sang đăng nhập...");
    } catch (err) {
      setError(err.response?.data?.message || "OTP không đúng");
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout
      icon={<MailCheck size={26} />}
      title="Xác thực email"
      subtitle="Nhập mã OTP gồm 6 chữ số vừa được gửi tới email của bạn"
    >
      <OtpVerifyForm
        email={email}
        error={error}
        success={success}
        loading={loading}
        submitText="Xác nhận"
        onSubmit={handleVerify}
        onBack={() => navigate("/login", { replace: true })}
        onClearError={() => setError("")}
      />
    </AuthLayout>
  );
}
