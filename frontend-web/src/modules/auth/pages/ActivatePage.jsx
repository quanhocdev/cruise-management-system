import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import api from "../../../api/axios";

export default function ActivatePage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const token = searchParams.get("token");

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [valid, setValid] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    verifyToken();
  }, []);

  const verifyToken = async () => {
    if (!token) {
      setError("Liên kết kích hoạt không hợp lệ.");
      setLoading(false);
      return;
    }

    try {
      const res = await api.post("/auth/activate/verify", {
        token,
      });

      setUsername(res.data.username);
      setValid(true);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Liên kết kích hoạt không hợp lệ hoặc đã hết hạn.",
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (password !== confirmPassword) {
      setError("Mật khẩu xác nhận không khớp.");
      return;
    }

    setSubmitting(true);

    try {
      const res = await api.post("/auth/activate/set-password", {
        token,
        password,
        confirmPassword,
      });

      setSuccess(res.data?.message || "Kích hoạt tài khoản thành công.");

      setTimeout(() => {
        navigate("/login");
      }, 1500);
    } catch (err) {
      setError(err.response?.data?.message || "Không thể kích hoạt tài khoản.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div style={styles.center}>
        <p>Đang kiểm tra liên kết kích hoạt...</p>
      </div>
    );
  }

  if (!valid) {
    return (
      <div style={styles.center}>
        <div style={styles.errorCard}>
          <h2>Liên kết không hợp lệ</h2>

          <p>{error}</p>

          <button style={styles.button} onClick={() => navigate("/login")}>
            Đến trang đăng nhập
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.title}>Kích hoạt tài khoản</h1>

        <p style={styles.subtitle}>
          Tài khoản: <strong>{username}</strong>
        </p>

        <p style={styles.description}>
          Vui lòng tạo mật khẩu mới để hoàn tất kích hoạt tài khoản.
        </p>

        {error && <div style={styles.error}>{error}</div>}

        {success && <div style={styles.success}>{success}</div>}

        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label style={styles.label}>Mật khẩu mới</label>

            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Nhập mật khẩu mới"
              minLength={8}
              maxLength={100}
              required
              style={styles.input}
            />
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Xác nhận mật khẩu</label>

            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Nhập lại mật khẩu"
              minLength={8}
              maxLength={100}
              required
              style={styles.input}
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            style={{
              ...styles.button,
              opacity: submitting ? 0.6 : 1,
            }}
          >
            {submitting ? "Đang kích hoạt..." : "Kích hoạt tài khoản"}
          </button>
        </form>
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#f5f6f8",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "30px",
  },

  card: {
    width: "100%",
    maxWidth: "500px",
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    padding: "30px",
    boxShadow: "0 2px 12px rgba(0, 0, 0, 0.08)",
  },

  errorCard: {
    width: "100%",
    maxWidth: "500px",
    boxSizing: "border-box",
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    padding: "30px",
    boxShadow: "0 2px 12px rgba(0, 0, 0, 0.08)",
    textAlign: "center",
  },

  center: {
    minHeight: "100vh",
    backgroundColor: "#f5f6f8",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "30px",
  },

  title: {
    margin: 0,
    fontSize: "26px",
    color: "#1f2937",
  },

  subtitle: {
    marginTop: "12px",
    color: "#374151",
  },

  description: {
    color: "#6b7280",
    lineHeight: "1.5",
    marginBottom: "24px",
  },

  formGroup: {
    marginBottom: "20px",
  },

  label: {
    display: "block",
    marginBottom: "8px",
    fontSize: "14px",
    fontWeight: "500",
    color: "#374151",
  },

  input: {
    width: "100%",
    boxSizing: "border-box",
    padding: "12px 14px",
    border: "1px solid #d1d5db",
    borderRadius: "8px",
    fontSize: "15px",
  },

  button: {
    width: "100%",
    padding: "12px",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: "600",
    cursor: "pointer",
  },

  error: {
    marginBottom: "20px",
    padding: "12px",
    borderRadius: "8px",
    backgroundColor: "#fef2f2",
    color: "#dc2626",
  },

  success: {
    marginBottom: "20px",
    padding: "12px",
    borderRadius: "8px",
    backgroundColor: "#ecfdf5",
    color: "#047857",
  },
};
