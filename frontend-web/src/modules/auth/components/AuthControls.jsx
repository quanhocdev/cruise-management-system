// src/modules/auth/components/AuthControls.jsx
// (FILE MỚI) Các control dùng chung: thông báo, ô nhập, nút gửi, thẻ "phiên hết hạn".

import { AlertCircle, CheckCircle2, Info, X } from "lucide-react";

import AuthLayout from "./AuthLayout";

export function AuthAlert({ type = "error", onClose, children }) {
  const Icon =
    type === "success" ? CheckCircle2 : type === "warning" ? Info : AlertCircle;

  return (
    <div
      className={`auth-alert auth-alert--${type}`}
      role={type === "error" ? "alert" : "status"}
    >
      <Icon size={18} className="auth-alert-icon" />
      <div className="auth-alert-text">{children}</div>

      {onClose && (
        <button
          type="button"
          className="auth-alert-close"
          onClick={onClose}
          aria-label="Đóng thông báo"
        >
          <X size={16} />
        </button>
      )}
    </div>
  );
}

export function TextField({
  id,
  label,
  icon: Icon,
  hint,
  error,
  ...inputProps
}) {
  return (
    <div className="auth-field">
      <label htmlFor={id} className="auth-label">
        {label}
      </label>

      <div className="auth-control">
        {Icon && (
          <span className="auth-control-icon">
            <Icon size={18} />
          </span>
        )}

        <input
          id={id}
          className={`auth-input ${error ? "auth-input--error" : ""}`}
          aria-invalid={Boolean(error)}
          {...inputProps}
        />
      </div>

      {error ? (
        <span className="auth-field-error">{error}</span>
      ) : (
        hint && <span className="auth-hint">{hint}</span>
      )}
    </div>
  );
}

export function SubmitButton({
  loading = false,
  loadingText = "Đang xử lý...",
  variant = "primary",
  type = "submit",
  disabled = false,
  className = "",
  children,
  ...rest
}) {
  return (
    <button
      type={type}
      className={`auth-btn auth-btn--${variant} ${className}`}
      disabled={loading || disabled}
      {...rest}
    >
      {loading ? (
        <>
          <span className="auth-spinner" aria-hidden="true" />
          {loadingText}
        </>
      ) : (
        children
      )}
    </button>
  );
}

// Thẻ báo phiên không hợp lệ / đã hết hạn (dùng cho trang OTP, kích hoạt...)
export function SessionExpired({ icon, title, message, actionText, onAction }) {
  return (
    <AuthLayout icon={icon} title={title}>
      <AuthAlert type="warning">{message}</AuthAlert>

      <SubmitButton type="button" onClick={onAction}>
        {actionText}
      </SubmitButton>
    </AuthLayout>
  );
}
