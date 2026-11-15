import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  CheckCircle,
  XCircle,
  Clock,
  RefreshCw,
  FileText,
  User,
  CalendarDays,
  Wallet,
  AlertCircle,
} from "lucide-react";

import LoadingSpinner from "../../../components/common/LoadingSpinner";
import financeService from "../services/financeService";
import { useFinanceRefundDetail } from "../hooks/useFinanceRefunds";
import { getRefundStatusLabel } from "../constants/refundStatuses";
import { formatDateTime } from "../utils/format";

import "../styles/financeRefundDetail.css";

const formatCurrency = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} đ`;

const getStatusClass = (status) => {
  switch (status) {
    case "PENDING":
      return "pending";
    case "APPROVED":
      return "approved";
    case "PROCESSING":
      return "processing";
    case "COMPLETED":
      return "completed";
    case "REJECTED":
      return "rejected";
    case "FAILED":
      return "failed";
    default:
      return "default";
  }
};

const FinanceRefundDetail = () => {
  const { refundId } = useParams();
  const navigate = useNavigate();

  const { refund, loading, error, reload } = useFinanceRefundDetail(refundId);

  const [rejectReason, setRejectReason] = useState("");
  const [showRejectForm, setShowRejectForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [actionError, setActionError] = useState("");

  const canReview = refund?.status === "PENDING";

  const handleApprove = async () => {
    const confirmed = window.confirm(
      "Bạn có chắc chắn muốn duyệt yêu cầu hoàn tiền này không?",
    );

    if (!confirmed) return;

    try {
      setSubmitting(true);
      setActionError("");

      await financeService.approveRefund(refundId);

      await reload();
    } catch (err) {
      console.error("Approve refund error:", err);
      setActionError(
        err?.response?.data?.message ||
          "Không thể duyệt yêu cầu hoàn tiền. Vui lòng thử lại.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleReject = async (e) => {
    e.preventDefault();

    if (!rejectReason.trim()) {
      setActionError("Vui lòng nhập lý do từ chối.");
      return;
    }

    try {
      setSubmitting(true);
      setActionError("");

      await financeService.rejectRefund(refundId, rejectReason.trim());

      setShowRejectForm(false);
      await reload();
    } catch (err) {
      console.error("Reject refund error:", err);
      setActionError(
        err?.response?.data?.message ||
          "Không thể từ chối yêu cầu hoàn tiền. Vui lòng thử lại.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  if (error || !refund) {
    return (
      <div className="finance-refund-detail-page">
        <button
          className="refund-back-btn"
          onClick={() => navigate("/finance/refunds")}
        >
          <ArrowLeft size={17} />
          Quay lại danh sách
        </button>

        <div className="refund-detail-error">
          <AlertCircle size={22} />
          <p>Không tải được thông tin yêu cầu hoàn tiền.</p>
          <button onClick={reload}>Thử lại</button>
        </div>
      </div>
    );
  }

  return (
    <div className="finance-refund-detail-page">
      <div className="refund-detail-header">
        <div>
          <button
            type="button"
            className="refund-back-btn"
            onClick={() => navigate("/finance/refunds")}
          >
            <ArrowLeft size={17} />
            Quay lại danh sách
          </button>

          <h2>Chi tiết yêu cầu hoàn tiền</h2>
          <p>
            Mã yêu cầu: <strong>RF-{refund.id}</strong>
          </p>
        </div>

        <span
          className={`refund-detail-status ${getStatusClass(refund.status)}`}
        >
          {getRefundStatusLabel(refund.status)}
        </span>
      </div>

      {actionError && (
        <div className="refund-detail-alert">
          <AlertCircle size={18} />
          {actionError}
        </div>
      )}

      <div className="refund-detail-grid">
        <div className="refund-detail-main">
          <section className="refund-detail-card">
            <div className="refund-detail-card-heading">
              <FileText size={19} />
              <h3>Thông tin yêu cầu</h3>
            </div>

            <div className="refund-detail-info-grid">
              <div className="refund-detail-info-item">
                <span>Mã booking</span>
                <strong>BK-{refund.bookingId}</strong>
              </div>

              <div className="refund-detail-info-item">
                <span>Người yêu cầu</span>
                <strong>{refund.requestedByUserId || "—"}</strong>
              </div>

              <div className="refund-detail-info-item">
                <span>Ngày tạo yêu cầu</span>
                <strong>{formatDateTime(refund.createdAt)}</strong>
              </div>

              <div className="refund-detail-info-item">
                <span>Ngày khởi hành</span>
                <strong>{refund.departureDate || "—"}</strong>
              </div>

              <div className="refund-detail-info-item">
                <span>Số ngày trước khởi hành</span>
                <strong>{refund.daysBeforeDeparture ?? "—"}</strong>
              </div>

              <div className="refund-detail-info-item">
                <span>Mã quy tắc hoàn tiền</span>
                <strong>{refund.policyRefundRuleId || "—"}</strong>
              </div>
            </div>

            <div className="refund-detail-reason">
              <span>Lý do hành khách yêu cầu hoàn tiền</span>
              <p>{refund.reason || "Không có nội dung."}</p>
            </div>
          </section>

          <section className="refund-detail-card">
            <div className="refund-detail-card-heading">
              <Wallet size={19} />
              <h3>Thông tin tính toán hoàn tiền</h3>
            </div>

            <div className="refund-calculation-row">
              <span>Số tiền thanh toán ban đầu</span>
              <strong>{formatCurrency(refund.originalAmount)}</strong>
            </div>

            <div className="refund-calculation-row">
              <span>Tỷ lệ hoàn tiền theo chính sách</span>
              <strong>{refund.refundPercentage ?? 0}%</strong>
            </div>

            <div className="refund-calculation-result">
              <span>Số tiền được hoàn</span>
              <strong>{formatCurrency(refund.calculatedRefundAmount)}</strong>
            </div>
          </section>

          {(refund.reviewedByUserId ||
            refund.reviewedAt ||
            refund.rejectionReason) && (
            <section className="refund-detail-card">
              <div className="refund-detail-card-heading">
                <User size={19} />
                <h3>Thông tin xét duyệt</h3>
              </div>

              <div className="refund-detail-info-grid">
                <div className="refund-detail-info-item">
                  <span>Người xét duyệt</span>
                  <strong>{refund.reviewedByUserId || "—"}</strong>
                </div>

                <div className="refund-detail-info-item">
                  <span>Thời gian xét duyệt</span>
                  <strong>{formatDateTime(refund.reviewedAt)}</strong>
                </div>
              </div>

              {refund.rejectionReason && (
                <div className="refund-detail-reason rejection">
                  <span>Lý do từ chối</span>
                  <p>{refund.rejectionReason}</p>
                </div>
              )}
            </section>
          )}
        </div>

        <aside className="refund-detail-sidebar">
          <section className="refund-detail-card">
            <h3>Thao tác Finance</h3>

            {canReview ? (
              <>
                <p className="refund-action-description">
                  Vui lòng kiểm tra chính sách và số tiền hoàn trước khi đưa ra
                  quyết định.
                </p>

                <button
                  type="button"
                  className="refund-approve-btn"
                  onClick={handleApprove}
                  disabled={submitting}
                >
                  <CheckCircle size={17} />
                  {submitting ? "Đang xử lý..." : "Duyệt hoàn tiền"}
                </button>

                <button
                  type="button"
                  className="refund-reject-btn"
                  onClick={() => {
                    setShowRejectForm((current) => !current);
                    setActionError("");
                  }}
                  disabled={submitting}
                >
                  <XCircle size={17} />
                  Từ chối yêu cầu
                </button>

                {showRejectForm && (
                  <form className="refund-reject-form" onSubmit={handleReject}>
                    <label htmlFor="refund-reject-reason">
                      Lý do từ chối <span>*</span>
                    </label>

                    <textarea
                      id="refund-reject-reason"
                      value={rejectReason}
                      onChange={(e) => setRejectReason(e.target.value)}
                      placeholder="Nhập lý do từ chối yêu cầu..."
                      rows={4}
                      maxLength={2000}
                      required
                    />

                    <div className="refund-reject-actions">
                      <button
                        type="button"
                        onClick={() => setShowRejectForm(false)}
                        disabled={submitting}
                      >
                        Hủy
                      </button>

                      <button type="submit" disabled={submitting}>
                        {submitting ? "Đang gửi..." : "Xác nhận từ chối"}
                      </button>
                    </div>
                  </form>
                )}
              </>
            ) : (
              <div className="refund-not-pending">
                <Clock size={20} />
                <p>
                  Yêu cầu này đã được xử lý khỏi trạng thái chờ xác thực. Bạn
                  không thể duyệt hoặc từ chối lại.
                </p>
              </div>
            )}
          </section>

          <section className="refund-detail-card refund-detail-note">
            <div className="refund-detail-card-heading">
              <CalendarDays size={18} />
              <h3>Lưu ý</h3>
            </div>
            <p>
              Khi Finance duyệt, yêu cầu sẽ được chuyển sang Payment Service để
              thực hiện hoàn tiền qua cổng thanh toán.
            </p>
            <p>
              Trạng thái hoàn tiền cuối cùng sẽ được cập nhật khi hệ thống nhận
              kết quả xử lý từ Payment Service.
            </p>
          </section>
        </aside>
      </div>
    </div>
  );
};

export default FinanceRefundDetail;
