// src/modules/admin/components/policy/RefundRulesPanel.jsx

import { useEffect, useMemo, useState } from "react";
import { Alert, Button } from "react-bootstrap";

import usePolicyRefundRules from "../../hooks/usePolicyRefundRules";
import { findOverlaps, formatDayRange } from "../../utils/refundRuleUtils";

import RefundRuleTable from "./RefundRuleTable";
import RefundRuleFormModal from "./RefundRuleFormModal";

import "../../styles/RefundRules.css";

export default function RefundRulesPanel({ policyId, onSavingChange }) {
  const {
    rules,
    loading,
    saving,
    error,
    success,
    setError,
    setSuccess,
    createRule,
    updateRule,
    deleteRule,
  } = usePolicyRefundRules(policyId);

  const [showForm, setShowForm] = useState(false);
  const [editingRule, setEditingRule] = useState(null);

  // Báo cho modal cha biết đang lưu để khóa nút đóng
  useEffect(() => {
    onSavingChange?.(saving);
  }, [saving, onSavingChange]);

  const overlaps = useMemo(() => findOverlaps(rules), [rules]);

  const handleOpenCreate = () => {
    setError("");
    setSuccess("");
    setEditingRule(null);
    setShowForm(true);
  };

  const handleOpenEdit = (rule) => {
    setError("");
    setSuccess("");
    setEditingRule(rule);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    if (saving) {
      return;
    }

    setShowForm(false);
    setEditingRule(null);
    setError("");
  };

  const handleSubmit = async (data) => {
    const result = editingRule
      ? await updateRule(editingRule.id, data)
      : await createRule(data);

    if (result) {
      setShowForm(false);
      setEditingRule(null);
    }
  };

  const handleDelete = async (rule) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa luật hoàn tiền ` +
        `${Number(rule.refundPercentage).toLocaleString("vi-VN")}% ` +
        `(${formatDayRange(rule)} trước khởi hành) không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteRule(rule.id);
  };

  return (
    <>
      {/* Lỗi của form hiển thị trong form, nên chỉ hiện ở đây khi form đóng */}
      {error && !showForm && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {overlaps.length > 0 && (
        <Alert variant="warning">
          Có {overlaps.length} cặp luật bị chồng lấn khoảng ngày. Khi hủy tour,
          hệ thống có thể áp dụng sai mức hoàn tiền, hãy kiểm tra lại.
        </Alert>
      )}

      <div className="refund-rule-header">
        <div>
          <h5 className="mb-1">Luật hoàn tiền</h5>

          <p className="text-muted mb-0">
            Thiết lập mức hoàn tiền theo khoảng số ngày trước khi khởi hành.
          </p>
        </div>

        <Button
          variant="primary"
          onClick={handleOpenCreate}
          disabled={loading || saving}
        >
          + Thêm luật
        </Button>
      </div>

      <RefundRuleTable
        rules={rules}
        loading={loading}
        disabled={saving}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

      <RefundRuleFormModal
        show={showForm}
        saving={saving}
        editingRule={editingRule}
        existingRules={rules}
        error={error}
        onClose={handleCloseForm}
        onSubmit={handleSubmit}
      />
    </>
  );
}
