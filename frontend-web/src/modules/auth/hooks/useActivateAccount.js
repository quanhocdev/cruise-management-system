// src/modules/auth/hooks/useActivateAccount.js

import { useCallback, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

import authService from "../services/authService";

const MIN_PASSWORD_LENGTH = 8;

const useActivateAccount = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get("token");

  const [username, setUsername] = useState("");
  const [checking, setChecking] = useState(true);
  const [valid, setValid] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Kiểm tra token khi vào trang
  useEffect(() => {
    let active = true;

    const verify = async () => {
      if (!token) {
        setError("Liên kết kích hoạt không hợp lệ.");
        setChecking(false);
        return;
      }

      try {
        const data = await authService.verifyActivationToken(token);
        if (!active) return;

        setUsername(data.username);
        setValid(true);
      } catch (err) {
        if (!active) return;

        setError(
          err.response?.data?.message ||
            "Liên kết kích hoạt không hợp lệ hoặc đã hết hạn.",
        );
      } finally {
        if (active) setChecking(false);
      }
    };

    verify();

    return () => {
      active = false;
    };
  }, [token]);

  // Kích hoạt xong thì tự chuyển sang đăng nhập
  useEffect(() => {
    if (!success) return;

    const timer = setTimeout(() => navigate("/login", { replace: true }), 1500);
    return () => clearTimeout(timer);
  }, [success, navigate]);

  const submit = useCallback(
    async (password, confirmPassword) => {
      setError("");
      setSuccess("");

      if (password.length < MIN_PASSWORD_LENGTH) {
        setError(`Mật khẩu phải có ít nhất ${MIN_PASSWORD_LENGTH} ký tự.`);
        return;
      }

      if (password !== confirmPassword) {
        setError("Mật khẩu xác nhận không khớp.");
        return;
      }

      setSubmitting(true);

      try {
        const data = await authService.setActivationPassword({
          token,
          password,
          confirmPassword,
        });

        setSuccess(data?.message || "Kích hoạt tài khoản thành công.");
      } catch (err) {
        setError(
          err.response?.data?.message || "Không thể kích hoạt tài khoản.",
        );
      } finally {
        setSubmitting(false);
      }
    },
    [token],
  );

  return {
    username,
    checking,
    valid,
    submitting,
    error,
    success,
    clearError: () => setError(""),
    submit,
    goToLogin: () => navigate("/login"),
  };
};

export default useActivateAccount;
