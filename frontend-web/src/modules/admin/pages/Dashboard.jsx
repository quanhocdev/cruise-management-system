// src/modules/admin/pages/Dashboard.jsx

import React from "react";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
  const navigate = useNavigate();

  return (
    <div style={styles.page}>
      <h1 style={styles.title}>Trang Admin Dashboard</h1>

      <p style={styles.description}>
        Chào mừng Admin đã đăng nhập thành công vào hệ thống Cruise!
      </p>

      <div style={styles.actions}>
        <button
          onClick={() => navigate("/admin/accounts")}
          style={styles.button}
        >
          Quản lý tài khoản nhân viên
        </button>
      </div>
    </div>
  );
};

const styles = {
  page: {
    padding: "30px",
    fontFamily: "sans-serif",
  },

  title: {
    marginBottom: "10px",
  },

  description: {
    color: "#666",
    marginBottom: "25px",
  },

  actions: {
    marginTop: "20px",
  },

  button: {
    padding: "12px 18px",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#2563eb",
    color: "#fff",
    fontSize: "15px",
    fontWeight: "600",
    cursor: "pointer",
  },
};

export default AdminDashboard;
