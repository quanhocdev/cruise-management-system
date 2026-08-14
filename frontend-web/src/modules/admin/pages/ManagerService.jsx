import { useState } from "react";
import { Alert, Button } from "react-bootstrap";

import useServices from "../hooks/useServices";

import ServiceTable from "../components/service/ServiceTable";
import ServiceFormModal from "../components/service/ServiceFormModal";

import "../styles/ManagerService.css";

export default function ManagerService() {
  const {
    services,
    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    createService,
    updateService,
    deleteService,
  } = useServices();

  const [showModal, setShowModal] = useState(false);
  const [editingService, setEditingService] = useState(null);

  /*
   * =====================================================
   * OPEN CREATE
   * =====================================================
   */
  const handleOpenCreate = () => {
    setEditingService(null);

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * OPEN EDIT
   * =====================================================
   */
  const handleOpenEdit = (service) => {
    setEditingService(service);

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
    setEditingService(null);
    setError("");
  };

  /*
   * =====================================================
   * CREATE / UPDATE
   * =====================================================
   */
  const handleSubmit = async (formData) => {
    let result;

    if (editingService) {
      result = await updateService(editingService.id, formData);
    } else {
      result = await createService(formData);
    }

    if (result) {
      setShowModal(false);
      setEditingService(null);
    }
  };

  /*
   * =====================================================
   * DELETE
   * =====================================================
   */
  const handleDelete = async (service) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa dịch vụ "${service.name}" không?\n\n` +
        "Dịch vụ sẽ bị xóa khỏi cơ sở dữ liệu và không thể khôi phục.",
    );

    if (!confirmed) {
      return;
    }

    await deleteService(service.id);
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */
  return (
    <div className="manager-service-page container-fluid py-4">
      {/* =================================================
          HEADER
         ================================================= */}
      <div className="manager-service-header">
        <div>
          <h2 className="manager-service-title">Quản lý dịch vụ</h2>

          <p className="manager-service-description">
            Quản lý danh sách các dịch vụ được cung cấp trong hệ thống.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo dịch vụ
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
          SERVICE TABLE
         ================================================= */}
      <ServiceTable
        services={services}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

      {/* =================================================
          FORM MODAL
         ================================================= */}
      <ServiceFormModal
        show={showModal}
        saving={saving}
        editingService={editingService}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
      />
    </div>
  );
}
