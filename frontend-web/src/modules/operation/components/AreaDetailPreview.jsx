import React, { useState, useEffect, useMemo, useCallback } from "react";
import {
  MapPin,
  DoorClosed,
  Layers,
  ChevronLeft,
  ChevronRight,
  Check,
  Loader2,
  Tag,
  Info,
  ImageOff,
  UserCheck,
  Bookmark,
} from "lucide-react";
import "../styles/AreaDetailPreview.css";

const BASE_URL = "http://localhost:8080";

function AreaDetailPreview({
  area,
  selectedConfigType,
  onChangeConfigType,
  onSaveAssignment,
  loading,
}) {
  const [currentImageIdx, setCurrentImageIdx] = useState(0);

  // Reset index ảnh khi thay đổi area
  useEffect(() => {
    setCurrentImageIdx(0);
  }, [area]);

  const isRoom = area?._type === "ROOM";

  // 1. Hàm chuẩn hóa URL hình ảnh
  const formatImageUrl = useCallback((img) => {
    if (!img) return null;

    let url = "";
    if (typeof img === "string") url = img;
    else if (typeof img === "object" && img.url) url = img.url;
    else if (typeof img === "object" && img.path) url = img.path;

    if (!url) return null;

    if (
      url.startsWith("http://") ||
      url.startsWith("https://") ||
      url.startsWith("data:") ||
      url.startsWith("blob:")
    ) {
      return url;
    }

    const cleanPath = url.startsWith("/") ? url : `/${url}`;
    return `${BASE_URL}${cleanPath}`;
  }, []);

  // 2. Trích xuất toàn bộ ảnh
  const images = useMemo(() => {
    if (!area) return [];

    const rawList =
      area.images ||
      area.imageUrl ||
      area.imageList ||
      area.photos ||
      area.image ||
      [];

    const list = Array.isArray(rawList) ? rawList : [rawList];
    return list.map(formatImageUrl).filter(Boolean);
  }, [area, formatImageUrl]);

  const handleNextImage = (e) => {
    e.stopPropagation();
    if (images.length === 0) return;
    setCurrentImageIdx((prev) => (prev + 1) % images.length);
  };

  const handlePrevImage = (e) => {
    e.stopPropagation();
    if (images.length === 0) return;
    setCurrentImageIdx((prev) => (prev - 1 + images.length) % images.length);
  };

  if (!area) return null;

  const configOptions = [
    {
      type: "ACTIVITY",
      label: "Hoạt động (Activity)",
      desc: "Tổ chức sự kiện, vui chơi, hoạt động nhóm.",
    },
    {
      type: "SERVICE",
      label: "Dịch vụ (Service)",
      desc: "Khu vực Spa, Nhà hàng, Quầy bar, Gym...",
    },
    {
      type: "PRODUCT",
      label: "Sản phẩm / Phòng (Product)",
      desc: "Gán phòng nghỉ hoặc dịch vụ lưu trú cụ thể.",
    },
  ];

  const currentImgUrl = images[currentImageIdx] || images[0];

  return (
    <div className="adp-container">
      {/* HEADER */}
      <div className="adp-header">
        <div className="adp-type-badge">
          {isRoom ? (
            <>
              <DoorClosed size={14} /> <span>Phòng nghỉ</span>
            </>
          ) : (
            <>
              <MapPin size={14} /> <span>Khu vực chung</span>
            </>
          )}
        </div>
        <h3 className="adp-title">
          {area.name || area.roomNumber || "Chi tiết"}
        </h3>
        {area.code && <span className="adp-code">Mã: {area.code}</span>}
      </div>

      {/* KHU VỰC HIỂN THỊ ẢNH (CAROUSEL SLIDER) */}
      <div className="adp-gallery">
        {images.length > 0 ? (
          <div className="adp-slider">
            <img
              src={currentImgUrl}
              alt={`${area.name || "Preview"} - ${currentImageIdx + 1}`}
              className="adp-image"
              onError={(e) => {
                e.target.src = "https://placehold.co/600x400?text=Loi+Anh";
              }}
            />

            <span className="adp-image-counter">
              {currentImageIdx + 1} / {images.length}
            </span>

            {images.length > 1 && (
              <>
                <button
                  type="button"
                  className="adp-nav-btn prev"
                  onClick={handlePrevImage}
                >
                  <ChevronLeft size={18} />
                </button>
                <button
                  type="button"
                  className="adp-nav-btn next"
                  onClick={handleNextImage}
                >
                  <ChevronRight size={18} />
                </button>

                <div className="adp-dots">
                  {images.map((_, idx) => (
                    <span
                      key={idx}
                      className={`dot ${
                        idx === currentImageIdx ? "active" : ""
                      }`}
                      onClick={() => setCurrentImageIdx(idx)}
                    />
                  ))}
                </div>
              </>
            )}
          </div>
        ) : (
          <div className="adp-no-image">
            <ImageOff size={36} />
            <span>Chưa có hình ảnh</span>
          </div>
        )}
      </div>

      {/* DANH SÁCH ẢNH NHỎ (THUMBNAILS) */}
      {images.length > 1 && (
        <div className="adp-thumbnails">
          {images.map((imgUrl, idx) => (
            <div
              key={idx}
              className={`adp-thumb-item ${
                idx === currentImageIdx ? "active" : ""
              }`}
              onClick={() => setCurrentImageIdx(idx)}
            >
              <img src={imgUrl} alt={`thumb-${idx}`} />
            </div>
          ))}
        </div>
      )}

      {/* THÔNG TIN CHI TIẾT CHUNG */}
      <div className="adp-info-section">
        <div className="adp-info-row">
          <span className="label">
            <Layers size={14} /> Vị trí tầng:
          </span>
          <span className="value">{area._deckName || "Chưa xác định"}</span>
        </div>

        {area.capacity && (
          <div className="adp-info-row">
            <span className="label">
              <Info size={14} /> Sức chứa:
            </span>
            <span className="value">{area.capacity} người</span>
          </div>
        )}

        {area.description && (
          <div className="adp-description">
            <p>{area.description}</p>
          </div>
        )}
      </div>

      {/* PHÂN NHÁNH GIAO DIỆN KHU VỰC VS PHÒNG */}
      {isRoom ? (
        /* --- GIAO DIỆN DÀNH CHUYÊN CHO PHÒNG NGHỈ (TĨNH) --- */
        <div className="adp-room-status-section">
          <div className="adp-section-title">
            <Bookmark size={15} />
            <span>Trạng thái đặt phòng (Booking)</span>
          </div>

          <div className="adp-status-box occupied">
            <div className="adp-info-row">
              <span className="label">Trạng thái:</span>
              <span className="status-badge occupied-badge">Đã đặt phòng</span>
            </div>

            <div className="adp-info-row">
              <span className="label">Mã Booking ID:</span>
              <span className="value code-highlight">BK-2026-8892</span>
            </div>

            <div className="adp-info-row">
              <span className="label">
                <UserCheck size={14} /> Người ở hiện tại:
              </span>
              <span className="value">Nguyễn Văn A (Khách đoàn)</span>
            </div>
          </div>
        </div>
      ) : (
        /* --- GIAO DIỆN DÀNH CHO KHU VỰC CHUNG (CÓ CẤU HÌNH & LƯU) --- */
        <>
          <div className="adp-config-section">
            <div className="adp-section-title">
              <Tag size={15} />
              <span>Chọn loại hình gán vào Tour</span>
            </div>

            <div className="adp-config-options">
              {configOptions.map((option) => {
                const isChecked = selectedConfigType === option.type;
                return (
                  <div
                    key={option.type}
                    className={`adp-config-item ${isChecked ? "selected" : ""}`}
                    onClick={() => onChangeConfigType?.(option.type)}
                  >
                    <div className="adp-radio">
                      {isChecked && <div className="adp-radio-inner" />}
                    </div>
                    <div className="adp-config-content">
                      <div className="adp-config-label">{option.label}</div>
                      <div className="adp-config-desc">{option.desc}</div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="adp-actions">
            <button
              type="button"
              className="adp-save-btn"
              onClick={onSaveAssignment}
              disabled={loading}
            >
              {loading ? (
                <>
                  <Loader2 size={18} className="adp-spinner" />
                  <span>Đang lưu...</span>
                </>
              ) : (
                <>
                  <Check size={18} />
                  <span>Xác nhận phân công</span>
                </>
              )}
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default AreaDetailPreview;
