// src/modules/convenience/tour-config/ProductTourDetailModal.jsx
import React, { useEffect, useState } from "react";
import { AlertCircle, MapPin, Package, RefreshCw, Ship, X } from "lucide-react";

import useProductTour from "../../hooks/useProductTour";
import "../../styles/history/ProductTourDetailModal.css";

const ProductTourDetailModal = ({ assignmentId, onClose }) => {
  const { getProductTourById } = useProductTour();

  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const fetchDetail = async () => {
      if (!assignmentId) return;

      setLoading(true);
      setError(null);

      try {
        if (typeof getProductTourById === "function") {
          const res = await getProductTourById(assignmentId);
          if (isMounted) setDetail(res);
        } else {
          setDetail(null);
        }
      } catch (err) {
        if (isMounted) {
          setError(
            err?.message || "Không thể tải thông tin chi tiết sản phẩm.",
          );
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchDetail();

    return () => {
      isMounted = false;
    };
  }, [assignmentId, getProductTourById]);

  // =====================================================
  // STATUS LABEL
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

  if (!assignmentId) return null;

  const handleOverlayClick = () => {
    onClose();
  };

  const stopPropagation = (event) => {
    event.stopPropagation();
  };

  return (
    <div className="product-tour-detail-overlay" onClick={handleOverlayClick}>
      <div className="product-tour-detail-modal" onClick={stopPropagation}>
        {/* HEADER */}
        <div className="product-tour-detail-header">
          <h3>
            <Package size={18} />
            Chi tiết phân công sản phẩm
          </h3>
          <button
            type="button"
            className="product-tour-detail-close"
            onClick={onClose}
            title="Đóng"
          >
            <X size={18} />
          </button>
        </div>

        {/* BODY */}
        <div className="product-tour-detail-body">
          {loading ? (
            <div className="product-tour-detail-loading">
              <RefreshCw size={20} className="spin" />
              <span>Đang tải thông tin...</span>
            </div>
          ) : error ? (
            <div className="product-tour-detail-error">
              <AlertCircle size={18} />
              <span>{error}</span>
            </div>
          ) : (
            <>
              {/* TOUR */}
              <section className="product-tour-detail-section">
                <h4>
                  <Ship size={15} />
                  Thông tin Tour
                </h4>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">Mã Tour</span>
                  <span className="product-tour-detail-value font-mono">
                    {detail?.tourCode || detail?.tourId || "—"}
                  </span>
                </div>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">Tên Tour</span>
                  <span className="product-tour-detail-value">
                    {detail?.tourName || "—"}
                  </span>
                </div>
              </section>

              {/* AREA */}
              <section className="product-tour-detail-section">
                <h4>
                  <MapPin size={15} />
                  Khu vực
                </h4>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">Khu vực</span>
                  <span className="product-tour-detail-value font-mono">
                    {detail?.cruiseAreaName || detail?.cruiseAreaId || "—"}
                  </span>
                </div>

                {detail?.deckNumber != null && (
                  <div className="product-tour-detail-row">
                    <span className="product-tour-detail-label">Tầng</span>
                    <span className="product-tour-detail-value">
                      Tầng {detail.deckNumber}
                    </span>
                  </div>
                )}
              </section>

              {/* PRODUCT */}
              <section className="product-tour-detail-section">
                <h4>
                  <Package size={15} />
                  Sản phẩm
                </h4>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">Mã sản phẩm</span>
                  <span className="product-tour-detail-value font-mono">
                    {detail?.productCode ||
                      (detail?.productId ? `PRD-${detail.productId}` : "—")}
                  </span>
                </div>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">
                    Tên sản phẩm
                  </span>
                  <span className="product-tour-detail-value">
                    {detail?.productName || "—"}
                  </span>
                </div>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">
                    Số lượng phân công
                  </span>
                  <span className="product-tour-detail-value">
                    {detail?.quantity ?? "—"}
                  </span>
                </div>

                <div className="product-tour-detail-row">
                  <span className="product-tour-detail-label">Trạng thái</span>
                  <span
                    className={`product-tour-detail-status ${String(
                      detail?.status || "",
                    ).toLowerCase()}`}
                  >
                    {getStatusLabel(detail?.status)}
                  </span>
                </div>

                {detail?.description && (
                  <div className="product-tour-detail-row col">
                    <span className="product-tour-detail-label">
                      Ghi chú / Mô tả
                    </span>
                    <p className="product-tour-detail-desc">
                      {detail.description}
                    </p>
                  </div>
                )}
              </section>
            </>
          )}
        </div>

        {/* FOOTER */}
        <div className="product-tour-detail-footer">
          <button
            type="button"
            className="product-tour-detail-close-btn"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductTourDetailModal;
