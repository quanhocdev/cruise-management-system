// src/modules/convenience/tour-config/ProductTourConfigTable.jsx
// (SỬA file có sẵn) Phần dùng chung đã chuyển sang TourConfigShared.jsx

import React, { useMemo, useState } from "react";
import { AlertCircle, Package, RefreshCw } from "lucide-react";

import useProductTour from "../../hooks/useProductTour";
import "../../styles/tour-config/ProductTourConfigTable.css";

import ProductTourConfigModal from "./ProductTourConfigModal";
// ⚠️ File gốc đang comment import này nhưng vẫn render <ProductTourDetailModal>
// -> bấm con mắt sẽ crash. Bỏ comment / sửa đường dẫn cho đúng file thật:
import ProductTourDetailModal from "../history/ProductTourDetailModal";
import {
  AreaCell,
  ConfigAction,
  StatusTabs,
  TourCell,
  TourCompleteBox,
  formatShortId,
  getStatusLabel,
} from "./TourConfigShared";

const PREFIX = "product-tour-config";
// Khớp ProductTourStatus (Java enum)
const STATUSES = [
  "ALL",
  "WAITING_CONFIG",
  "CONFIGURED",
  "NOT_STARTED",
  "IN_PROGRESS",
  "OUT_OF_STOCK",
  "COMPLETED",
];

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
  const [viewDetailAssignment, setViewDetailAssignment] = useState(null);

  const filteredTours = useMemo(() => {
    const list = productTours || [];
    if (statusFilter === "ALL") return list;
    return list.filter((item) => item.status === statusFilter);
  }, [productTours, statusFilter]);

  const handleCloseModal = () => {
    if (loading) return;
    setSelectedAssignment(null);
  };

  // Backend chỉ cho configure() khi WAITING_CONFIG, updateConfig() khi CONFIGURED
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

  if (loading && filteredTours.length === 0) {
    return (
      <div className={`${PREFIX}-loading`}>
        <RefreshCw size={20} className="spin" />
        <span>Đang tải danh sách sản phẩm...</span>
      </div>
    );
  }

  return (
    <>
      <div className={`${PREFIX}-toolbar`}>
        <div>
          <h2>
            <Package size={20} />
            Sản phẩm của Tour
          </h2>
          <p>Các sản phẩm được Operation phân công cho Convenience cấu hình.</p>
        </div>

        <button
          type="button"
          className={`${PREFIX}-refresh`}
          onClick={loadProductTours}
          disabled={loading}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />
          Làm mới
        </button>
      </div>

      {error && (
        <div className={`${PREFIX}-error`}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <TourCompleteBox
        prefix={PREFIX}
        noun="sản phẩm"
        tourSummaries={tourSummaries}
        completing={completing}
        completeError={completeError}
        onComplete={completeTourConfiguration}
      />

      <StatusTabs
        prefix={PREFIX}
        statuses={STATUSES}
        value={statusFilter}
        onChange={setStatusFilter}
      />

      {filteredTours.length === 0 && !error ? (
        <div className={`${PREFIX}-empty`}>
          <Package size={32} />
          <h3>Không có sản phẩm nào</h3>
          <p>Không có sản phẩm nào khớp với bộ lọc hiện tại.</p>
        </div>
      ) : (
        <div className={`${PREFIX}-table-wrapper`}>
          <table className={`${PREFIX}-table`}>
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
              {filteredTours.map((assignment, index) => (
                <tr key={assignment.id}>
                  <td>{index + 1}</td>

                  <td>
                    <TourCell
                      prefix={PREFIX}
                      assignment={assignment}
                      onView={setViewDetailAssignment}
                    />
                  </td>

                  <td>
                    <AreaCell
                      prefix={PREFIX}
                      assignment={assignment}
                      onView={setViewDetailAssignment}
                    />
                  </td>

                  <td>
                    <div className={`${PREFIX}-product`}>
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

                  <td>
                    {assignment.quantity != null ? assignment.quantity : "—"}
                  </td>

                  <td>
                    <span
                      className={`${PREFIX}-status ${String(
                        assignment.status || "",
                      ).toLowerCase()}`}
                    >
                      {getStatusLabel(assignment.status)}
                    </span>
                  </td>

                  <td>
                    <div
                      style={{
                        display: "flex",
                        gap: "8px",
                        justifyContent: "center",
                      }}
                    >
                      <ConfigAction
                        prefix={PREFIX}
                        assignment={assignment}
                        onOpen={setSelectedAssignment}
                      />
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selectedAssignment && (
        <ProductTourConfigModal
          assignment={selectedAssignment}
          onClose={handleCloseModal}
          onSubmit={handleSubmit}
          submitting={loading}
        />
      )}

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
