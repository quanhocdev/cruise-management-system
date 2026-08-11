import { useState } from "react";
import api from "../../../api/axios";

export default function ManagerAccount() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    roleId: "", // Đổi tên state từ role thành roleId
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");
    setLoading(true);

    try {
      const res = await api.post("/admin/staff", {
        username: form.username,
        email: form.email,
        roleId: Number(form.roleId), // Gửi đúng key 'roleId' dạng Number
      });

      setMessage(
        res.data?.message ||
          "Tạo tài khoản nhân viên thành công. Email kích hoạt đã được gửi.",
      );

      setForm({
        username: "",
        email: "",
        roleId: "",
      });
    } catch (err) {
      const message =
        err.response?.data?.message || "Không thể tạo tài khoản nhân viên.";

      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <div style={styles.header}>
          <h1 style={styles.title}>Quản lý tài khoản</h1>
          <p style={styles.subtitle}>Tạo tài khoản cho nhân viên</p>
        </div>

        <div style={styles.card}>
          {message && <div style={styles.success}>{message}</div>}
          {error && <div style={styles.error}>{error}</div>}

          <form onSubmit={handleSubmit}>
            {/* Username */}
            <div style={styles.formGroup}>
              <label style={styles.label}>Tài khoản</label>
              <input
                type="text"
                name="username"
                value={form.username}
                onChange={handleChange}
                placeholder="Nhập tên tài khoản"
                required
                minLength={3}
                maxLength={50}
                style={styles.input}
              />
            </div>

            {/* Email */}
            <div style={styles.formGroup}>
              <label style={styles.label}>Email</label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="Nhập email nhân viên"
                required
                style={styles.input}
              />
            </div>

            {/* Role ID */}
            <div style={styles.formGroup}>
              <label style={styles.label}>Vai trò</label>
              <select
                name="roleId"
                value={form.roleId}
                onChange={handleChange}
                required
                style={styles.input}
              >
                <option value="">-- Chọn vai trò --</option>
                {/* Lưu ý: Thay đổi các value=ID này cho khớp với bảng roles trong DB của bạn */}
                <option value="2">Scheduler</option>
                <option value="3">Shore</option>
                <option value="4">Onboard</option>
                <option value="5">Convenience</option>
                <option value="6">Finance</option>
                <option value="7">Operation</option>
              </select>
            </div>

            <div style={styles.note}>
              Nhân viên sẽ nhận email kích hoạt và tự thiết lập mật khẩu. Admin
              không cần tạo mật khẩu cho nhân viên.
            </div>

            <button
              type="submit"
              disabled={loading}
              style={{
                ...styles.button,
                opacity: loading ? 0.6 : 1,
                cursor: loading ? "not-allowed" : "pointer",
              }}
            >
              {loading ? "Đang tạo..." : "Tạo tài khoản"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#f5f6f8",
    padding: "40px",
  },
  container: {
    maxWidth: "700px",
    margin: "0 auto",
  },
  header: {
    marginBottom: "24px",
  },
  title: {
    margin: 0,
    fontSize: "28px",
    fontWeight: "600",
    color: "#1f2937",
  },
  subtitle: {
    marginTop: "8px",
    marginBottom: 0,
    color: "#6b7280",
    fontSize: "15px",
  },
  card: {
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    padding: "30px",
    boxShadow: "0 2px 10px rgba(0, 0, 0, 0.06)",
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
    outline: "none",
    backgroundColor: "#ffffff",
  },
  note: {
    marginBottom: "20px",
    padding: "12px 14px",
    backgroundColor: "#eff6ff",
    borderRadius: "8px",
    color: "#1d4ed8",
    fontSize: "14px",
    lineHeight: "1.5",
  },
  success: {
    marginBottom: "20px",
    padding: "12px 14px",
    backgroundColor: "#ecfdf5",
    borderRadius: "8px",
    color: "#047857",
    fontSize: "14px",
  },
  error: {
    marginBottom: "20px",
    padding: "12px 14px",
    backgroundColor: "#fef2f2",
    borderRadius: "8px",
    color: "#dc2626",
    fontSize: "14px",
  },
  button: {
    width: "100%",
    marginTop: "8px",
    padding: "12px",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: "600",
  },
};
