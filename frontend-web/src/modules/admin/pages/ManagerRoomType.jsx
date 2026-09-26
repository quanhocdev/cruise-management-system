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
  // FORM (Thêm capacity mặc định là 1)
  // =====================================================

  const [form, setForm] = useState({
    name: "",
    description: "",
    capacity: 1,
  });

  // =====================================================
  // OPEN CREATE
  // =====================================================

  const handleOpenCreate = () => {
    setEditingRoomType(null);
    setForm({
      name: "",
      description: "",
      capacity: 1,
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
      capacity: roomType.capacity || 1,
    });
    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleCloseModal = () => {
    if (saving) return;
    setShowModal(false);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({
      ...previous,
      // Ép kiểu number nếu là trường capacity
      [name]: name === "capacity" ? (value === "" ? "" : Number(value)) : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    const name = form.name.trim();
    const description = form.description.trim();
    const capacity = Number(form.capacity);

    if (!name) {
      setError("Vui lòng nhập tên loại phòng.");
      return;
    }

    if (name.length > 100) {
      setError("Tên loại phòng không được vượt quá 100 ký tự.");
      return;
    }

    if (!capacity || capacity < 1) {
      setError("Sức chứa tối đa phải từ 1 người trở lên.");
      return;
    }

    if (description.length > 5000) {
      setError("Mô tả không được vượt quá 5000 ký tự.");
      return;
    }

    const data = {
      name,
      description,
      capacity,
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

  const handleDelete = async (roomType) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa loại phòng "${roomType.name}" không?`,
    );
    if (!confirmed) return;
    await deleteRoomType(roomType.id);
  };

  return (
    <div className="container-fluid py-4">
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

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <RoomTypeTable
        roomTypes={roomTypes}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

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
