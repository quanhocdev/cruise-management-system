// src/modules/auth/components/PasswordInput.jsx

import { useState } from "react";
import { Eye, EyeOff, Lock } from "lucide-react";

function PasswordInput({ id, label, hint, error, ...inputProps }) {
  const [visible, setVisible] = useState(false);

  return (
    <div className="auth-field">
      <label htmlFor={id} className="auth-label">
        {label}
      </label>

      <div className="auth-control">
        <span className="auth-control-icon">
          <Lock size={18} />
        </span>

        <input
          id={id}
          type={visible ? "text" : "password"}
          className={`auth-input auth-input--password ${
            error ? "auth-input--error" : ""
          }`}
          aria-invalid={Boolean(error)}
          {...inputProps}
        />

        <button
          type="button"
          className="auth-toggle"
          onClick={() => setVisible((previous) => !previous)}
          aria-label={visible ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
          aria-pressed={visible}
        >
          {visible ? <EyeOff size={18} /> : <Eye size={18} />}
        </button>
      </div>

      {error ? (
        <span className="auth-field-error">{error}</span>
      ) : (
        hint && <span className="auth-hint">{hint}</span>
      )}
    </div>
  );
}

export default PasswordInput;
