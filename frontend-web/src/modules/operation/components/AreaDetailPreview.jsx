import React, { useState, useEffect, useMemo } from "react";
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
  Trash2,
  CheckCircle2,
} from "lucide-react";
import "../styles/AreaDetailPreview.css";

const CONFIG_LABEL_MAP = {
  ACTIVITY: "Hoạt động trên tàu",
  SERVICE: "Dịch vụ",
  PRODUCT: "Sản phẩm / Phòng",
};

const CONFIG_OPTIONS = [
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

function AreaDetailPreview({
  area,
  isAssigned: isAssignedProp,
  assignedType,
  selectedConfigType,
  onChangeConfigType,
  onSaveAssignment,
  onUnassign,
  loading = false,
}) {
  const [currentImageIdx, setCurrentImageIdx] = useState(0);

  // Reset slider chỉ số ảnh khi đổi khu vực/phòng
  useEffect(() => {
    setCurrentImageIdx(0);
  }, [area]);

  if (!area) return null;

  const isRoom = area?._type === "ROOM" || area?.type === "ROOM";

  // Kiểm tra trạng thái gán
  const isAssigned = Boolean(
    isAssignedProp ||
    area?.isAssigned ||
    area?.assigned ||
    area?.assignmentId ||
    area?.status === "ASSIGNED",
  );

  // Trích xuất danh sách ảnh đã chuẩn hóa
  const images = useMemo(() => {
    const rawList =
      area.images ||
      area.imageUrl ||
      area.imageList ||
      area.photos ||
      area.image ||
      [];

    const list = Array.isArray(rawList) ? rawList : [rawList];
    return list
      .map((img) => (typeof img === "string" ? img : img?.url || img?.path))
      .filter(Boolean);
  }, [area]);

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

  const currentImgUrl = images[currentImageIdx] || images[0];
  const displayAssignedType =
    assignedType ||
    selectedConfigType ||
    area?.assignedType ||
    area?.configType;
  const assignedLabel =
    CONFIG_LABEL_MAP[displayAssignedType] || "Chưa xác định loại hình";

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

      {/* GALLERY / SLIDER */}
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

      {/* THUMBNAILS */}
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

      {/* PHÂN NHÁNH TRẠNG THÁI GÁN */}
      {isAssigned ? (
        /* TRƯỜNG HỢP 1: ĐÃ PHÂN CÔNG */
        <div className="adp-assigned-section">
          <div className="adp-status-box assigned-box">
            <div className="adp-status-header">
              <CheckCircle2 size={18} color="#16a34a" />
              <span className="assigned-title">Đã phân công vào Tour</span>
            </div>

            <div className="adp-info-row" style={{ marginTop: "12px" }}>
              <span
                className="value font-semibold"
                style={{ color: "#2563eb", fontWeight: "600" }}
              >
                {assignedLabel}
              </span>
            </div>
          </div>

          <div className="adp-actions" style={{ marginTop: "16px" }}>
            <button
              type="button"
              className="adp-unassign-btn"
              onClick={() => onUnassign?.(area)}
              disabled={loading}
            >
              {loading ? (
                <>
                  <Loader2 size={18} className="adp-spinner" />
                  <span>Đang xử lý...</span>
                </>
              ) : (
                <>
                  <Trash2 size={18} />
                  <span>Hủy gán khỏi Tour</span>
                </>
              )}
            </button>
          </div>
        </div>
      ) : (
        /* TRƯỜNG HỢP 2: CHƯA PHÂN CÔNG */
        <>
          <div className="adp-config-section">
            <div className="adp-section-title">
              <Tag size={15} />
              <span>Chọn loại hình gán vào Tour</span>
            </div>

            <div className="adp-config-options">
              {CONFIG_OPTIONS.map((option) => {
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
              disabled={loading || !selectedConfigType}
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
