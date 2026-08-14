// src/modules/admin/pages/ManagerRoomType.jsx
import { useState } from "react";
import { Alert, Button } from "react-bootstrap";

import useRoomTypes from "../hooks/useRoomTypes";
import RoomTypeTable from "../components/room/RoomTypeTable";
import RoomTypeFormModal from "../components/room/RoomTypeFormModal";

export default function ManagerRoomType() {
  const {
    roomTypes,
    loading,
    error,
    success,
    setError,
    setSuccess,
    createRoomType,
    updateRoomType,
    deleteRoomType,
  } = useRoomTypes();

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingRoomType, setEditingRoomType] = useState(null);

  // =====================================================
  // FORM
  // =====================================================

  const [form, setForm] = useState({
    name: "",
    description: "",
  });

  // =====================================================
  // OPEN CREATE
  // =====================================================

  const handleOpenCreate = () => {
    setEditingRoomType(null);

    setForm({
      name: "",
      description: "",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  // =====================================================
  // OPEN EDIT
  // =====================================================

  const handleOpenEdit = (roomType) => {
    setEditingRoomType(roomType);

    setForm({
      name: roomType.name || "",
      description: roomType.description || "",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  // =====================================================
  // CLOSE
  // =====================================================

  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
  };

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    // ===================================================
    // VALIDATION
    // ===================================================

    const name = form.name.trim();
    const description = form.description.trim();

    if (!name) {
      setError("Vui lòng nhập tên loại phòng.");
      return;
    }

    if (name.length > 100) {
      setError("Tên loại phòng không được vượt quá 100 ký tự.");
      return;
    }

    if (description.length > 5000) {
      setError("Mô tả không được vượt quá 5000 ký tự.");
      return;
    }

    // ===================================================
    // REQUEST
    // ===================================================

    const data = {
      name,
      description,
    };

    setSaving(true);

    try {
      let result;

      if (!editingRoomType) {
        result = await createRoomType(data);
      } else {
        result = await updateRoomType(editingRoomType.id, data);
      }

      if (result) {
        setShowModal(false);
      }
    } finally {
      setSaving(false);
    }
  };

  // =====================================================
  // DELETE
  // =====================================================

  const handleDelete = async (roomType) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa loại phòng "${roomType.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteRoomType(roomType.id);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="container-fluid py-4">
      {/* =================================================
          HEADER
         ================================================= */}

      <div className="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h2>Quản lý loại phòng</h2>

          <p className="text-muted mb-0">
            Quản lý các loại phòng được sử dụng trên các tầng của du thuyền.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Thêm loại phòng
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

      <RoomTypeTable
        roomTypes={roomTypes}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

      {/* =================================================
          MODAL
         ================================================= */}

      <RoomTypeFormModal
        show={showModal}
        saving={saving}
        editingRoomType={editingRoomType}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
