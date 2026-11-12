// src/modules/admin/pages/ManagerPolicy.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Form, Spinner } from "react-bootstrap";

import usePolicies from "../hooks/usePolicies";
import useBookingPolicies from "../hooks/useBookingPolicies";

import PolicyTable from "../components/policy/PolicyTable";
import PolicyFormModal from "../components/policy/PolicyFormModal";
import PolicyRuleModal from "../components/policy/PolicyRuleModal";

import "../styles/ManagerPolicy.css";

export default function ManagerPolicy() {
  const {
    policies,
    loading: policyLoading,
    saving: policySaving,

    error: policyError,
    success: policySuccess,

    setError: setPolicyError,
    setSuccess: setPolicySuccess,

    createPolicy,
    updatePolicy,
    deletePolicy,
    loadPolicies,
  } = usePolicies();

  const [typeFilter, setTypeFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const [showPolicyModal, setShowPolicyModal] = useState(false);
  const [editingPolicy, setEditingPolicy] = useState(null);

  const [showRuleModal, setShowRuleModal] = useState(false);
  const [selectedPolicy, setSelectedPolicy] = useState(null);

  const {
    bookingPolicies,

    loading: bookingLoading,
    saving: bookingSaving,

    error: bookingError,

    createBookingPolicy,
    updateBookingPolicy,
    deleteBookingPolicy,
  } = useBookingPolicies(
    selectedPolicy?.type === "BOOKING" ? selectedPolicy.id : null,
  );

  const ruleLoading = bookingLoading;
  const ruleSaving = bookingSaving;
  const ruleError = bookingError;

  // COMBINED SAVING
  const saving = policySaving || ruleSaving;

  const handleTypeFilterChange = (event) => {
    setTypeFilter(event.target.value);
  };

  const handleStatusFilterChange = (event) => {
    setStatusFilter(event.target.value);
  };

  useEffect(() => {
    loadPolicies({
      type: typeFilter || undefined,
      status: statusFilter || undefined,
    });
  }, [typeFilter, statusFilter, loadPolicies]);

  const handleOpenCreate = () => {
    setPolicyError("");
    setPolicySuccess("");

    setEditingPolicy(null);
    setShowPolicyModal(true);
  };

  const handleOpenEdit = (policy) => {
    setPolicyError("");
    setPolicySuccess("");

    setEditingPolicy(policy);
    setShowPolicyModal(true);
  };

  const handleClosePolicyModal = () => {
    if (policySaving) {
      return;
    }

    setShowPolicyModal(false);
    setEditingPolicy(null);

    setPolicyError("");
  };

  const handlePolicySubmit = async (data) => {
    let result;

    if (editingPolicy) {
      result = await updatePolicy(editingPolicy.id, data);
    } else {
      result = await createPolicy(data);
    }

    if (result) {
      setShowPolicyModal(false);
      setEditingPolicy(null);

      await loadPolicies({
        type: typeFilter || undefined,
        status: statusFilter || undefined,
      });
    }
  };

  const handleDelete = async (policy) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa chính sách "${policy.title}" không?\n\n` +
        "Chính sách sẽ được chuyển sang trạng thái ngừng hoạt động.",
    );

    if (!confirmed) {
      return;
    }

    const result = await deletePolicy(policy.id);

    if (result) {
      await loadPolicies({
        type: typeFilter || undefined,
        status: statusFilter || undefined,
      });
    }
  };

  const handleOpenRules = (policy) => {
    setPolicyError("");
    setPolicySuccess("");

    setSelectedPolicy(policy);
    setShowRuleModal(true);
  };

  const handleCloseRuleModal = () => {
    if (ruleSaving) {
      return;
    }

    setShowRuleModal(false);
    setSelectedPolicy(null);
  };

  const handleCreateBookingRule = async (policyId, data) => {
    return await createBookingPolicy(data);
  };

  const handleUpdateBookingRule = async (policyId, ruleId, data) => {
    return await updateBookingPolicy(ruleId, data);
  };

  const handleDeleteBookingRule = async (policyId, ruleId) => {
    return await deleteBookingPolicy(ruleId);
  };

  return (
    <div className="manager-policy-page container-fluid py-4">
      <div className="manager-policy-header">
        <div>
          <h2 className="manager-policy-title">Quản lý chính sách</h2>

          <p className="manager-policy-description">
            Quản lý chính sách đặt tour, hủy tour và các quy tắc áp dụng.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo chính sách
        </Button>
      </div>

      {policySuccess && (
        <Alert
          variant="success"
          dismissible
          onClose={() => setPolicySuccess("")}
        >
          {policySuccess}
        </Alert>
      )}

      {(policyError || ruleError) && !showPolicyModal && !showRuleModal && (
        <Alert
          variant="danger"
          dismissible
          onClose={() => {
            setPolicyError("");
          }}
        >
          {typeof policyError === "string"
            ? policyError
            : policyError?.message || ruleError || "Có lỗi xảy ra."}
        </Alert>
      )}

      <div className="manager-policy-filter-card">
        <div className="manager-policy-filter-header">
          <div>
            <h5>Bộ lọc chính sách</h5>

            <span>Lọc danh sách theo loại và trạng thái</span>
          </div>
        </div>

        <div className="manager-policy-filter-body">
          {/* TYPE */}

          <Form.Group className="manager-policy-filter-item">
            <Form.Label>Loại chính sách</Form.Label>

            <Form.Select value={typeFilter} onChange={handleTypeFilterChange}>
              <option value="">Tất cả loại</option>

              <option value="BOOKING">Chính sách đặt tour</option>

              <option value="CANCEL">Chính sách hủy tour</option>
            </Form.Select>
          </Form.Group>

          {/* STATUS */}

          <Form.Group className="manager-policy-filter-item">
            <Form.Label>Trạng thái</Form.Label>

            <Form.Select
              value={statusFilter}
              onChange={handleStatusFilterChange}
            >
              <option value="">Tất cả trạng thái</option>

              <option value="ACTIVE">Đang hoạt động</option>

              <option value="INACTIVE">Ngừng hoạt động</option>
            </Form.Select>
          </Form.Group>
        </div>
      </div>

      {/* =================================================
          FILTER LOADING
         ================================================= */}

      {policyLoading && (
        <div className="manager-policy-loading">
          <Spinner animation="border" size="sm" />

          <span>Đang cập nhật danh sách...</span>
        </div>
      )}

      {/* =================================================
          TABLE
         ================================================= */}

      <PolicyTable
        policies={policies}
        loading={false}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
        onManageRules={handleOpenRules}
      />

      <PolicyFormModal
        show={showPolicyModal}
        saving={policySaving}
        editingPolicy={editingPolicy}
        error={policyError}
        onClose={handleClosePolicyModal}
        onSubmit={handlePolicySubmit}
      />

      <PolicyRuleModal
        show={showRuleModal}
        policy={selectedPolicy}
        loading={ruleLoading}
        saving={ruleSaving}
        error={ruleError}
        bookingRules={bookingPolicies}
        onClose={handleCloseRuleModal}
        onCreateBookingRule={handleCreateBookingRule}
        onUpdateBookingRule={handleUpdateBookingRule}
        onDeleteBookingRule={handleDeleteBookingRule}
      />
    </div>
  );
}
