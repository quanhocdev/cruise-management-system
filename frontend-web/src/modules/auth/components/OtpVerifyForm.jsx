// src/modules/auth/components/OtpVerifyForm.jsx

import { useState } from "react";
import { Mail } from "lucide-react";

import OtpInput from "./OtpInput";
import { AuthAlert, SubmitButton } from "./AuthControls";

const OTP_LENGTH = 6;

function OtpVerifyForm({
  email,
  error,
  success,
  loading,
  submitText = "Xác nhận OTP",
  backText = "Quay lại đăng nhập",
  onSubmit,
  onBack,
  onClearError,
}) {
  const [otp, setOtp] = useState("");
  const [localError, setLocalError] = useState("");

  const shownError = localError || error;

  const handleChange = (value) => {
    setOtp(value);
    setLocalError("");
    if (error) onClearError?.();
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (otp.length !== OTP_LENGTH) {
      setLocalError(`Vui lòng nhập OTP gồm đúng ${OTP_LENGTH} chữ số.`);
      return;
    }

    onSubmit(otp);
  };

  return (
    <form className="auth-form" onSubmit={handleSubmit} noValidate>
      {email && (
        <div className="auth-otp-meta">
          <span className="auth-email-chip">
            <Mail size={16} />
            {email}
          </span>
        </div>
      )}

      {shownError && (
        <AuthAlert
          type="error"
          onClose={() => {
            setLocalError("");
            onClearError?.();
          }}
        >
          {shownError}
        </AuthAlert>
      )}

      {success && <AuthAlert type="success">{success}</AuthAlert>}

      <OtpInput
        value={otp}
        onChange={handleChange}
        length={OTP_LENGTH}
        disabled={loading || Boolean(success)}
        hasError={Boolean(shownError)}
      />

      <SubmitButton
        loading={loading}
        loadingText="Đang xác minh..."
        disabled={otp.length !== OTP_LENGTH || Boolean(success)}
      >
        {submitText}
      </SubmitButton>

      <SubmitButton
        type="button"
        variant="link"
        disabled={loading}
        onClick={onBack}
      >
        {backText}
      </SubmitButton>
    </form>
  );
}

export default OtpVerifyForm;
