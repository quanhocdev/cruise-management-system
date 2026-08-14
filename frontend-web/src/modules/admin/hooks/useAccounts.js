import { useEffect, useState } from "react";
import api from "../../../api/axios";

export default function useAccounts() {
  const [accounts, setAccounts] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadAccounts = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/admin/staff");

      const data = response.data;

      if (Array.isArray(data)) {
        setAccounts(data);
      } else if (Array.isArray(data?.data)) {
        setAccounts(data.data);
      } else if (Array.isArray(data?.content)) {
        setAccounts(data.content);
      } else {
        setAccounts([]);
      }
    } catch (err) {
      console.error("Load accounts error:", err);

      setError(
        err.response?.data?.message || "Không thể tải danh sách tài khoản.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  const createAccount = async (requestData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const response = await api.post("/admin/staff", requestData);

      setSuccess(
        response.data?.message ||
          "Tạo tài khoản nhân viên thành công. Email kích hoạt đã được gửi.",
      );

      await loadAccounts();

      return true;
    } catch (err) {
      console.error("Create account error:", err);

      setError(
        err.response?.data?.message || "Không thể tạo tài khoản nhân viên.",
      );

      return false;
    } finally {
      setSaving(false);
    }
  };

  const updateAccount = async (id, requestData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const response = await api.patch(`/admin/staff/${id}`, requestData);

      setSuccess(response.data?.message || "Cập nhật tài khoản thành công.");

      await loadAccounts();

      return true;
    } catch (err) {
      console.error("Update account error:", err);

      setError(err.response?.data?.message || "Không thể cập nhật tài khoản.");

      return false;
    } finally {
      setSaving(false);
    }
  };

  const deactivateAccount = async (id) => {
    setError("");
    setSuccess("");

    try {
      const response = await api.delete(`/admin/staff/${id}`);

      setSuccess(response.data?.message || "Đã vô hiệu hóa tài khoản.");

      await loadAccounts();

      return true;
    } catch (err) {
      console.error("Deactivate account error:", err);

      setError(
        err.response?.data?.message || "Không thể vô hiệu hóa tài khoản.",
      );

      return false;
    }
  };

  return {
    accounts,
    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadAccounts,
    createAccount,
    updateAccount,
    deactivateAccount,
  };
}
