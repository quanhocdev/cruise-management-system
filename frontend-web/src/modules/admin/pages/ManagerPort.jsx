import { useEffect, useState } from "react";
import {
  Alert,
  Button,
  Card,
  Form,
  Modal,
  Spinner,
  Table,
} from "react-bootstrap";

import api from "../../../api/axios";
import PortMap from "../components/port/PortMap";

export default function ManagerPort() {
  const [ports, setPorts] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [showModal, setShowModal] = useState(false);

  const [editingPort, setEditingPort] = useState(null);

  const [form, setForm] = useState({
    name: "",
    latitude: "",
    longitude: "",
    description: "",
    status: "ACTIVE",
  });

  /*
   * =====================================================
   * LOAD PORTS
   * =====================================================
   */
  useEffect(() => {
    loadPorts();
  }, []);

  const loadPorts = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/ports");

      setPorts(response.data || []);
    } catch (error) {
      console.error("Load ports error:", error);

      setError(
        error.response?.data?.message || "Không thể tải danh sách cảng.",
      );
    } finally {
      setLoading(false);
    }
  };

  /*
   * =====================================================
   * OPEN CREATE
   * =====================================================
   */
  const handleOpenCreate = () => {
    setEditingPort(null);

    setForm({
      name: "",
      latitude: "",
      longitude: "",
      description: "",
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * OPEN EDIT
   * =====================================================
   */
  const handleOpenEdit = (port) => {
    setEditingPort(port);

    setForm({
      name: port.name || "",
      latitude: port.latitude ?? "",
      longitude: port.longitude ?? "",
      description: port.description || "",
      status: port.status || "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * CLOSE MODAL
   * =====================================================
   */
  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
  };

  /*
   * =====================================================
   * INPUT CHANGE
   * =====================================================
   */
  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /*
   * =====================================================
   * MAP LOCATION CHANGE
   * =====================================================
   */
  const handleLocationChange = ({ latitude, longitude }) => {
    setForm((previous) => ({
      ...previous,
      latitude,
      longitude,
    }));
  };

  /*
   * =====================================================
   * CREATE / UPDATE
   * =====================================================
   */
  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");
    setSuccess("");

    /*
     * Frontend validation
     */
    if (!form.name.trim()) {
      setError("Vui lòng nhập tên cảng.");
      setSaving(false);
      return;
    }

    if (form.latitude === "" || form.longitude === "") {
      setError("Vui lòng chọn vị trí trên bản đồ.");
      setSaving(false);
      return;
    }

    const requestData = {
      name: form.name.trim(),
      latitude: Number(form.latitude),
      longitude: Number(form.longitude),
      description: form.description.trim() || null,
    };

    try {
      /*
       * CREATE
       */
      if (!editingPort) {
        await api.post("/ports", requestData);

        setSuccess("Tạo cảng thành công.");
      } else {

      /*
       * UPDATE
       */
        await api.patch(`/ports/${editingPort.id}`, {
          ...requestData,
          status: form.status,
        });

        setSuccess("Cập nhật cảng thành công.");
      }

      setShowModal(false);

      await loadPorts();
    } catch (error) {
      console.error("Save port error:", error);

      setError(
        error.response?.data?.message || "Không thể lưu thông tin cảng.",
      );
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * DEACTIVATE
   * =====================================================
   */
  const handleDeactivate = async (port) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn vô hiệu hóa cảng "${port.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    setError("");
    setSuccess("");

    try {
      await api.delete(`/ports/${port.id}`);

      setSuccess("Đã vô hiệu hóa cảng.");

      await loadPorts();
    } catch (error) {
      console.error("Deactivate port error:", error);

      setError(error.response?.data?.message || "Không thể vô hiệu hóa cảng.");
    }
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */
  return (
    <div className="container-fluid py-4">
      {/* ===================================================
          HEADER
         =================================================== */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-1">Quản lý cảng</h2>

          <p className="text-muted mb-0">
            Quản lý các cảng và điểm dừng hành trình của du thuyền.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo cảng
        </Button>
      </div>

      {/* ===================================================
          SUCCESS
         =================================================== */}
      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {/* ===================================================
          ERROR
         =================================================== */}
      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {/* ===================================================
          PORT TABLE
         =================================================== */}
      <Card>
        <Card.Body>
          {loading ? (
            <div className="text-center py-5">
              <Spinner animation="border" />

              <div className="mt-2">Đang tải danh sách cảng...</div>
            </div>
          ) : ports.length === 0 ? (
            <div className="text-center text-muted py-5">Chưa có cảng nào.</div>
          ) : (
            <div className="table-responsive">
              <Table hover bordered align="middle">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Tên cảng</th>
                    <th>Địa chỉ</th>
                    <th>Thành phố</th>
                    <th>Quốc gia</th>
                    <th>Tọa độ</th>
                    <th>Trạng thái</th>
                    <th
                      className="text-center"
                      style={{
                        width: "180px",
                      }}
                    >
                      Thao tác
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {ports.map((port, index) => (
                    <tr key={port.id}>
                      <td>{index + 1}</td>

                      <td>
                        <strong>{port.name}</strong>
                      </td>

                      <td>{port.address || "—"}</td>

                      <td>{port.city || "—"}</td>

                      <td>{port.country || "—"}</td>

                      <td>
                        <small>
                          {port.latitude}
                          <br />
                          {port.longitude}
                        </small>
                      </td>

                      <td>
                        {port.status === "ACTIVE" ? (
                          <span className="badge bg-success">ACTIVE</span>
                        ) : (
                          <span className="badge bg-secondary">INACTIVE</span>
                        )}
                      </td>

                      <td>
                        <div className="d-flex gap-2 justify-content-center">
                          <Button
                            size="sm"
                            variant="outline-primary"
                            onClick={() => handleOpenEdit(port)}
                          >
                            Sửa
                          </Button>

                          {port.status === "ACTIVE" && (
                            <Button
                              size="sm"
                              variant="outline-danger"
                              onClick={() => handleDeactivate(port)}
                            >
                              Vô hiệu hóa
                            </Button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      {/* ===================================================
          CREATE / UPDATE MODAL
         =================================================== */}
      <Modal show={showModal} onHide={handleCloseModal} size="xl" centered>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingPort ? "Cập nhật cảng" : "Tạo cảng"}
          </Modal.Title>
        </Modal.Header>

        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            {error && <Alert variant="danger">{error}</Alert>}

            {/* =================================================
                PORT NAME
               ================================================= */}
            <Form.Group className="mb-3">
              <Form.Label>
                Tên cảng <span className="text-danger">*</span>
              </Form.Label>

              <Form.Control
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Ví dụ: Cảng Cát Lái"
                maxLength={150}
                required
              />
            </Form.Group>

            {/* =================================================
                MAP
               ================================================= */}
            <Form.Group className="mb-3">
              <Form.Label>
                Vị trí cảng <span className="text-danger">*</span>
              </Form.Label>

              <PortMap
                latitude={form.latitude !== "" ? Number(form.latitude) : null}
                longitude={
                  form.longitude !== "" ? Number(form.longitude) : null
                }
                onLocationChange={handleLocationChange}
              />
            </Form.Group>

            {/* =================================================
                DESCRIPTION
               ================================================= */}
            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>

              <Form.Control
                as="textarea"
                rows={4}
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Mô tả về cảng..."
                maxLength={1000}
              />
            </Form.Group>

            {/* =================================================
                STATUS - UPDATE ONLY
               ================================================= */}
            {editingPort && (
              <Form.Group className="mb-3">
                <Form.Label>Trạng thái</Form.Label>

                <Form.Select
                  name="status"
                  value={form.status}
                  onChange={handleChange}
                >
                  <option value="ACTIVE">ACTIVE</option>

                  <option value="INACTIVE">INACTIVE</option>
                </Form.Select>
              </Form.Group>
            )}

            {/* =================================================
                INFO
               ================================================= */}
            <Alert variant="light" className="mb-0">
              <strong>Lưu ý:</strong> Địa chỉ, thành phố và quốc gia sẽ được hệ
              thống tự động xác định từ tọa độ thông qua Nominatim.
            </Alert>
          </Modal.Body>

          <Modal.Footer>
            <Button
              variant="secondary"
              onClick={handleCloseModal}
              disabled={saving}
            >
              Hủy
            </Button>

            <Button variant="primary" type="submit" disabled={saving}>
              {saving ? (
                <>
                  <Spinner size="sm" animation="border" className="me-2" />
                  Đang lưu...
                </>
              ) : editingPort ? (
                "Cập nhật"
              ) : (
                "Tạo cảng"
              )}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
}
