import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Search,
  RefreshCw,
  Eye,
  RotateCcw,
  Clock,
  CheckCircle,
  XCircle,
} from "lucide-react";

import LoadingSpinner from "../../../components/common/LoadingSpinner";
import { useFinanceRefunds } from "../hooks/useFinanceRefunds";
import {
  REFUND_STATUS_OPTIONS,
  getRefundStatusLabel,
} from "../constants/refundStatuses";
import { formatDateTime } from "../utils/format";

import "../styles/financeRefunds.css";

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

const FinanceRefunds = () => {
  const navigate = useNavigate();

  const [filterStatus, setFilterStatus] = useState("PENDING");
  const [searchTerm, setSearchTerm] = useState("");

  const { refunds, loading, error, reload } = useFinanceRefunds(filterStatus);

  const filteredRefunds = useMemo(() => {
    const keyword = searchTerm.trim().toLowerCase();

    if (!keyword) return refunds || [];

    return (refunds || []).filter((refund) =>
      [
        refund.id,
        refund.bookingId,
        refund.requestedByUserId,
        refund.reason,
      ].some((value) =>
        String(value ?? "")
          .toLowerCase()
          .includes(keyword),
      ),
    );
  }, [refunds, searchTerm]);

  const summary = useMemo(() => {
    const items = refunds || [];

    return {
      total: items.length,
      pending: items.filter((item) => item.status === "PENDING").length,
      processing: items.filter((item) => item.status === "PROCESSING").length,
      completed: items.filter((item) => item.status === "COMPLETED").length,
    };
  }, [refunds]);

  return (
    <div className="finance-refunds-page">
      <div className="finance-refunds-header">
        <div>
          <h2 className="finance-refunds-title">Quản lý yêu cầu hoàn tiền</h2>
          <p className="finance-refunds-subtitle">
            Kiểm tra chính sách, xác thực và theo dõi các yêu cầu hoàn tiền của
            hành khách.
          </p>
        </div>

        <button
          type="button"
          className="refund-refresh-btn"
          onClick={reload}
          disabled={loading}
        >
          <RefreshCw size={16} />
          Làm mới
        </button>
      </div>

      <div className="refund-summary-grid">
        <div className="refund-summary-card">
          <div className="refund-summary-icon total">
            <RotateCcw size={20} />
          </div>
          <div>
            <span>Tổng yêu cầu</span>
            <strong>{summary.total}</strong>
          </div>
        </div>

        <div className="refund-summary-card">
          <div className="refund-summary-icon pending">
            <Clock size={20} />
          </div>
          <div>
            <span>Chờ xác thực</span>
            <strong>{summary.pending}</strong>
          </div>
        </div>

        <div className="refund-summary-card">
          <div className="refund-summary-icon processing">
            <RefreshCw size={20} />
          </div>
          <div>
            <span>Đang xử lý</span>
            <strong>{summary.processing}</strong>
          </div>
        </div>

        <div className="refund-summary-card">
          <div className="refund-summary-icon completed">
            <CheckCircle size={20} />
          </div>
          <div>
            <span>Đã hoàn tiền</span>
            <strong>{summary.completed}</strong>
          </div>
        </div>
      </div>

      <div className="refund-filter-card">
        <div className="refund-filter-heading">
          <div>
            <h3>Danh sách yêu cầu</h3>
            <p>Chọn trạng thái để xem các yêu cầu tương ứng.</p>
          </div>
        </div>

        <div className="refund-filter-controls">
          <div className="refund-search-box">
            <Search size={18} />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Tìm mã yêu cầu, mã booking, lý do..."
            />
          </div>

          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="refund-status-select"
          >
            {REFUND_STATUS_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error && (
        <div className="refund-error-banner">
          <XCircle size={18} />
          <span>Không tải được danh sách yêu cầu hoàn tiền.</span>
          <button type="button" onClick={reload}>
            Thử lại
          </button>
        </div>
      )}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div className="refund-table-card">
          <div className="refund-table-wrapper">
            <table className="refund-table">
              <thead>
                <tr>
                  <th>Mã yêu cầu</th>
                  <th>Mã booking</th>
                  <th>Người yêu cầu</th>
                  <th>Số tiền hoàn</th>
                  <th>Tỷ lệ hoàn</th>
                  <th>Ngày yêu cầu</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>

              <tbody>
                {filteredRefunds.map((refund) => (
                  <tr key={refund.id}>
                    <td>
                      <strong>RF-{refund.id}</strong>
                    </td>

                    <td>BK-{refund.bookingId}</td>

                    <td>
                      <span className="refund-requester-id">
                        {refund.requestedByUserId || "—"}
                      </span>
                    </td>

                    <td className="refund-amount">
                      {formatCurrency(refund.calculatedRefundAmount)}
                    </td>

                    <td>
                      {refund.refundPercentage != null
                        ? `${refund.refundPercentage}%`
                        : "—"}
                    </td>

                    <td>{formatDateTime(refund.createdAt)}</td>

                    <td>
                      <span
                        className={`refund-status-badge ${getStatusClass(
                          refund.status,
                        )}`}
                      >
                        {getRefundStatusLabel(refund.status)}
                      </span>
                    </td>

                    <td>
                      <button
                        type="button"
                        className="refund-view-btn"
                        onClick={() =>
                          navigate(`/finance/refunds/${refund.id}`)
                        }
                      >
                        <Eye size={15} />
                        Chi tiết
                      </button>
                    </td>
                  </tr>
                ))}

                {filteredRefunds.length === 0 && (
                  <tr>
                    <td colSpan={8} className="refund-empty-cell">
                      Không tìm thấy yêu cầu hoàn tiền nào.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="refund-table-footer">
            Hiển thị {filteredRefunds.length} yêu cầu
          </div>
        </div>
      )}
    </div>
  );
};

export default FinanceRefunds;
