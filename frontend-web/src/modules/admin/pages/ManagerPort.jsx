import { useState } from "react";
import { Alert, Button } from "react-bootstrap";

import usePorts from "../hooks/usePorts";
import PortTable from "../components/port/PortTable";
import PortFormModal from "../components/port/PortFormModal";

import "../styles/ManagerPort.css";

export default function ManagerPort() {
  const {
    ports,
    loading,
    error,
    success,
    setError,
    setSuccess,
    createPort,
    updatePort,
    deactivatePort,
  } = usePorts();

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
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

    setError("");
    setSuccess("");

    /*
     * Frontend validation
     */
    if (!form.name.trim()) {
      setError("Vui lòng nhập tên cảng.");
      return;
    }

    if (form.latitude === "" || form.longitude === "") {
      setError("Vui lòng chọn vị trí trên bản đồ.");
      return;
    }

    const requestData = {
      name: form.name.trim(),
      latitude: Number(form.latitude),
      longitude: Number(form.longitude),
      description: form.description.trim() || null,
    };

    setSaving(true);

    try {
      let result;

      /*
       * CREATE
       */
      if (!editingPort) {
        result = await createPort(requestData);
      } else {

      /*
       * UPDATE
       */
        result = await updatePort(editingPort.id, {
          ...requestData,
          status: form.status,
        });
      }

      if (result) {
        setShowModal(false);
      }
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

    const result = await deactivatePort(port.id);

    if (!result) {
      return;
    }
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */
  return (
    <div className="manager-port-page container-fluid py-4">
      {/* =================================================
          HEADER
         ================================================= */}
      <div className="manager-port-header mb-4">
        <div>
          <h2 className="manager-port-title">Quản lý cảng</h2>

          <p className="manager-port-description">
            Quản lý các cảng và điểm dừng hành trình của du thuyền.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo cảng
        </Button>
      </div>

      {/* =================================================
          SUCCESS
         ================================================= */}
      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {/* =================================================
          ERROR
         ================================================= */}
      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {/* =================================================
          TABLE
         ================================================= */}
      <PortTable
        ports={ports}
        loading={loading}
        onEdit={handleOpenEdit}
        onDeactivate={handleDeactivate}
      />

      {/* =================================================
          CREATE / UPDATE MODAL
         ================================================= */}
      <PortFormModal
        show={showModal}
        saving={saving}
        editingPort={editingPort}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
        onLocationChange={handleLocationChange}
      />
    </div>
  );
}
