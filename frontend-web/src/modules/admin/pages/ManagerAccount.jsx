import { useMemo, useState } from "react";
import { Alert, Button, Form, InputGroup } from "react-bootstrap";

import useAccounts from "../hooks/useAccounts";

import AccountTable from "../components/account/AccountTable";
import AccountFormModal from "../components/account/AccountFormModal";
import AccountSummaryCards from "../components/account/AccountSummaryCards";

import "../styles/ManagerAccount.css";

const MANAGEMENT_ROLES = [
  "SCHEDULER",
  "OPERATION",
  "ONBOARD",
  "SHORE",
  "CONVENIENCE",
  "FINANCE",
];

const STAFF_ROLES = ["ONBOARD_STAFF", "SHORE_STAFF", "CONVENIENCE_STAFF"];

function getRoleName(account) {
  return String(account.role?.name || account.roleName || account.role || "")
    .trim()
    .toUpperCase();
}

function getAccountCategory(account) {
  const role = getRoleName(account);

  if (role === "ADMIN") {
    return "ADMIN";
  }

  if (role === "PASSENGER") {
    return "PASSENGER";
  }

  if (MANAGEMENT_ROLES.includes(role)) {
    return "MANAGEMENT";
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

  // Thống kê dựa trên toàn bộ danh sách tài khoản
  const counts = useMemo(() => {
    const result = {
      ALL: accounts.length,
      ADMIN: 0,
      MANAGEMENT: 0,
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

  // Lọc theo nhóm và từ khóa
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

    setForm({
      username: account.username || "",
      email: account.email || "",
      roleId: "",
      status: account.status || "ACTIVE",
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

    let successResult;

    if (!editingAccount) {
      if (!form.roleId) {
        setError("Vui lòng chọn vai trò.");
        return;
      }

      successResult = await createAccount({
        username: form.username.trim(),
        email: form.email.trim(),
        roleId: Number(form.roleId),
      });
    } else {
      // Không gửi roleId khi cập nhật tài khoản
      successResult = await updateAccount(editingAccount.id, {
        username: form.username.trim(),
        email: form.email.trim(),
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
          <i className="bi bi-plus-lg me-2" />
          Tạo tài khoản
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

      <div className="manager-account-search">
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
            {activeCategory === "ADMIN" && "Tài khoản quản trị"}
            {activeCategory === "MANAGEMENT" && "Tài khoản quản lý"}
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
