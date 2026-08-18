import React, { useState, useEffect } from "react";
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

  useEffect(() => {
    setCurrentImageIdx(0);
    console.log("==> [LOG 1] Dữ liệu area truyền vào:", area);
  }, [area]);

  if (!area) return null;

  const isRoom = area._type === "ROOM";

  // 1. Hàm chuẩn hóa URL hình ảnh
  const formatImageUrl = (img) => {
    if (!img) return null;

    let url = "";
    if (typeof img === "string") url = img;
    else if (typeof img === "object" && img.url) url = img.url;
    else if (typeof img === "object" && img.path) url = img.path;

    if (!url) return null;

    // Giữ nguyên 100% nếu là link Cloudinary/HTTP/HTTPS
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    const cleanPath = url.startsWith("/") ? url : `/${url}`;
    return `${BASE_URL}${cleanPath}`;
  };

  // 2. Trích xuất toàn bộ ảnh (Khai báo TRƯỚC khi gọi)
  const extractImages = () => {
    const rawList =
      area.images ||
      area.imageUrl ||
      area.imageList ||
      area.photos ||
      area.image ||
      [];

    const list = Array.isArray(rawList) ? rawList : [rawList];
    const result = list.map(formatImageUrl).filter(Boolean);

    console.log("==> [LOG 2] Mảng URL ảnh trích xuất thành công:", result);

    return result;
  };

  // 3. Thực thi lấy danh sách ảnh sau khi hàm đã khai báo xong
  const images = extractImages();

  const handleNextImage = (e) => {
    e.stopPropagation();
    setCurrentImageIdx((prev) => (prev + 1) % images.length);
  };

  const handlePrevImage = (e) => {
    e.stopPropagation();
    setCurrentImageIdx((prev) => (prev - 1 + images.length) % images.length);
  };

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
              src={images[currentImageIdx]}
              alt={`${area.name || "Preview"} - ${currentImageIdx + 1}`}
              className="adp-image"
              onError={(e) => {
                console.error(
                  "==> [LOG 3] Lỗi tải ảnh từ URL:",
                  images[currentImageIdx],
                );
                e.target.src = "https://placehold.co/600x400?text=Loi+Annh";
              }}
            />

            {/* Đếm số lượng ảnh */}
            <span className="adp-image-counter">
              {currentImageIdx + 1} / {images.length}
            </span>

            {/* Nút chuyển ảnh nếu có từ 2 ảnh trở lên */}
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

                {/* Dots indicator */}
                <div className="adp-dots">
                  {images.map((_, idx) => (
                    <span
                      key={idx}
                      className={`dot ${idx === currentImageIdx ? "active" : ""}`}
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
            <span>Khu vực này chưa có hình ảnh</span>
          </div>
        )}
      </div>

      {/* DANH SÁCH ẢNH NHỎ (THUMBNAILS) NẾU CÓ NHIỀU ẢNH */}
      {images.length > 1 && (
        <div className="adp-thumbnails">
          {images.map((imgUrl, idx) => (
            <div
              key={idx}
              className={`adp-thumb-item ${idx === currentImageIdx ? "active" : ""}`}
              onClick={() => setCurrentImageIdx(idx)}
            >
              <img src={imgUrl} alt={`thumb-${idx}`} />
            </div>
          ))}
        </div>
      )}

      {/* THÔNG TIN CHI TIẾT */}
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

      {/* CHỌN CẤU HÌNH LOẠI HÌNH */}
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

      {/* HÀNH ĐỘNG HÀNG ĐẦU */}
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
    </div>
  );
}

export default AreaDetailPreview;
