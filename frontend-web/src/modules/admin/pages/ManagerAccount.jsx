import { useState } from "react";
import { Alert, Button } from "react-bootstrap";

import useAccounts from "../hooks/useAccounts";

import AccountTable from "../components/account/AccountTable";
import AccountFormModal from "../components/account/AccountFormModal";

import "../styles/ManagerAccount.css";

export default function ManagerAccount() {
  const {
    accounts,
    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    createAccount,
    updateAccount,
    deactivateAccount,
  } = useAccounts();

  const [showModal, setShowModal] = useState(false);
  const [editingAccount, setEditingAccount] = useState(null);

  const [form, setForm] = useState({
    username: "",
    email: "",
    roleId: "",
    status: "ACTIVE",
  });

  /*
   * =====================================================
   * OPEN CREATE
   * =====================================================
   */
  const handleOpenCreate = () => {
    setEditingAccount(null);

    setForm({
      username: "",
      email: "",
      roleId: "",
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * OPEN EDIT
   * =====================================================
   */
  const handleOpenEdit = (account) => {
    setEditingAccount(account);

    const roleId = account.role?.id || account.roleId || "";

    setForm({
      username: account.username || "",
      email: account.email || "",
      roleId: String(roleId),
      status: account.status || account.accountStatus || "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * CLOSE
   * =====================================================
   */
  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
  };

  /*
   * =====================================================
   * INPUT
   * =====================================================
   */
  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /*
   * =====================================================
   * SUBMIT
   * =====================================================
   */
  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!form.username.trim()) {
      setError("Vui lòng nhập tên tài khoản.");
      return;
    }

    if (!form.email.trim()) {
      setError("Vui lòng nhập email.");
      return;
    }

    if (!form.roleId) {
      setError("Vui lòng chọn vai trò.");
      return;
    }

    let successResult;

    if (!editingAccount) {
      successResult = await createAccount({
        username: form.username.trim(),
        email: form.email.trim(),
        roleId: Number(form.roleId),
      });
    } else {
      successResult = await updateAccount(editingAccount.id, {
        username: form.username.trim(),
        email: form.email.trim(),
        roleId: Number(form.roleId),
        status: form.status,
      });
    }

    if (successResult) {
      setShowModal(false);
    }
  };

  /*
   * =====================================================
   * DEACTIVATE
   * =====================================================
   */
  const handleDeactivate = async (account) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn vô hiệu hóa tài khoản "${account.username}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deactivateAccount(account.id);
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */
  return (
    <div className="manager-account-page container-fluid py-4">
      <div className="manager-account-header mb-4">
        <div>
          <h2 className="manager-account-title">Quản lý tài khoản</h2>

          <p className="manager-account-description">
            Quản lý tài khoản nhân viên trong hệ thống Cruise.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo tài khoản
        </Button>
      </div>

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <AccountTable
        accounts={accounts}
        loading={loading}
        onEdit={handleOpenEdit}
        onDeactivate={handleDeactivate}
      />

      <AccountFormModal
        show={showModal}
        saving={saving}
        editingAccount={editingAccount}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
