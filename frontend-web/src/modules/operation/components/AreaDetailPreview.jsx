import React from "react";
import {
  Image,
  Layers,
  Package,
  Bell,
  Activity,
  CheckCircle,
} from "lucide-react";
import "../styles/AreaDetailPreview.css";

function AreaDetailPreview({
  area,
  selectedConfigType,
  onChangeConfigType,
  onSaveAssignment, // Hàm gọi khi bấm nút Phân công ở cột phải
  isSaving = false,
}) {
  if (!area) {
    return (
      <div className="area-preview-empty">
        <Image size={40} className="icon-placeholder" />
        <p>Vui lòng chọn một khu vực để xem chi tiết và phân công</p>
      </div>
    );
  }

  const { name, description, imageUrl, _deckName } = area;

  const configOptions = [
    { id: "PRODUCT", label: "Sản phẩm", icon: Package },
    { id: "SERVICE", label: "Dịch vụ", icon: Bell },
    { id: "ACTIVITY", label: "Hoạt động", icon: Activity },
  ];

  return (
    <div className="area-preview-card">
      {/* 1. KHUNG ẢNH KHU VỰC */}
      <div className="area-preview-image-wrapper">
        {imageUrl ? (
          <img src={imageUrl} alt={name} className="area-preview-img" />
        ) : (
          <div className="area-preview-no-img">
            <Image size={32} />
            <span>Chưa có hình ảnh khu vực</span>
          </div>
        )}
        {_deckName && (
          <span className="area-preview-deck-badge">
            <Layers size={13} /> {_deckName}
          </span>
        )}
      </div>

      {/* 2. THÔNG TIN KHU VỰC */}
      <div className="area-preview-info">
        <h3 className="area-preview-title">{name}</h3>
        {description && <p className="area-preview-desc">{description}</p>}
      </div>

      {/* 3. THANH CHỌN LOẠI HÌNH & NÚT PHÂN CÔNG */}
      <div className="area-preview-config">
        <label className="config-label">
          Phân công loại hình cho khu vực này:
        </label>

        <div className="segmented-control">
          {configOptions.map((opt) => {
            const IconComponent = opt.icon;
            const isSelected = selectedConfigType === opt.id;

            return (
              <button
                key={opt.id}
                type="button"
                className={`segmented-item ${isSelected ? "active" : ""}`}
                onClick={() => onChangeConfigType(opt.id)}
              >
                <IconComponent size={16} className="segmented-icon" />
                <span>{opt.label}</span>
              </button>
            );
          })}
        </div>

        {/* NÚT PHÂN CÔNG NẰM NGAY DƯỚI 3 NÚT CHỌN */}
        <button
          type="button"
          className="btn-assign-submit"
          disabled={!selectedConfigType || isSaving}
          onClick={onSaveAssignment}
        >
          <CheckCircle size={16} />
          <span>{isSaving ? "Đang lưu..." : "Phân công khu vực này"}</span>
        </button>
      </div>
    </div>
  );
}

export default AreaDetailPreview;
