// src/modules/admin/components/port/PortFormModal.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

const INITIAL_FORM = {
  name: "",
  latitude: "",
  longitude: "",
  description: "",
  status: "ACTIVE",
};

export default function PortFormModal({
  show,
  saving = false,
  editingPort = null,
  selectedLocation = null,
  error = "",
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState(INITIAL_FORM);

  const isEditing = Boolean(editingPort);

  // =====================================================
  // INITIALIZE FORM
  // =====================================================

  useEffect(() => {
    if (!show) {
      return;
    }

    if (editingPort) {
      setForm({
        name: editingPort.name || "",

        latitude: editingPort.latitude ?? "",

        longitude: editingPort.longitude ?? "",

        description: editingPort.description || "",

        status: editingPort.status || "ACTIVE",
      });

      return;
    }

    setForm({
      name: "",
      latitude: selectedLocation?.latitude ?? "",
      longitude: selectedLocation?.longitude ?? "",
      description: "",
      status: "ACTIVE",
    });
  }, [show, editingPort, selectedLocation]);

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (event) => {
    event.preventDefault();

    const name = form.name.trim();

    if (!name) {
      return;
    }

    const latitude = Number(form.latitude);

    const longitude = Number(form.longitude);

    if (Number.isNaN(latitude) || Number.isNaN(longitude)) {
      return;
    }

    const data = {
      name,

      latitude,

      longitude,

      description: form.description.trim() || null,
    };

    if (isEditing) {
      data.status = form.status;
    }

    await onSubmit(data);
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      size="lg"
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton={!saving}>
          <div>
            <Modal.Title>
              {isEditing ? "Chỉnh sửa cảng" : "Tạo cảng"}
            </Modal.Title>

            <div className="text-muted mt-1">
              {isEditing
                ? "Cập nhật thông tin cảng."
                : "Nhập thông tin cảng tại vị trí đã chọn."}
            </div>
          </div>
        </Modal.Header>

        <Modal.Body>
          {error && (
            <Alert variant="danger">
              {typeof error === "string"
                ? error
                : error?.message || "Có lỗi xảy ra."}
            </Alert>
          )}

          <Form.Group className="mb-3">
            <Form.Label>
              Tên cảng <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Ví dụ: Cảng Tiên Sa"
              maxLength={150}
              required
              disabled={saving}
            />
          </Form.Group>

          <div className="row">
            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Vĩ độ (Latitude)</Form.Label>

                <Form.Control
                  type="number"
                  name="latitude"
                  value={form.latitude}
                  onChange={handleChange}
                  step="any"
                  min="-90"
                  max="90"
                  required
                  disabled={saving}
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>Kinh độ (Longitude)</Form.Label>

                <Form.Control
                  type="number"
                  name="longitude"
                  value={form.longitude}
                  onChange={handleChange}
                  step="any"
                  min="-180"
                  max="180"
                  required
                  disabled={saving}
                />
              </Form.Group>
            </div>
          </div>

          {selectedLocation?.placeName && (
            <div className="port-selected-location mb-3">
              <div className="port-selected-location-label">
                Vị trí Mapbox tìm được
              </div>

              <div className="port-selected-location-name">
                {selectedLocation.placeName}
              </div>
            </div>
          )}

          {isEditing && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                name="status"
                value={form.status}
                onChange={handleChange}
                disabled={saving}
              >
                <option value="ACTIVE">Đang hoạt động</option>

                <option value="INACTIVE">Ngừng hoạt động</option>
              </Form.Select>
            </Form.Group>
          )}

          <Form.Group>
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={4}
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Mô tả thêm về cảng..."
              maxLength={1000}
              disabled={saving}
            />
          </Form.Group>

          {!isEditing && (
            <div className="port-geocoding-note mt-3">
              <span>📍</span>

              <span>
                Sau khi tạo, hệ thống sẽ tự động lấy địa chỉ, thành phố và quốc
                gia từ tọa độ thông qua reverse geocoding.
              </span>
            </div>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button
            variant="primary"
            type="submit"
            disabled={
              saving ||
              !form.name.trim() ||
              form.latitude === "" ||
              form.longitude === ""
            }
          >
            {saving && (
              <Spinner animation="border" size="sm" className="me-2" />
            )}

            {isEditing ? "Lưu thay đổi" : "Tạo cảng"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
