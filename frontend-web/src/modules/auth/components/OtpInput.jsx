// src/modules/auth/components/OtpInput.jsx

import { useRef } from "react";

function OtpInput({
  value,
  onChange,
  length = 6,
  disabled = false,
  hasError = false,
  autoFocus = true,
}) {
  const refs = useRef([]);
  const digits = Array.from({ length }, (_, index) => value[index] || "");

  const focusAt = (index) => {
    const target = Math.max(0, Math.min(length - 1, index));
    refs.current[target]?.focus();
  };

  const update = (index, chars, removeCount = 0) => {
    const next = value.split("");
    next.splice(index, removeCount, ...chars.split(""));
    onChange(next.join("").slice(0, length));
  };

  const handleChange = (index, event) => {
    let chars = event.target.value.replace(/\D/g, "");

    // Ô đã có số mà gõ thêm 1 số nữa (con trỏ không chọn sẵn): chỉ lấy số mới gõ
    if (digits[index] && chars.length === 2) {
      chars = chars.replace(digits[index], "");
    }

    // Xóa ký tự trong ô
    if (!chars) {
      update(index, "", 1);
      return;
    }

    // Gõ 1 số, hoặc trình duyệt tự điền cả mã vào 1 ô
    update(index, chars, chars.length > 1 ? length : 1);
    focusAt(index + chars.length);
  };

  const handleKeyDown = (index, event) => {
    if (event.key === "Backspace" && !digits[index]) {
      event.preventDefault();
      focusAt(index - 1);
    } else if (event.key === "ArrowLeft") {
      event.preventDefault();
      focusAt(index - 1);
    } else if (event.key === "ArrowRight") {
      event.preventDefault();
      focusAt(index + 1);
    }
  };

  const handlePaste = (index, event) => {
    event.preventDefault();

    const pasted = event.clipboardData.getData("text").replace(/\D/g, "");
    if (!pasted) return;

    update(index, pasted, length);
    focusAt(index + pasted.length);
  };

  return (
    <div className="auth-otp" role="group" aria-label="Mã OTP">
      {digits.map((digit, index) => (
        <input
          key={index}
          ref={(element) => {
            refs.current[index] = element;
          }}
          className={`auth-otp-box ${digit ? "auth-otp-box--filled" : ""} ${
            hasError ? "auth-otp-box--error" : ""
          }`}
          type="text"
          inputMode="numeric"
          autoComplete={index === 0 ? "one-time-code" : "off"}
          autoFocus={autoFocus && index === 0}
          aria-label={`Chữ số ${index + 1}`}
          value={digit}
          disabled={disabled}
          onChange={(event) => handleChange(index, event)}
          onKeyDown={(event) => handleKeyDown(index, event)}
          onPaste={(event) => handlePaste(index, event)}
          onFocus={(event) => {
            // Không cho nhảy vào ô trống nằm sau ô đang chờ nhập
            if (index > value.length) focusAt(value.length);
            else event.target.select();
          }}
        />
      ))}
    </div>
  );
}

export default OtpInput;
