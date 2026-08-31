// src/modules/convenience/tour-config/ProductTourConfigTable.jsx
import React, { useMemo, useState } from "react";
import {
  AlertCircle,
  CheckCircle2,
  Edit3,
  Eye,
  Package,
  Plus,
  RefreshCw,
} from "lucide-react";

import useProductTour from "../../hooks/useProductTour";
import "../../styles/tour-config/ProductTourConfigTable.css";

import ProductTourConfigModal from "./ProductTourConfigModal";
import ProductTourDetailModal from "../history/ProductTourDetailModal"; // Modal chi tiết khi click con mắt

// =========================================================
// STATUS TABS — khớp đúng ProductTourStatus (Java enum)
// =========================================================

const STATUS_TABS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa bắt đầu" },
  { value: "IN_PROGRESS", label: "Đang phục vụ" },
  { value: "OUT_OF_STOCK", label: "Hết hàng" },
  { value: "COMPLETED", label: "Đã kết thúc" },
];

const formatShortId = (str, maxLength = 8) => {
  if (!str) return "—";
  if (str.length <= maxLength) return str;
  return `${str.substring(0, maxLength)}...`;
};

const ProductTourConfigTable = () => {
  const {
    productTours,
    tourSummaries,
    loading,
    error,
    completing,
    completeError,
    loadProductTours,
    configureProduct,
    updateProduct,
    completeTourConfiguration,
  } = useProductTour();

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [selectedAssignment, setSelectedAssignment] = useState(null);
  const [viewDetailAssignment, setViewDetailAssignment] = useState(null); // State xem chi tiết

  // Tour đang được chọn để "Hoàn thành cấu hình"
  const [selectedTourIdToComplete, setSelectedTourIdToComplete] = useState("");
  const [completeSuccess, setCompleteSuccess] = useState(false);

  // =====================================================
  // FILTER THEO TAB TRẠNG THÁI (không chỉ WAITING_CONFIG nữa)
  // =====================================================

  const filteredTours = useMemo(() => {
    if (statusFilter === "ALL") return productTours || [];
    return (productTours || []).filter((item) => item.status === statusFilter);
  }, [productTours, statusFilter]);

  // =====================================================
  // OPEN/CLOSE CONFIG MODAL
  // =====================================================

  const handleOpenConfig = (assignment) => {
    setSelectedAssignment(assignment);
  };

  const handleCloseModal = () => {
    if (loading) return;
    setSelectedAssignment(null);
  };

  // =====================================================
  // SUBMIT
  // =====================================================
  //
  // ✅ Backend chỉ cho:
  //   - configure()   khi status === WAITING_CONFIG
  //   - updateConfig() khi status === CONFIGURED (không phải NOT_STARTED)
  //
  // =====================================================

  const handleSubmit = async (assignmentId, payload) => {
    const assignment = productTours.find((item) => item.id === assignmentId);

    if (!assignment) return;

    if (assignment.status === "WAITING_CONFIG") {
      await configureProduct(assignmentId, payload);
    } else if (assignment.status === "CONFIGURED") {
      await updateProduct(assignmentId, payload);
    }

    setSelectedAssignment(null);
  };

  // =====================================================
  // FORMAT STATUS — khớp đúng ý nghĩa thật của từng status
  // =====================================================

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";
      case "CONFIGURED":
        return "Đã cấu hình";
      case "NOT_STARTED":
        return "Chưa bắt đầu";
      case "IN_PROGRESS":
        return "Đang phục vụ";
      case "OUT_OF_STOCK":
        return "Hết hàng";
      case "COMPLETED":
        return "Đã kết thúc";
      default:
        return status || "Không xác định";
    }
  };

  // =====================================================
  // HOÀN THÀNH CẤU HÌNH TOUR
  // =====================================================

  const selectedTourSummary = useMemo(
    () =>
      tourSummaries.find((tour) => tour.tourId === selectedTourIdToComplete),
    [tourSummaries, selectedTourIdToComplete],
  );

  const canComplete =
    !!selectedTourSummary &&
    !selectedTourSummary.completed &&
    selectedTourSummary.total > 0 &&
    selectedTourSummary.configuredCount === selectedTourSummary.total;

  const handleCompleteTour = async () => {
    if (!selectedTourIdToComplete || !canComplete) return;

    try {
      setCompleteSuccess(false);
      await completeTourConfiguration(selectedTourIdToComplete);
      setCompleteSuccess(true);
      setSelectedTourIdToComplete("");
    } catch (err) {
      console.error("COMPLETE PRODUCT TOUR ERROR:", err);
      // completeError đã được hook set, hiển thị bên dưới
    }
  };

  // =====================================================
  // LOADING
  // =====================================================

  if (loading && filteredTours.length === 0) {
    return (
      <div className="product-tour-config-loading">
        <RefreshCw size={20} className="spin" />
        <span>Đang tải danh sách sản phẩm...</span>
      </div>
    );
  }

  return (
    <>
      {/* HEADER */}
      <div className="product-tour-config-toolbar">
        <div>
          <h2>
            <Package size={20} />
            Sản phẩm của Tour
          </h2>
          <p>Các sản phẩm được Operation phân công cho Convenience cấu hình.</p>
        </div>

        <button
          type="button"
          className="product-tour-config-refresh"
          onClick={loadProductTours}
          disabled={loading}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />
          Làm mới
        </button>
      </div>

      {/* ERROR */}
      {error && (
        <div className="product-tour-config-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* ============ HOÀN THÀNH CẤU HÌNH TOUR ============ */}
      <div className="product-tour-config-complete-box">
        <div className="product-tour-config-complete-info">
          <strong>Hoàn thành cấu hình Tour</strong>
          <p>
            Chọn Tour và bấm Hoàn thành khi tất cả sản phẩm của Tour đó đã được
            cấu hình xong. Hệ thống sẽ gửi thông tin sang Tour service.
          </p>
        </div>

        <div className="product-tour-config-complete-controls">
          <select
            className="product-tour-config-complete-select"
            value={selectedTourIdToComplete}
            onChange={(e) => {
              setSelectedTourIdToComplete(e.target.value);
              setCompleteSuccess(false);
            }}
            disabled={completing}
          >
            <option value="">— Chọn Tour —</option>
            {tourSummaries.map((tour) => (
              <option key={tour.tourId} value={tour.tourId}>
                {formatShortId(tour.tourId, 8)} ({tour.configuredCount}/
                {tour.total} đã cấu hình)
                {tour.completed ? " — Đã hoàn thành" : ""}
              </option>
            ))}
          </select>

          <button
            type="button"
            className="product-tour-config-complete-button"
            onClick={handleCompleteTour}
            disabled={!canComplete || completing}
          >
            <CheckCircle2 size={16} />
            {completing ? "Đang xử lý..." : "Hoàn thành cấu hình"}
          </button>
        </div>

        {selectedTourIdToComplete && selectedTourSummary?.completed && (
          <span className="product-tour-config-complete-hint">
            Tour này đã được hoàn thành cấu hình trước đó.
          </span>
        )}

        {selectedTourIdToComplete &&
          !selectedTourSummary?.completed &&
          !canComplete && (
            <span className="product-tour-config-complete-hint">
              Tour này còn sản phẩm chưa được cấu hình xong.
            </span>
          )}

        {completeError && (
          <span className="product-tour-config-complete-error">
            {completeError}
          </span>
        )}

        {completeSuccess && (
          <span className="product-tour-config-complete-success">
            Đã hoàn thành cấu hình Tour thành công.
          </span>
        )}
      </div>

      {/* STATUS TABS */}
      <div className="product-tour-config-filters">
        {STATUS_TABS.map((tab) => (
          <button
            key={tab.value}
            type="button"
            className={`product-tour-config-filter-btn ${
              statusFilter === tab.value
                ? "product-tour-config-filter-btn--active"
                : ""
            }`}
            onClick={() => setStatusFilter(tab.value)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* EMPTY */}
      {filteredTours.length === 0 && !error ? (
        <div className="product-tour-config-empty">
          <Package size={32} />
          <h3>Không có sản phẩm nào</h3>
          <p>Không có sản phẩm nào khớp với bộ lọc hiện tại.</p>
        </div>
      ) : (
        /* TABLE */
        <div className="product-tour-config-table-wrapper">
          <table className="product-tour-config-table">
            <thead>
              <tr>
                <th>STT</th>
                <th>Mã Tour</th>
                <th>Khu vực / Tầng</th>
                <th>Mã / Tên Sản phẩm</th>
                <th>Số lượng</th>
                <th>Trạng thái</th>
                <th style={{ textAlign: "center" }}>Thao tác</th>
              </tr>
            </thead>

            <tbody>
              {filteredTours.map((assignment, index) => {
                const tourIdValue = assignment.tourId || assignment.id;
                const fullTourCode =
                  assignment.tourCode ||
                  (tourIdValue ? `TOUR-${tourIdValue}` : "");
                const fullAreaId =
                  assignment.cruiseAreaName || assignment.cruiseAreaId || "";

                return (
                  <tr key={assignment.id}>
                    {/* STT */}
                    <td>{index + 1}</td>

                    {/* TOUR */}
                    <td>
                      <div className="product-tour-config-tour">
                        <strong className="font-mono" title={fullTourCode}>
                          {assignment.tourCode ||
                            (tourIdValue
                              ? `TOUR-${formatShortId(tourIdValue, 6)}`
                              : "—")}
                        </strong>
                        <button
                          type="button"
                          className="product-tour-config-action product-tour-config-action--view"
                          onClick={() => setViewDetailAssignment(assignment)}
                          title="Xem chi tiết Tour"
                        >
                          <Eye size={14} />
                        </button>
                        {assignment.tourName && (
                          <span>{assignment.tourName}</span>
                        )}
                      </div>
                    </td>

                    {/* AREA */}
                    <td>
                      <div className="product-tour-config-area">
                        <strong className="font-mono" title={fullAreaId}>
                          {assignment.cruiseAreaName ||
                            formatShortId(assignment.cruiseAreaId, 6)}
                        </strong>
                        <button
                          type="button"
                          className="product-tour-config-action product-tour-config-action--view"
                          onClick={() => setViewDetailAssignment(assignment)}
                          title="Xem chi tiết Khu vực"
                        >
                          <Eye size={14} />
                        </button>
                        {assignment.deckNumber != null && (
                          <span>Tầng {assignment.deckNumber}</span>
                        )}
                      </div>
                    </td>

                    {/* PRODUCT */}
                    <td>
                      <div className="product-tour-config-product">
                        <strong
                          className="font-mono"
                          title={assignment.productId}
                        >
                          {assignment.productCode ||
                            (assignment.productId
                              ? `PRD-${formatShortId(assignment.productId, 6)}`
                              : "—")}
                        </strong>
                        {assignment.productName && (
                          <span>{assignment.productName}</span>
                        )}
                      </div>
                    </td>

                    {/* QUANTITY */}
                    <td>
                      {assignment.quantity != null ? assignment.quantity : "—"}
                    </td>

                    {/* STATUS */}
                    <td>
                      <span
                        className={`product-tour-config-status ${String(
                          assignment.status || "",
                        ).toLowerCase()}`}
                      >
                        {getStatusLabel(assignment.status)}
                      </span>
                    </td>

                    {/* ACTION */}
                    <td>
                      <div
                        style={{
                          display: "flex",
                          gap: "8px",
                          justifyContent: "center",
                        }}
                      >
                        {/* ✅ Cấu hình lần đầu: chỉ khi WAITING_CONFIG */}
                        {assignment.status === "WAITING_CONFIG" && (
                          <button
                            type="button"
                            className="product-tour-config-action"
                            onClick={() => handleOpenConfig(assignment)}
                            title="Cấu hình"
                          >
                            <Plus size={16} />
                            Cấu hình
                          </button>
                        )}

                        {/* ✅ Chỉnh sửa: chỉ khi CONFIGURED (khớp backend updateConfig) */}
                        {assignment.status === "CONFIGURED" && (
                          <button
                            type="button"
                            className="product-tour-config-action"
                            onClick={() => handleOpenConfig(assignment)}
                            title="Chỉnh sửa"
                          >
                            <Edit3 size={16} />
                            Chỉnh sửa
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* MODAL CẤU HÌNH / EDIT */}
      {selectedAssignment && (
        <ProductTourConfigModal
          assignment={selectedAssignment}
          onClose={handleCloseModal}
          onSubmit={handleSubmit}
          submitting={loading}
        />
      )}

      {/* MODAL XEM CHI TIẾT (EYE ICON) */}
      {viewDetailAssignment && (
        <ProductTourDetailModal
          assignmentId={viewDetailAssignment.id}
          onClose={() => setViewDetailAssignment(null)}
        />
      )}
    </>
  );
};

export default ProductTourConfigTable;
