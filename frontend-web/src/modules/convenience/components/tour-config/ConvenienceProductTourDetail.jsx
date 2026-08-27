// src/modules/convenience/components/tour-config/ConvenienceProductTourDetail.jsx

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { AlertCircle, Eye, Package, RefreshCw } from "lucide-react";
import "../../styles/tour-config/ConvenienceProductTourDetail.css";
import productTourService from "../../services/productTourService";
import ProductTourDetailModal from "./ProductTourDetailModal";

const STATUS_TABS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "NOT_STARTED", label: "Đã cấu hình" },
  { value: "IN_PROGRESS", label: "Đang phục vụ" },
  { value: "OUT_OF_STOCK", label: "Hết hàng" },
  { value: "COMPLETED", label: "Đã kết thúc" },
];

const ConvenienceProductTourDetail = () => {
  const [productTours, setProductTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [viewDetailAssignment, setViewDetailAssignment] = useState(null);

  const loadProductTours = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await productTourService.getConfigurationHistory();

      setProductTours(data || []);
    } catch (err) {
      console.error("LOAD PRODUCT TOUR HISTORY ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải lịch sử cấu hình sản phẩm.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProductTours();
  }, [loadProductTours]);

  const filteredProductTours = useMemo(() => {
    if (statusFilter === "ALL") {
      return productTours;
    }

    return productTours.filter((item) => item.status === statusFilter);
  }, [productTours, statusFilter]);

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

  if (loading && productTours.length === 0) {
    return (
      <div className="convenience-product-tour-detail-loading">
        <RefreshCw size={20} className="spin" />
        <span>Đang tải lịch sử sản phẩm...</span>
      </div>
    );
  }

  return (
    <>
      {/* HEADER */}
      <div className="convenience-product-tour-detail-toolbar">
        <div>
          <h2>
            <Package size={20} />
            Lịch sử sản phẩm Tour
          </h2>

          <p>Danh sách các sản phẩm đã được cấu hình cho các Tour.</p>
        </div>

        <button
          type="button"
          className="convenience-product-tour-detail-refresh"
          onClick={loadProductTours}
          disabled={loading}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />
          Làm mới
        </button>
      </div>

      {/* ERROR */}
      {error && (
        <div className="convenience-product-tour-detail-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* FILTER */}
      <div className="convenience-product-tour-detail-filters">
        {STATUS_TABS.map((tab) => (
          <button
            key={tab.value}
            type="button"
            className={
              statusFilter === tab.value
                ? "convenience-product-tour-detail-filter active"
                : "convenience-product-tour-detail-filter"
            }
            onClick={() => setStatusFilter(tab.value)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* EMPTY */}
      {filteredProductTours.length === 0 && !error ? (
        <div className="convenience-product-tour-detail-empty">
          <Package size={32} />

          <h3>Chưa có dữ liệu</h3>

          <p>Không có sản phẩm nào phù hợp với trạng thái đang chọn.</p>
        </div>
      ) : (
        <div className="convenience-product-tour-detail-table-wrapper">
          <table className="convenience-product-tour-detail-table">
            <thead>
              <tr>
                <th>STT</th>
                <th>Mã Tour</th>
                <th>Tên Tour</th>
                <th>Khu vực</th>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
                <th>Trạng thái</th>
                <th>Chi tiết</th>
              </tr>
            </thead>

            <tbody>
              {filteredProductTours.map((item, index) => (
                <tr key={item.id}>
                  <td>{index + 1}</td>

                  <td>
                    <span className="font-mono">
                      {item.tourCode || item.tourId || "—"}
                    </span>
                  </td>

                  <td>{item.tourName || "—"}</td>

                  <td>
                    {item.cruiseAreaName || item.cruiseAreaId || "—"}

                    {item.deckNumber != null && (
                      <div>Tầng {item.deckNumber}</div>
                    )}
                  </td>

                  <td>
                    <div>
                      <strong>
                        {item.productCode || item.productId || "—"}
                      </strong>

                      {item.productName && <div>{item.productName}</div>}
                    </div>
                  </td>

                  <td>{item.quantity ?? "—"}</td>

                  <td>
                    <span
                      className={`convenience-product-tour-detail-status ${String(
                        item.status || "",
                      ).toLowerCase()}`}
                    >
                      {getStatusLabel(item.status)}
                    </span>
                  </td>

                  <td>
                    <button
                      type="button"
                      className="convenience-product-tour-detail-view"
                      onClick={() => setViewDetailAssignment(item)}
                      title="Xem chi tiết"
                    >
                      <Eye size={15} />
                      Xem
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* DETAIL */}
      {viewDetailAssignment && (
        <ProductTourDetailModal
          assignmentId={viewDetailAssignment.id}
          onClose={() => setViewDetailAssignment(null)}
        />
      )}
    </>
  );
};

export default ConvenienceProductTourDetail;
