// src/modules/auth/pages/LoginPage.jsx

import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { Ship, User } from "lucide-react";

import { useAuth } from "../../../context/AuthContext";
import { getRedirectPathByRole } from "../../../routes/roleRoutes";
import useGoogleAuth from "../hooks/useGoogleAuth";

import AuthLayout from "../components/AuthLayout";
import PasswordInput from "../components/PasswordInput";
import { AuthAlert, SubmitButton, TextField } from "../components/AuthControls";

function GoogleIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 48 48" aria-hidden="true">
      <path
        fill="#EA4335"
        d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
      />
      <path
        fill="#4285F4"
        d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
      />
      <path
        fill="#FBBC05"
        d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
      />
      <path
        fill="#34A853"
        d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"
      />
    </svg>
  );
}

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const { loginWithGoogle, googleLoading, googleError } = useGoogleAuth();

  const getTargetPath = (role) => {
    const fromPath = location.state?.from;

    if (role === "PASSENGER" && fromPath) {
      return `${fromPath.pathname}${fromPath.search || ""}`;
    }

    return role === "PASSENGER" ? "/" : getRedirectPathByRole(role);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const role = await login(username, password);
      navigate(getTargetPath(role), { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "Đăng nhập thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    setError("");
    await loginWithGoogle();
  };

  const busy = loading || googleLoading;

  return (
    <AuthLayout
      icon={<Ship size={26} />}
      title="Chào mừng trở lại"
      subtitle="Đăng nhập để tiếp tục hành trình của bạn"
      footer={
        <>
          Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
        </>
      }
    >
      {error && (
        <AuthAlert type="error" onClose={() => setError("")}>
          {error}
        </AuthAlert>
      )}

      {googleError && <AuthAlert type="error">{googleError}</AuthAlert>}

      <form className="auth-form" onSubmit={handleSubmit}>
        <TextField
          id="login-username"
          label="Tài khoản"
          icon={User}
          type="text"
          placeholder="Nhập tên đăng nhập"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          autoComplete="username"
          required
          disabled={busy}
        />

        <PasswordInput
          id="login-password"
          label="Mật khẩu"
          placeholder="Nhập mật khẩu"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="current-password"
          required
          disabled={busy}
        />

        <SubmitButton loading={loading} disabled={googleLoading}>
          Đăng nhập
        </SubmitButton>
      </form>

      <div className="auth-divider">HOẶC</div>

      <SubmitButton
        type="button"
        variant="outline"
        loading={googleLoading}
        loadingText="Đang đăng nhập Google..."
        disabled={loading}
        onClick={handleGoogleLogin}
      >
        <GoogleIcon />
        Tiếp tục với Google
      </SubmitButton>
    </AuthLayout>
  );
}
