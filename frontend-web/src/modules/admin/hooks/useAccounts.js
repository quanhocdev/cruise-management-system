import { useCallback, useEffect, useState } from "react";

import accountService from "../services/accountService";
import roleService from "../services/roleService";

export default function useAccounts() {
  const [accounts, setAccounts] = useState([]);
  const [roles, setRoles] = useState([]);

  const [loading, setLoading] = useState(true);
  const [rolesLoading, setRolesLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadAccounts = useCallback(async () => {
    setLoading(true);

    try {
      const response = await accountService.getAll();
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
  }, []);

  const loadRoles = useCallback(async () => {
    setRolesLoading(true);

    try {
      const response = await roleService.getAll();
      const data = response.data;

      const roleList = Array.isArray(data)
        ? data
        : Array.isArray(data?.data)
          ? data.data
          : [];

      // Chỉ hiển thị những role có thể được cấp qua màn hình nhân viên.
      const assignableRoles = roleList.filter(
        (role) =>
          !["ADMIN", "PASSENGER", "GUEST"].includes(role.name?.toUpperCase()),
      );

      setRoles(assignableRoles);
    } catch (err) {
      console.error("Load roles error:", err);
      setRoles([]);
      setError(
        err.response?.data?.message || "Không thể tải danh sách vai trò.",
      );
    } finally {
      setRolesLoading(false);
    }
  }, []);

  useEffect(() => {
    loadAccounts();
    loadRoles();
  }, [loadAccounts, loadRoles]);

  const createAccount = async (requestData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const response = await accountService.create(requestData);

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
      const response = await accountService.update(id, requestData);

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
      const response = await accountService.deactivate(id);

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
    roles,
    loading,
    rolesLoading,
    saving,
    error,
    success,
    setError,
    setSuccess,
    loadAccounts,
    loadRoles,
    createAccount,
    updateAccount,
    deactivateAccount,
  };
}
