import { useState } from "react";
import { Alert, Button, Modal, Spinner } from "react-bootstrap";

import usePolicy from "../hooks/usePolicy";

import PolicyTable from "../components/policy/PolicyTable";
import PolicyFormModal from "../components/policy/PolicyFormModal";
import BookingPolicyTable from "../components/policy/BookingPolicyTable";
import CancelPolicyTable from "../components/policy/CancelPolicyTable";

import "../styles/ManagerPolicy.css";

export default function ManagerPolicy() {
  const {
    policies,

    bookingRules,
    cancelRules,

    loading,
    rulesLoading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    createPolicy,
    updatePolicy,
    deletePolicy,

    fetchBookingRules,
    createBookingRule,
    updateBookingRule,
    deleteBookingRule,

    fetchCancelRules,
    createCancelRule,
    updateCancelRule,
    deleteCancelRule,

    clearMessages,
  } = usePolicy();

  /*
   * =====================================================
   * POLICY MODAL
   * =====================================================
   */

  const [showPolicyModal, setShowPolicyModal] = useState(false);

  const [editingPolicy, setEditingPolicy] = useState(null);

  const [policyForm, setPolicyForm] = useState({
    type: "",
    title: "",
    content: "",
    status: "ACTIVE",
  });

  /*
   * =====================================================
   * RULE MODAL
   * =====================================================
   */

  const [showRuleModal, setShowRuleModal] = useState(false);

  const [editingRule, setEditingRule] = useState(null);

  const [rulePolicy, setRulePolicy] = useState(null);

  const [ruleForm, setRuleForm] = useState({
    daysBefore: "",
    refundPercent: "",
    daysBeforeDeparture: "",
    discountPercent: "",
    status: "ACTIVE",
  });

  /*
   * =====================================================
   * OPEN CREATE POLICY
   * =====================================================
   */

  const handleOpenCreatePolicy = () => {
    setEditingPolicy(null);

    setPolicyForm({
      type: "",
      title: "",
      content: "",
      status: "ACTIVE",
    });

    clearMessages();

    setShowPolicyModal(true);
  };

  /*
   * =====================================================
   * OPEN EDIT POLICY
   * =====================================================
   */

  const handleOpenEditPolicy = (policy) => {
    setEditingPolicy(policy);

    setPolicyForm({
      type: policy.type || "",
      title: policy.title || "",
      content: policy.content || "",
      status: policy.status || "ACTIVE",
    });

    clearMessages();

    setShowPolicyModal(true);
  };

  /*
   * =====================================================
   * CLOSE POLICY MODAL
   * =====================================================
   */

  const handleClosePolicyModal = () => {
    if (saving) {
      return;
    }

    setShowPolicyModal(false);
  };

  /*
   * =====================================================
   * POLICY INPUT
   * =====================================================
   */

  const handlePolicyChange = (event) => {
    const { name, value } = event.target;

    setPolicyForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /*
   * =====================================================
   * SUBMIT POLICY
   * =====================================================
   */

  const handlePolicySubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!policyForm.type) {
      setError("Vui lòng chọn loại chính sách.");
      return;
    }

    if (!policyForm.title.trim()) {
      setError("Vui lòng nhập tiêu đề chính sách.");
      return;
    }

    if (!policyForm.content.trim()) {
      setError("Vui lòng nhập nội dung chính sách.");
      return;
    }

    let result = null;

    if (!editingPolicy) {
      result = await createPolicy({
        type: policyForm.type,
        title: policyForm.title.trim(),
        content: policyForm.content.trim(),
      });
    } else {
      result = await updatePolicy(editingPolicy.id, {
        title: policyForm.title.trim(),
        content: policyForm.content.trim(),
        status: policyForm.status,
      });
    }

    if (result) {
      setShowPolicyModal(false);
    }
  };

  /*
   * =====================================================
   * DELETE POLICY
   * =====================================================
   */

  const handleDeletePolicy = async (policy) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa chính sách "${policy.title}" không?\n\nCác quy tắc ${policy.type === "BOOKING" ? "đăng ký" : "hủy / hoàn tiền"} thuộc chính sách này cũng sẽ không còn được quản lý từ đây.`,
    );

    if (!confirmed) {
      return;
    }

    await deletePolicy(policy.id);
  };

  /*
   * =====================================================
   * OPEN RULE MANAGER
   * =====================================================
   */

  const handleOpenRuleManager = async (policy) => {
    setRulePolicy(policy);
    setEditingRule(null);

    setRuleForm({
      daysBefore: "",
      refundPercent: "",
      daysBeforeDeparture: "",
      discountPercent: "",
      status: "ACTIVE",
    });

    clearMessages();

    if (policy.type === "BOOKING") {
      await fetchBookingRules(policy.id);
    }

    if (policy.type === "CANCEL") {
      await fetchCancelRules(policy.id);
    }
  };

  /*
   * =====================================================
   * CLOSE RULE MANAGER
   * =====================================================
   */

  const handleCloseRuleManager = () => {
    if (saving) {
      return;
    }

    setRulePolicy(null);
    setEditingRule(null);
    setShowRuleModal(false);
  };

  /*
   * =====================================================
   * OPEN CREATE RULE
   * =====================================================
   */

  const handleOpenCreateRule = () => {
    setEditingRule(null);

    setRuleForm({
      daysBefore: "",
      refundPercent: "",
      daysBeforeDeparture: "",
      discountPercent: "",
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowRuleModal(true);
  };

  /*
   * =====================================================
   * OPEN EDIT RULE
   * =====================================================
   */

  const handleOpenEditRule = (rule) => {
    setEditingRule(rule);

    if (rulePolicy?.type === "BOOKING") {
      setRuleForm({
        daysBefore: "",
        refundPercent: "",
        daysBeforeDeparture: rule.daysBeforeDeparture ?? "",
        discountPercent: rule.discountPercent ?? "",
        status: rule.status || "ACTIVE",
      });
    } else {
      setRuleForm({
        daysBefore: rule.daysBefore ?? "",
        refundPercent: rule.refundPercent ?? "",
        daysBeforeDeparture: "",
        discountPercent: "",
        status: rule.status || "ACTIVE",
      });
    }

    setError("");
    setSuccess("");

    setShowRuleModal(true);
  };

  /*
   * =====================================================
   * CLOSE RULE FORM
   * =====================================================
   */

  const handleCloseRuleForm = () => {
    if (saving) {
      return;
    }

    setShowRuleModal(false);
  };

  /*
   * =====================================================
   * RULE INPUT
   * =====================================================
   */

  const handleRuleChange = (event) => {
    const { name, value } = event.target;

    setRuleForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /*
   * =====================================================
   * SUBMIT BOOKING RULE
   * =====================================================
   */

  const handleSubmitBookingRule = async () => {
    if (!rulePolicy) {
      return;
    }

    if (ruleForm.daysBeforeDeparture === "") {
      setError("Vui lòng nhập số ngày trước khi khởi hành.");
      return;
    }

    if (Number(ruleForm.daysBeforeDeparture) < 0) {
      setError("Số ngày trước khi khởi hành không được âm.");
      return;
    }

    if (ruleForm.discountPercent === "") {
      setError("Vui lòng nhập phần trăm giảm giá.");
      return;
    }

    const discount = Number(ruleForm.discountPercent);

    if (discount < 0 || discount > 100) {
      setError("Phần trăm giảm giá phải từ 0 đến 100.");
      return;
    }

    let result = null;

    if (!editingRule) {
      result = await createBookingRule(rulePolicy.id, {
        daysBeforeDeparture: ruleForm.daysBeforeDeparture,
        discountPercent: ruleForm.discountPercent,
      });
    } else {
      result = await updateBookingRule(rulePolicy.id, editingRule.id, {
        daysBeforeDeparture: ruleForm.daysBeforeDeparture,
        discountPercent: ruleForm.discountPercent,
        status: ruleForm.status,
      });
    }

    if (result) {
      setShowRuleModal(false);
    }
  };

  /*
   * =====================================================
   * SUBMIT CANCEL RULE
   * =====================================================
   */

  const handleSubmitCancelRule = async () => {
    if (!rulePolicy) {
      return;
    }

    if (ruleForm.daysBefore === "") {
      setError("Vui lòng nhập số ngày trước khi khởi hành.");
      return;
    }

    if (Number(ruleForm.daysBefore) < 0) {
      setError("Số ngày trước khi khởi hành không được âm.");
      return;
    }

    if (ruleForm.refundPercent === "") {
      setError("Vui lòng nhập phần trăm hoàn tiền.");
      return;
    }

    const refund = Number(ruleForm.refundPercent);

    if (refund < 0 || refund > 100) {
      setError("Phần trăm hoàn tiền phải từ 0 đến 100.");
      return;
    }

    let result = null;

    if (!editingRule) {
      result = await createCancelRule(rulePolicy.id, {
        daysBefore: ruleForm.daysBefore,
        refundPercent: ruleForm.refundPercent,
      });
    } else {
      result = await updateCancelRule(rulePolicy.id, editingRule.id, {
        daysBefore: ruleForm.daysBefore,
        refundPercent: ruleForm.refundPercent,
        status: ruleForm.status,
      });
    }

    if (result) {
      setShowRuleModal(false);
    }
  };

  /*
   * =====================================================
   * SUBMIT RULE
   * =====================================================
   */

  const handleRuleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!rulePolicy) {
      return;
    }

    if (rulePolicy.type === "BOOKING") {
      await handleSubmitBookingRule();
      return;
    }

    if (rulePolicy.type === "CANCEL") {
      await handleSubmitCancelRule();
    }
  };

  /*
   * =====================================================
   * DELETE BOOKING RULE
   * =====================================================
   */

  const handleDeleteBookingRule = async (rule) => {
    if (!rulePolicy) {
      return;
    }

    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa mức giảm ${rule.discountPercent}% cho khách đặt trước ${rule.daysBeforeDeparture} ngày không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteBookingRule(rulePolicy.id, rule.id);
  };

  /*
   * =====================================================
   * DELETE CANCEL RULE
   * =====================================================
   */

  const handleDeleteCancelRule = async (rule) => {
    if (!rulePolicy) {
      return;
    }

    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa mức hoàn ${rule.refundPercent}% cho trường hợp hủy trước ${rule.daysBefore} ngày không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteCancelRule(rulePolicy.id, rule.id);
  };

  /*
   * =====================================================
   * LABEL
   * =====================================================
   */

  const getPolicyTypeLabel = (type) => {
    if (type === "BOOKING") {
      return "Đăng ký / giảm giá";
    }

    if (type === "CANCEL") {
      return "Hủy / hoàn tiền";
    }

    return type;
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */

  return (
    <div className="manager-policy-page container-fluid py-4">
      {/* =================================================
          HEADER
          ================================================= */}

      <div className="manager-policy-header mb-4">
        <div>
          <h2 className="manager-policy-title">Quản lý chính sách</h2>

          <p className="manager-policy-description">
            Quản lý chính sách đăng ký, giảm giá, hủy và hoàn tiền cho hệ thống
            Cruise.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreatePolicy}>
          + Tạo chính sách
        </Button>
      </div>

      {/* =================================================
          SUCCESS
          ================================================= */}

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {/* =================================================
          ERROR
          ================================================= */}

      {error && !showPolicyModal && !showRuleModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {/* =================================================
          POLICY CARD
          ================================================= */}

      <div className="manager-policy-card">
        <div className="manager-policy-card-header">
          <div>
            <h5 className="mb-1">Danh sách chính sách</h5>

            <span className="text-muted">
              Mỗi loại chỉ có một chính sách chính.
            </span>
          </div>

          <div className="manager-policy-summary">
            <span>
              Tổng: <strong>{policies.length}</strong>
            </span>
          </div>
        </div>

        <div className="manager-policy-card-body">
          <PolicyTable
            policies={policies}
            loading={loading}
            onEdit={handleOpenEditPolicy}
            onDelete={handleDeletePolicy}
            onManageRules={handleOpenRuleManager}
          />
        </div>
      </div>

      {/* =================================================
          POLICY FORM
          ================================================= */}

      <PolicyFormModal
        show={showPolicyModal}
        saving={saving}
        editingPolicy={editingPolicy}
        form={policyForm}
        error={error}
        onClose={handleClosePolicyModal}
        onSubmit={handlePolicySubmit}
        onChange={handlePolicyChange}
      />

      {/* =================================================
          RULE MANAGER MODAL
          ================================================= */}

      <Modal
        show={Boolean(rulePolicy)}
        onHide={handleCloseRuleManager}
        centered
        size="xl"
        backdrop={saving ? "static" : true}
        keyboard={!saving}
      >
        <Modal.Header closeButton={!saving}>
          <div>
            <Modal.Title>
              {rulePolicy
                ? getPolicyTypeLabel(rulePolicy.type)
                : "Quản lý chính sách"}
            </Modal.Title>

            {rulePolicy && (
              <div className="manager-policy-rule-subtitle">
                {rulePolicy.title}
              </div>
            )}
          </div>
        </Modal.Header>

        <Modal.Body>
          {error && !showRuleModal && (
            <Alert variant="danger" dismissible onClose={() => setError("")}>
              {error}
            </Alert>
          )}

          {success && !showRuleModal && (
            <Alert variant="success" dismissible onClose={() => setSuccess("")}>
              {success}
            </Alert>
          )}

          <div className="manager-policy-rule-toolbar">
            <div>
              <h6 className="mb-1">
                {rulePolicy?.type === "BOOKING"
                  ? "Các mức giảm giá"
                  : "Các mức hoàn tiền"}
              </h6>

              <small className="text-muted">
                Các mức được áp dụng dựa trên số ngày trước ngày khởi hành.
              </small>
            </div>

            <Button
              variant="primary"
              onClick={handleOpenCreateRule}
              disabled={rulesLoading || saving}
            >
              + Thêm mức
            </Button>
          </div>

          <div className="manager-policy-rule-table">
            {rulePolicy?.type === "BOOKING" && (
              <BookingPolicyTable
                rules={bookingRules}
                loading={rulesLoading}
                onEdit={handleOpenEditRule}
                onDelete={handleDeleteBookingRule}
              />
            )}

            {rulePolicy?.type === "CANCEL" && (
              <CancelPolicyTable
                rules={cancelRules}
                loading={rulesLoading}
                onEdit={handleOpenEditRule}
                onDelete={handleDeleteCancelRule}
              />
            )}
          </div>
        </Modal.Body>

        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={handleCloseRuleManager}
            disabled={saving}
          >
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* =================================================
          RULE FORM MODAL
          ================================================= */}

      <Modal
        show={showRuleModal}
        onHide={handleCloseRuleForm}
        centered
        backdrop={saving ? "static" : true}
        keyboard={!saving}
      >
        <form onSubmit={handleRuleSubmit}>
          <Modal.Header closeButton={!saving}>
            <Modal.Title>
              {editingRule ? "Cập nhật mức chính sách" : "Thêm mức chính sách"}
            </Modal.Title>
          </Modal.Header>

          <Modal.Body>
            {error && <Alert variant="danger">{error}</Alert>}

            {rulePolicy?.type === "BOOKING" && (
              <>
                <div className="mb-3">
                  <label className="form-label" htmlFor="daysBeforeDeparture">
                    Số ngày trước khởi hành
                  </label>

                  <input
                    id="daysBeforeDeparture"
                    name="daysBeforeDeparture"
                    type="number"
                    min="0"
                    className="form-control"
                    value={ruleForm.daysBeforeDeparture}
                    onChange={handleRuleChange}
                    disabled={saving}
                    placeholder="Ví dụ: 30"
                  />

                  <div className="form-text">
                    Ví dụ: khách đặt trước 30 ngày.
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label" htmlFor="discountPercent">
                    Phần trăm giảm giá
                  </label>

                  <div className="input-group">
                    <input
                      id="discountPercent"
                      name="discountPercent"
                      type="number"
                      min="0"
                      max="100"
                      step="0.01"
                      className="form-control"
                      value={ruleForm.discountPercent}
                      onChange={handleRuleChange}
                      disabled={saving}
                      placeholder="Ví dụ: 10"
                    />

                    <span className="input-group-text">%</span>
                  </div>
                </div>
              </>
            )}

            {rulePolicy?.type === "CANCEL" && (
              <>
                <div className="mb-3">
                  <label className="form-label" htmlFor="daysBefore">
                    Số ngày trước khởi hành
                  </label>

                  <input
                    id="daysBefore"
                    name="daysBefore"
                    type="number"
                    min="0"
                    className="form-control"
                    value={ruleForm.daysBefore}
                    onChange={handleRuleChange}
                    disabled={saving}
                    placeholder="Ví dụ: 15"
                  />

                  <div className="form-text">Ví dụ: hủy trước 15 ngày.</div>
                </div>

                <div className="mb-3">
                  <label className="form-label" htmlFor="refundPercent">
                    Phần trăm hoàn tiền
                  </label>

                  <div className="input-group">
                    <input
                      id="refundPercent"
                      name="refundPercent"
                      type="number"
                      min="0"
                      max="100"
                      step="0.01"
                      className="form-control"
                      value={ruleForm.refundPercent}
                      onChange={handleRuleChange}
                      disabled={saving}
                      placeholder="Ví dụ: 80"
                    />

                    <span className="input-group-text">%</span>
                  </div>
                </div>
              </>
            )}

            {editingRule && (
              <div className="mb-3">
                <label className="form-label" htmlFor="ruleStatus">
                  Trạng thái
                </label>

                <select
                  id="ruleStatus"
                  name="status"
                  className="form-select"
                  value={ruleForm.status}
                  onChange={handleRuleChange}
                  disabled={saving}
                >
                  <option value="ACTIVE">Đang hoạt động</option>

                  <option value="INACTIVE">Ngừng hoạt động</option>
                </select>
              </div>
            )}
          </Modal.Body>

          <Modal.Footer>
            <Button
              variant="secondary"
              onClick={handleCloseRuleForm}
              disabled={saving}
            >
              Hủy
            </Button>

            <Button type="submit" variant="primary" disabled={saving}>
              {saving ? (
                <>
                  <Spinner size="sm" animation="border" className="me-2" />
                  Đang lưu...
                </>
              ) : editingRule ? (
                "Lưu thay đổi"
              ) : (
                "Thêm mức"
              )}
            </Button>
          </Modal.Footer>
        </form>
      </Modal>
    </div>
  );
}
