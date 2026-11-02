// src/modules/auth/pages/RegisterPage.jsx

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Mail, User, UserPlus } from "lucide-react";

import authService from "../services/authService";

import AuthLayout from "../components/AuthLayout";
import PasswordInput from "../components/PasswordInput";
import { AuthAlert, SubmitButton, TextField } from "../components/AuthControls";

const MIN_PASSWORD_LENGTH = 8;

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({ ...previous, [name]: value }));
  };

  const confirmMismatch =
    form.confirmPassword !== "" && form.password !== form.confirmPassword;

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (form.password.length < MIN_PASSWORD_LENGTH) {
      setError(`Mật khẩu phải có ít nhất ${MIN_PASSWORD_LENGTH} ký tự.`);
      return;
    }

    if (form.password !== form.confirmPassword) {
      setError("Mật khẩu xác nhận không khớp.");
      return;
    }

    setLoading(true);

    try {
      // Backend chỉ nhận username / email / password
      const data = await authService.register({
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
      });

      navigate("/verify-email", {
        state: { userId: data.id, email: data.email },
      });
    } catch (err) {
      setError(err.response?.data?.message || "Đăng ký thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout
      icon={<UserPlus size={26} />}
      title="Tạo tài khoản"
      subtitle="Đăng ký để đặt tour du thuyền của bạn"
      footer={
        <>
          Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
        </>
      }
    >
      {error && (
        <AuthAlert type="error" onClose={() => setError("")}>
          {error}
        </AuthAlert>
      )}

      <form className="auth-form" onSubmit={handleSubmit}>
        <TextField
          id="register-username"
          label="Tên đăng nhập"
          icon={User}
          name="username"
          placeholder="Ví dụ: nguyenvana"
          value={form.username}
          onChange={handleChange}
          autoComplete="username"
          required
          disabled={loading}
        />

        <TextField
          id="register-email"
          label="Email"
          icon={Mail}
          type="email"
          name="email"
          placeholder="ban@example.com"
          hint="Mã OTP xác thực sẽ được gửi tới email này."
          value={form.email}
          onChange={handleChange}
          autoComplete="email"
          required
          disabled={loading}
        />

        <PasswordInput
          id="register-password"
          label="Mật khẩu"
          name="password"
          placeholder="Tạo mật khẩu"
          hint={`Tối thiểu ${MIN_PASSWORD_LENGTH} ký tự.`}
          value={form.password}
          onChange={handleChange}
          autoComplete="new-password"
          minLength={MIN_PASSWORD_LENGTH}
          required
          disabled={loading}
        />

        <PasswordInput
          id="register-confirm"
          label="Xác nhận mật khẩu"
          name="confirmPassword"
          placeholder="Nhập lại mật khẩu"
          error={confirmMismatch ? "Mật khẩu xác nhận không khớp." : ""}
          value={form.confirmPassword}
          onChange={handleChange}
          autoComplete="new-password"
          required
          disabled={loading}
        />

        <SubmitButton loading={loading} loadingText="Đang tạo tài khoản...">
          Đăng ký
        </SubmitButton>
      </form>
    </AuthLayout>
  );
}
