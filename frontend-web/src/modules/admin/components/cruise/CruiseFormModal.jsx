// src/modules/admin/components/cruise/CruiseFormModal.jsx

import { useEffect, useRef, useState } from "react";
import { Alert, Modal, Spinner } from "react-bootstrap";
import { ImagePlus, Ship, X } from "lucide-react";

const MAX_IMAGE_MB = 5;

const EMPTY_FORM = {
  name: "",
  code: "",
  description: "",
  maxPassengers: "",
  image: null,
  status: "ACTIVE",
};

export default function CruiseFormModal({
  show,
  cruise = null,
  saving,
  error,
  onClose,
  onSubmit,
}) {
  const isEditing = Boolean(cruise);

  const [form, setForm] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});
  const [previewUrl, setPreviewUrl] = useState(null);
  const [dragging, setDragging] = useState(false);
  const fileInputRef = useRef(null);

  // Mỗi lần mở modal: nạp lại dữ liệu
  useEffect(() => {
    if (!show) return;

    setForm(
      cruise
        ? {
            name: cruise.name || "",
            code: cruise.code || "",
            description: cruise.description || "",
            maxPassengers: cruise.maxPassengers ?? "",
            image: null,
            status: cruise.status || "ACTIVE",
          }
        : EMPTY_FORM,
    );
    setErrors({});
  }, [show, cruise]);

  // Ảnh xem trước cho file vừa chọn
  useEffect(() => {
    if (!form.image) {
      setPreviewUrl(null);
      return;
    }

    const url = URL.createObjectURL(form.image);
    setPreviewUrl(url);

    return () => URL.revokeObjectURL(url);
  }, [form.image]);

  const shownImage = previewUrl || (isEditing ? cruise.imageUrl : null);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({ ...previous, [name]: value }));
    setErrors((previous) => ({ ...previous, [name]: undefined }));
  };

  const handleFile = (file) => {
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setErrors((previous) => ({
        ...previous,
        image: "Vui lòng chọn file hình ảnh.",
      }));
      return;
    }

    if (file.size > MAX_IMAGE_MB * 1024 * 1024) {
      setErrors((previous) => ({
        ...previous,
        image: `Ảnh không được vượt quá ${MAX_IMAGE_MB}MB.`,
      }));
      return;
    }

    setForm((previous) => ({ ...previous, image: file }));
    setErrors((previous) => ({ ...previous, image: undefined }));
  };

  const clearImage = () => {
    setForm((previous) => ({ ...previous, image: null }));
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const validate = () => {
    const next = {};

    if (!form.name.trim()) next.name = "Vui lòng nhập tên du thuyền.";
    if (!form.code.trim()) next.code = "Vui lòng nhập mã du thuyền.";

    if (!form.maxPassengers || Number(form.maxPassengers) < 1) {
      next.maxPassengers = "Sức chứa phải lớn hơn 0.";
    }

    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!validate()) return;

    const formData = new FormData();

    formData.append("name", form.name.trim());
    formData.append("code", form.code.trim());
    formData.append("description", form.description.trim());
    formData.append("maxPassengers", Number(form.maxPassengers));

    if (form.image) formData.append("image", form.image);
    if (isEditing) formData.append("status", form.status);

    onSubmit(formData);
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      size="lg"
      contentClassName="mc-modal"
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <form onSubmit={handleSubmit} noValidate>
        <Modal.Header closeButton={!saving}>
          <span className="mc-modal-icon">
            <Ship size={20} />
          </span>

          <div>
            <Modal.Title>
              {isEditing ? "Chỉnh sửa du thuyền" : "Thêm du thuyền"}
            </Modal.Title>
            <p>
              {isEditing
                ? "Cập nhật thông tin và trạng thái của du thuyền."
                : "Nhập thông tin cơ bản, bạn có thể thêm tầng và phòng sau."}
            </p>
          </div>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <div className="mc-form">
            {/* ẢNH */}
            <div className="mc-upload">
              <span className="mc-label">Hình ảnh</span>

              <button
                type="button"
                className={`mc-upload-zone ${shownImage ? "has-image" : ""} ${
                  dragging ? "is-dragging" : ""
                }`}
                onClick={() => fileInputRef.current?.click()}
                onDragOver={(event) => {
                  event.preventDefault();
                  setDragging(true);
                }}
                onDragLeave={() => setDragging(false)}
                onDrop={(event) => {
                  event.preventDefault();
                  setDragging(false);
                  handleFile(event.dataTransfer.files?.[0]);
                }}
                disabled={saving}
              >
                {shownImage ? (
                  <img src={shownImage} alt="Ảnh du thuyền" />
                ) : (
                  <>
                    <ImagePlus size={32} />
                    <strong>Chọn ảnh du thuyền</strong>
                    <span>
                      Kéo thả hoặc bấm để chọn · tối đa {MAX_IMAGE_MB}MB
                    </span>
                  </>
                )}
              </button>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                hidden
                onChange={(event) => handleFile(event.target.files?.[0])}
              />

              {form.image && (
                <div className="mc-upload-file">
                  <span title={form.image.name}>{form.image.name}</span>
                  <button
                    type="button"
                    onClick={clearImage}
                    aria-label="Bỏ ảnh đã chọn"
                  >
                    <X size={15} />
                  </button>
                </div>
              )}

              {errors.image && <span className="mc-error">{errors.image}</span>}
            </div>

            {/* THÔNG TIN */}
            <div className="mc-fields">
              <div className="mc-field">
                <label className="mc-label" htmlFor="cruise-name">
                  Tên du thuyền <b>*</b>
                </label>
                <input
                  id="cruise-name"
                  className={`mc-input ${errors.name ? "is-invalid" : ""}`}
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Ví dụ: Ocean Star"
                  maxLength={150}
                  disabled={saving}
                />
                {errors.name && <span className="mc-error">{errors.name}</span>}
              </div>

              <div className="mc-row">
                <div className="mc-field">
                  <label className="mc-label" htmlFor="cruise-code">
                    Mã du thuyền <b>*</b>
                  </label>
                  <input
                    id="cruise-code"
                    className={`mc-input ${errors.code ? "is-invalid" : ""}`}
                    name="code"
                    value={form.code}
                    onChange={handleChange}
                    placeholder="CRUISE-001"
                    maxLength={50}
                    disabled={saving}
                  />
                  {errors.code ? (
                    <span className="mc-error">{errors.code}</span>
                  ) : (
                    <span className="mc-hint">Mã phải là duy nhất.</span>
                  )}
                </div>

                <div className="mc-field">
                  <label className="mc-label" htmlFor="cruise-capacity">
                    Sức chứa tối đa <b>*</b>
                  </label>
                  <input
                    id="cruise-capacity"
                    className={`mc-input ${errors.maxPassengers ? "is-invalid" : ""}`}
                    type="number"
                    name="maxPassengers"
                    value={form.maxPassengers}
                    onChange={handleChange}
                    placeholder="Số hành khách"
                    min="1"
                    disabled={saving}
                  />
                  {errors.maxPassengers && (
                    <span className="mc-error">{errors.maxPassengers}</span>
                  )}
                </div>
              </div>

              {isEditing && (
                <div className="mc-field">
                  <label className="mc-label" htmlFor="cruise-status">
                    Trạng thái
                  </label>
                  <select
                    id="cruise-status"
                    className="mc-input"
                    name="status"
                    value={form.status}
                    onChange={handleChange}
                    disabled={saving}
                  >
                    <option value="ACTIVE">Hoạt động</option>
                    <option value="INACTIVE">Ngừng hoạt động</option>
                  </select>
                </div>
              )}
            </div>

            <div className="mc-field mc-field--full">
              <label className="mc-label" htmlFor="cruise-description">
                Mô tả
              </label>
              <textarea
                id="cruise-description"
                className="mc-input"
                name="description"
                rows={4}
                value={form.description}
                onChange={handleChange}
                placeholder="Giới thiệu ngắn về du thuyền..."
                maxLength={5000}
                disabled={saving}
              />
            </div>
          </div>
        </Modal.Body>

        <Modal.Footer>
          <button
            type="button"
            className="mc-btn mc-btn--ghost"
            onClick={onClose}
            disabled={saving}
          >
            Hủy
          </button>

          <button
            type="submit"
            className="mc-btn mc-btn--primary"
            disabled={saving}
          >
            {saving ? (
              <>
                <Spinner size="sm" animation="border" />
                Đang lưu...
              </>
            ) : isEditing ? (
              "Lưu thay đổi"
            ) : (
              "Tạo du thuyền"
            )}
          </button>
        </Modal.Footer>
      </form>
    </Modal>
  );
}
