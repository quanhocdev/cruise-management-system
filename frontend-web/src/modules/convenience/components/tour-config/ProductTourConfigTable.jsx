// src/modules/convenience/tour-config/ProductTourConfigTable.jsx
import React, { useMemo, useState } from "react";
import { AlertCircle, Edit3, Package, Plus, RefreshCw } from "lucide-react";

import useProductTour from "../../hooks/useProductTour";
import "../../styles/tour-config/ProductTourConfigTable.css";

import ProductTourConfigModal from "./ProductTourConfigModal";

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
  // OPEN MODAL
  // =====================================================

  const handleOpenConfig = (assignment) => {
    setSelectedAssignment(assignment);
  };

  // =====================================================
  // CLOSE MODAL
  // =====================================================

  const handleCloseModal = () => {
    if (loading) {
      return;
    }

    setSelectedAssignment(null);
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (assignmentId, payload) => {
    const assignment = productTours.find((item) => item.id === assignmentId);

    if (!assignment) {
      return;
    }

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
      {/* =====================================================
          HEADER
          ===================================================== */}

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

      {/* =====================================================
          ERROR
          ===================================================== */}

      {error && (
        <div className="product-tour-config-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* =====================================================
          EMPTY
          ===================================================== */}

      {configurableTours.length === 0 && !error ? (
        <div className="product-tour-config-empty">
          <Package size={32} />

          <h3>Chưa có sản phẩm cần cấu hình</h3>

          <p>Hiện tại không có sản phẩm nào đang chờ hoặc đã cấu hình.</p>
        </div>
      ) : (
        /* =====================================================
           TABLE
           ===================================================== */

        <div className="product-tour-config-table-wrapper">
          <table className="product-tour-config-table">
            <thead>
              <tr>
                <th>Tour</th>
                <th>Khu vực</th>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>

            <tbody>
              {configurableTours.map((assignment) => (
                <tr key={assignment.id}>
                  {/* TOUR */}
                  <td>
                    <div className="product-tour-config-tour">
                      <strong>{assignment.tourCode || "—"}</strong>

                      <span>{assignment.tourName || "—"}</span>
                    </div>
                  </td>

                  {/* AREA */}
                  <td>
                    <div className="product-tour-config-area">
                      <strong>{assignment.cruiseAreaName || "—"}</strong>

                      {assignment.deckNumber != null && (
                        <span>Tầng {assignment.deckNumber}</span>
                      )}
                    </div>
                  </td>

                  {/* PRODUCT */}
                  <td>
                    {assignment.productName ? (
                      <div className="product-tour-config-product">
                        <strong>{assignment.productName}</strong>

                        {assignment.productDescription && (
                          <span>{assignment.productDescription}</span>
                        )}
                      </div>
                    ) : (
                      <span>Chưa chọn sản phẩm</span>
                    )}
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
                    {assignment.status === "WAITING_CONFIG" && (
                      <button
                        type="button"
                        className="product-tour-config-action"
                        onClick={() => handleOpenConfig(assignment)}
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
                      >
                        <Edit3 size={16} />
                        Chỉnh sửa
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* =====================================================
          MODAL
          ===================================================== */}

      {selectedAssignment && (
        <ProductTourConfigModal
          assignment={selectedAssignment}
          onClose={handleCloseModal}
          onSubmit={handleSubmit}
          submitting={loading}
        />
      )}
    </>
  );
};

export default ProductTourConfigTable;
