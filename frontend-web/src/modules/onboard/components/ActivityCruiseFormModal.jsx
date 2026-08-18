import React, { useState, useEffect } from "react";
import { X, Upload } from "lucide-react";
import "../styles/ActivityCruise.css";

const initialFormState = {
  name: "",
  description: "",
  status: "ACTIVE",
  image: null,
};

function ActivityCruiseFormModal({ isOpen, onClose, onSubmit, initialData }) {
  const [formData, setFormData] = useState(initialFormState);
  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name || "",
        description: initialData.description || "",
        status: initialData.status || "ACTIVE",
        image: null,
      });
      setImagePreview(initialData.imageUrl || null);
    } else {
      setFormData(initialFormState);
      setImagePreview(null);
    }
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setFormData((prev) => ({ ...prev, image: file }));
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <div className="onboard-activity-modal-overlay">
      <div className="onboard-activity-modal">
        <div className="onboard-activity-modal-header">
          <h3>{initialData ? "Cập nhật Hoạt động" : "Thêm Hoạt động Mới"}</h3>
          <button
            type="button"
            className="onboard-activity-modal-close"
            onClick={onClose}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="onboard-activity-modal-body">
          <div className="onboard-activity-form-group">
            <label>Tên hoạt động *</label>
            <input
              type="text"
              name="name"
              required
              value={formData.name}
              onChange={handleChange}
              placeholder="VD: Tiệc đón khách Sunset Party"
            />
          </div>

          <div className="onboard-activity-form-group">
            <label>Trạng thái</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
            >
              <option value="ACTIVE">Kích hoạt (ACTIVE)</option>
              <option value="INACTIVE">Tạm dừng (INACTIVE)</option>
            </select>
          </div>

          <div className="onboard-activity-form-group">
            <label>Hình ảnh hoạt động</label>
            <div className="onboard-activity-image-upload">
              <input
                type="file"
                accept="image/*"
                id="activity-image-input"
                onChange={handleImageChange}
                style={{ display: "none" }}
              />
              <label
                htmlFor="activity-image-input"
                className="onboard-activity-upload-btn"
              >
                <Upload size={16} /> Chọn ảnh
              </label>
              {imagePreview && (
                <div className="onboard-activity-preview-container">
                  <img
                    src={imagePreview}
                    alt="Preview"
                    className="onboard-activity-image-preview"
                  />
                </div>
              )}
            </div>
          </div>

          <div className="onboard-activity-form-group">
            <label>Mô tả chi tiết</label>
            <textarea
              name="description"
              rows={3}
              value={formData.description}
              onChange={handleChange}
              placeholder="Nhập ghi chú hoặc mô tả hoạt động..."
            />
          </div>

          <div className="onboard-activity-modal-footer">
            <button
              type="button"
              className="onboard-activity-btn cancel"
              onClick={onClose}
            >
              Hủy
            </button>
            <button type="submit" className="onboard-activity-btn submit">
              {initialData ? "Lưu thay đổi" : "Tạo mới"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ActivityCruiseFormModal;
