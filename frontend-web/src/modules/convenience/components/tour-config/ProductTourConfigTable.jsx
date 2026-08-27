// src/modules/convenience/tour-config/ProductTourConfigTable.jsx
import React, { useMemo, useState } from "react";
import {
  AlertCircle,
  Edit3,
  Eye,
  Package,
  Plus,
  RefreshCw,
} from "lucide-react";

import useProductTour from "../../hooks/useProductTour";
import "../../styles/tour-config/ProductTourConfigTable.css";

import ProductTourConfigModal from "./ProductTourConfigModal";
import ProductTourDetailModal from "./ProductTourDetailModal"; // Modal chi tiết khi click con mắt

const ProductTourConfigTable = () => {
  const {
    productTours,
    loading,
    error,
    loadProductTours,
    configureProduct,
    updateProduct,
  } = useProductTour();

  const [selectedAssignment, setSelectedAssignment] = useState(null);
  const [viewDetailAssignment, setViewDetailAssignment] = useState(null); // State xem chi tiết

  const formatShortId = (str, maxLength = 8) => {
    if (!str) return "—";
    if (str.length <= maxLength) return str;
    return `${str.substring(0, maxLength)}...`;
  };

  // =====================================================
  // CONFIGURABLE DATA
  // =====================================================

  const configurableTours = useMemo(() => {
    return (productTours || []).filter(
      (item) =>
        item.status === "WAITING_CONFIG" || item.status === "NOT_STARTED",
    );
  }, [productTours]);

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

  const handleSubmit = async (assignmentId, payload) => {
    const assignment = productTours.find((item) => item.id === assignmentId);

    if (!assignment) return;

    if (assignment.status === "WAITING_CONFIG") {
      await configureProduct(assignmentId, payload);
    } else if (assignment.status === "NOT_STARTED") {
      await updateProduct(assignmentId, payload);
    }

    setSelectedAssignment(null);
  };

  // =====================================================
  // FORMAT STATUS
  // =====================================================

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";
      case "NOT_STARTED":
        return "Đã cấu hình";
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
  // LOADING
  // =====================================================

  if (loading && configurableTours.length === 0) {
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

      {/* EMPTY */}
      {configurableTours.length === 0 && !error ? (
        <div className="product-tour-config-empty">
          <Package size={32} />
          <h3>Chưa có sản phẩm cần cấu hình</h3>
          <p>Hiện tại không có sản phẩm nào đang chờ hoặc đã cấu hình.</p>
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
              {configurableTours.map((assignment, index) => {
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
                        {/* NÚT CẤU HÌNH / CHỈNH SỬA */}
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

                        {assignment.status === "NOT_STARTED" && (
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
