// src/modules/auth/pages/ActivatePage.jsx

import { useState } from "react";
import { KeyRound, Link2Off, User } from "lucide-react";

import useActivateAccount from "../hooks/useActivateAccount";

import AuthLayout from "../components/AuthLayout";
import PasswordInput from "../components/PasswordInput";
import {
  AuthAlert,
  SessionExpired,
  SubmitButton,
  TextField,
} from "../components/AuthControls";

export default function ActivatePage() {
  const a = useActivateAccount();

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  if (a.checking) {
    return (
      <AuthLayout
        icon={<span className="auth-spinner auth-spinner--lg" />}
        title="Đang kiểm tra liên kết"
        subtitle="Vui lòng chờ trong giây lát..."
      />
    );
  }

  if (!a.valid) {
    return (
      <SessionExpired
        icon={<Link2Off size={26} />}
        title="Liên kết không hợp lệ"
        message={a.error}
        actionText="Đến trang đăng nhập"
        onAction={a.goToLogin}
      />
    );
  }

  const confirmMismatch =
    confirmPassword !== "" && password !== confirmPassword;

  const handleSubmit = (event) => {
    event.preventDefault();
    a.submit(password, confirmPassword);
  };

  const locked = a.submitting || Boolean(a.success);

  return (
    <AuthLayout
      icon={<KeyRound size={26} />}
      title="Kích hoạt tài khoản"
      subtitle="Tạo mật khẩu mới để hoàn tất kích hoạt tài khoản"
    >
      {a.error && (
        <AuthAlert type="error" onClose={a.clearError}>
          {a.error}
        </AuthAlert>
      )}

      {a.success && <AuthAlert type="success">{a.success}</AuthAlert>}

      <form className="auth-form" onSubmit={handleSubmit}>
        <TextField
          id="activate-username"
          label="Tài khoản"
          icon={User}
          value={a.username}
          readOnly
          disabled
        />

        <PasswordInput
          id="activate-password"
          label="Mật khẩu mới"
          placeholder="Nhập mật khẩu mới"
          hint="Tối thiểu 8 ký tự."
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="new-password"
          minLength={8}
          maxLength={100}
          required
          disabled={locked}
        />

        <PasswordInput
          id="activate-confirm"
          label="Xác nhận mật khẩu"
          placeholder="Nhập lại mật khẩu"
          error={confirmMismatch ? "Mật khẩu xác nhận không khớp." : ""}
          value={confirmPassword}
          onChange={(event) => setConfirmPassword(event.target.value)}
          autoComplete="new-password"
          minLength={8}
          maxLength={100}
          required
          disabled={locked}
        />

        <SubmitButton
          loading={a.submitting}
          loadingText="Đang kích hoạt..."
          disabled={Boolean(a.success)}
        >
          Kích hoạt tài khoản
        </SubmitButton>
      </form>
    </AuthLayout>
  );
}
