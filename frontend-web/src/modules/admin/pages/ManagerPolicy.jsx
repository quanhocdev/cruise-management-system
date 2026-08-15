// src/modules/admin/pages/ManagerPolicy.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Form, Spinner } from "react-bootstrap";

import usePolicies from "../hooks/usePolicies";
import useBookingPolicies from "../hooks/useBookingPolicies";
import useCancelPolicies from "../hooks/useCancelPolicies";

import PolicyTable from "../components/policy/PolicyTable";
import PolicyFormModal from "../components/policy/PolicyFormModal";
import PolicyRuleModal from "../components/policy/PolicyRuleModal";

import "../styles/ManagerPolicy.css";

export default function ManagerPolicy() {
  // =====================================================
  // POLICY HOOK
  // =====================================================

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

  // =====================================================
  // FILTER
  // =====================================================

  const [typeFilter, setTypeFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  // =====================================================
  // POLICY MODAL
  // =====================================================

  const [showPolicyModal, setShowPolicyModal] = useState(false);
  const [editingPolicy, setEditingPolicy] = useState(null);

  // =====================================================
  // RULE MODAL
  // =====================================================

  const [showRuleModal, setShowRuleModal] = useState(false);
  const [selectedPolicy, setSelectedPolicy] = useState(null);

  // =====================================================
  // BOOKING RULE HOOK
  // =====================================================

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

  // =====================================================
  // CANCEL RULE HOOK
  // =====================================================

  const {
    cancelPolicies,

    loading: cancelLoading,
    saving: cancelSaving,

    error: cancelError,

    createCancelPolicy,
    updateCancelPolicy,
    deleteCancelPolicy,
  } = useCancelPolicies(
    selectedPolicy?.type === "CANCEL" ? selectedPolicy.id : null,
  );

  // =====================================================
  // RULE LOADING / SAVING
  // =====================================================

  const ruleLoading =
    selectedPolicy?.type === "BOOKING" ? bookingLoading : cancelLoading;

  const ruleSaving =
    selectedPolicy?.type === "BOOKING" ? bookingSaving : cancelSaving;

  const ruleError =
    selectedPolicy?.type === "BOOKING" ? bookingError : cancelError;

  // =====================================================
  // COMBINED SAVING
  // =====================================================

  const saving = policySaving || ruleSaving;

  // =====================================================
  // FILTER
  // =====================================================

  const handleTypeFilterChange = (event) => {
    setTypeFilter(event.target.value);
  };

  const handleStatusFilterChange = (event) => {
    setStatusFilter(event.target.value);
  };

  /*
   * Filter được xử lý bởi BACKEND.
   *
   * Khi người dùng đổi filter:
   * - UI đổi ngay lập tức
   * - useEffect gọi API
   * - Không cần await trong event handler
   */

  useEffect(() => {
    loadPolicies({
      type: typeFilter || undefined,
      status: statusFilter || undefined,
    });
  }, [typeFilter, statusFilter, loadPolicies]);

  // =====================================================
  // CREATE POLICY
  // =====================================================

  const handleOpenCreate = () => {
    setPolicyError("");
    setPolicySuccess("");

    setEditingPolicy(null);
    setShowPolicyModal(true);
  };

  // =====================================================
  // EDIT POLICY
  // =====================================================

  const handleOpenEdit = (policy) => {
    setPolicyError("");
    setPolicySuccess("");

    setEditingPolicy(policy);
    setShowPolicyModal(true);
  };

  // =====================================================
  // CLOSE POLICY MODAL
  // =====================================================

  const handleClosePolicyModal = () => {
    if (policySaving) {
      return;
    }

    setShowPolicyModal(false);
    setEditingPolicy(null);

    setPolicyError("");
  };

  // =====================================================
  // CREATE / UPDATE POLICY
  // =====================================================

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

      /*
       * Không cần reload nếu usePolicies đã cập nhật state.
       *
       * Tuy nhiên sau CREATE/UPDATE có thể filter hiện tại
       * không còn phù hợp.
       *
       * Reload lại theo filter hiện tại để backend quyết định.
       */
      await loadPolicies({
        type: typeFilter || undefined,
        status: statusFilter || undefined,
      });
    }
  };

  // =====================================================
  // DELETE POLICY
  // =====================================================

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

  // =====================================================
  // OPEN RULE MODAL
  // =====================================================

  const handleOpenRules = (policy) => {
    setPolicyError("");
    setPolicySuccess("");

    setSelectedPolicy(policy);
    setShowRuleModal(true);
  };

  // =====================================================
  // CLOSE RULE MODAL
  // =====================================================

  const handleCloseRuleModal = () => {
    if (ruleSaving) {
      return;
    }

    setShowRuleModal(false);
    setSelectedPolicy(null);
  };

  // =====================================================
  // CREATE BOOKING RULE
  // =====================================================

  const handleCreateBookingRule = async (policyId, data) => {
    return await createBookingPolicy(data);
  };

  // =====================================================
  // UPDATE BOOKING RULE
  // =====================================================

  const handleUpdateBookingRule = async (policyId, ruleId, data) => {
    return await updateBookingPolicy(ruleId, data);
  };

  // =====================================================
  // DELETE BOOKING RULE
  // =====================================================

  const handleDeleteBookingRule = async (policyId, ruleId) => {
    return await deleteBookingPolicy(ruleId);
  };

  // =====================================================
  // CREATE CANCEL RULE
  // =====================================================

  const handleCreateCancelRule = async (policyId, data) => {
    return await createCancelPolicy(data);
  };

  // =====================================================
  // UPDATE CANCEL RULE
  // =====================================================

  const handleUpdateCancelRule = async (policyId, ruleId, data) => {
    return await updateCancelPolicy(ruleId, data);
  };

  // =====================================================
  // DELETE CANCEL RULE
  // =====================================================

  const handleDeleteCancelRule = async (policyId, ruleId) => {
    return await deleteCancelPolicy(ruleId);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="manager-policy-page container-fluid py-4">
      {/* =================================================
          HEADER
         ================================================= */}

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

      {/* =================================================
          SUCCESS
         ================================================= */}

      {policySuccess && (
        <Alert
          variant="success"
          dismissible
          onClose={() => setPolicySuccess("")}
        >
          {policySuccess}
        </Alert>
      )}

      {/* =================================================
          ERROR
         ================================================= */}

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

      {/* =================================================
          FILTER
         ================================================= */}

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

      {/* =================================================
          POLICY FORM
         ================================================= */}

      <PolicyFormModal
        show={showPolicyModal}
        saving={policySaving}
        editingPolicy={editingPolicy}
        error={policyError}
        onClose={handleClosePolicyModal}
        onSubmit={handlePolicySubmit}
      />

      {/* =================================================
          RULE MODAL
         ================================================= */}

      <PolicyRuleModal
        show={showRuleModal}
        policy={selectedPolicy}
        loading={ruleLoading}
        saving={ruleSaving}
        error={ruleError}
        bookingRules={bookingPolicies}
        cancelRules={cancelPolicies}
        onClose={handleCloseRuleModal}
        onCreateBookingRule={handleCreateBookingRule}
        onUpdateBookingRule={handleUpdateBookingRule}
        onDeleteBookingRule={handleDeleteBookingRule}
        onCreateCancelRule={handleCreateCancelRule}
        onUpdateCancelRule={handleUpdateCancelRule}
        onDeleteCancelRule={handleDeleteCancelRule}
      />
    </div>
  );
}
