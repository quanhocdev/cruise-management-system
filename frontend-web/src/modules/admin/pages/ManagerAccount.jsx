import { useMemo, useState } from "react";
import { Alert, Button, Form, InputGroup } from "react-bootstrap";

import useAccounts from "../hooks/useAccounts";

import AccountTable from "../components/account/AccountTable";
import AccountFormModal from "../components/account/AccountFormModal";
import AccountSummaryCards from "../components/account/AccountSummaryCards";

import "../styles/ManagerAccount.css";

const MANAGER_ROLES = [
  "SCHEDULER",
  "OPERATION",
  "ONBOARD",
  "SHORE",
  "CONVENIENCE",
  "FINANCE",
];

const STAFF_ROLES = ["ONBOARD_STAFF", "SHORE_STAFF", "CONVENIENCE_STAFF"];

function getRoleName(account) {
  const role = account.role?.name || account.roleName || account.role || "";

  return String(role).trim().toUpperCase();
}

function getAccountCategory(account) {
  const role = getRoleName(account);

  if (role === "PASSENGER" || role === "GUEST") {
    return "PASSENGER";
  }

  if (MANAGER_ROLES.includes(role)) {
    return "MANAGER";
  }

  if (STAFF_ROLES.includes(role)) {
    return "STAFF";
  }

  return "OTHER";
}

export default function ManagerAccount() {
  const {
    accounts,
    roles,
    loading,
    rolesLoading,
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

  const [activeCategory, setActiveCategory] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");

  const [form, setForm] = useState({
    username: "",
    email: "",
    roleId: "",
    status: "ACTIVE",
  });

  const counts = useMemo(() => {
    const result = {
      ALL: accounts.length,
      MANAGER: 0,
      STAFF: 0,
      PASSENGER: 0,
    };

    accounts.forEach((account) => {
      const category = getAccountCategory(account);

      if (category !== "OTHER") {
        result[category]++;
      }
    });

    return result;
  }, [accounts]);

  const filteredAccounts = useMemo(() => {
    const keyword = searchTerm.trim().toLowerCase();

    return accounts.filter((account) => {
      const category = getAccountCategory(account);

      const matchesCategory =
        activeCategory === "ALL" || category === activeCategory;

      const matchesSearch =
        !keyword ||
        (account.username || "").toLowerCase().includes(keyword) ||
        (account.email || "").toLowerCase().includes(keyword);

      return matchesCategory && matchesSearch;
    });
  }, [accounts, activeCategory, searchTerm]);

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

  const handleCloseModal = () => {
    if (saving) return;
    setShowModal(false);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

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

  const handleDeactivate = async (account) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn vô hiệu hóa tài khoản "${account.username}" không?`,
    );

    if (!confirmed) return;

    await deactivateAccount(account.id);
  };

  return (
    <div className="manager-account-page container-fluid py-4">
      <div className="manager-account-header mb-4">
        <div>
          <h2 className="manager-account-title">Quản lý tài khoản</h2>

          <p className="manager-account-description">
            Quản lý tài khoản quản lý, nhân viên và hành khách trong hệ thống
            Cruise.
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

      <AccountSummaryCards
        counts={counts}
        activeCategory={activeCategory}
        onCategoryChange={setActiveCategory}
      />

      <div className="mb-3">
        <InputGroup>
          <InputGroup.Text>
            <i className="bi bi-search" />
          </InputGroup.Text>

          <Form.Control
            type="search"
            placeholder="Tìm theo tên tài khoản hoặc email..."
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
          />

          {searchTerm && (
            <Button
              variant="outline-secondary"
              onClick={() => setSearchTerm("")}
            >
              Xóa
            </Button>
          )}
        </InputGroup>

        <div className="d-flex justify-content-between align-items-center mt-2">
          <small className="text-muted">
            Đang hiển thị {filteredAccounts.length} /{" "}
            {counts[activeCategory] ?? counts.ALL} tài khoản
          </small>

          <small className="text-muted">
            {activeCategory === "ALL" && "Tất cả tài khoản"}
            {activeCategory === "MANAGER" && "Tài khoản quản lý"}
            {activeCategory === "STAFF" && "Tài khoản nhân viên"}
            {activeCategory === "PASSENGER" && "Tài khoản hành khách"}
          </small>
        </div>
      </div>

      <AccountTable
        accounts={filteredAccounts}
        loading={loading}
        onEdit={handleOpenEdit}
        onDeactivate={handleDeactivate}
        emptyMessage={
          searchTerm
            ? "Không tìm thấy tài khoản phù hợp với từ khóa."
            : "Danh mục này chưa có tài khoản nào."
        }
      />

      <AccountFormModal
        show={showModal}
        saving={saving}
        editingAccount={editingAccount}
        form={form}
        roles={roles}
        rolesLoading={rolesLoading}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
