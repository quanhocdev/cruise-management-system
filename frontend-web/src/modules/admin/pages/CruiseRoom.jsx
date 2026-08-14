import { useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

import useCruiseRooms from "../hooks/useCruiseRooms";

import CruiseRoomTable from "../components/cruise/CruiseRoomTable";
import CruiseRoomFormModal from "../components/cruise/CruiseRoomFormModal";

import "../styles/cruise/CruiseRoom.css";

export default function CruiseRoom() {
  const { deckId } = useParams();

  const navigate = useNavigate();

  const {
    rooms,
    roomTypes,
    loading,
    error,
    success,

    setError,
    setSuccess,

    createRooms,
    updateRoom,
    deleteRoom,
  } = useCruiseRooms(deckId);

  const [showModal, setShowModal] = useState(false);

  const [saving, setSaving] = useState(false);

  const [editingRoom, setEditingRoom] = useState(null);

  // =====================================================
  // FORM
  // =====================================================

  const [form, setForm] = useState({
    code: "",
    roomTypeId: "",
    quantity: 1,
    status: "ACTIVE",
  });

  // =====================================================
  // OPEN CREATE
  // =====================================================

  const handleOpenCreate = () => {
    setEditingRoom(null);

    setForm({
      code: "",
      roomTypeId: "",
      quantity: 1,
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  // =====================================================
  // OPEN EDIT
  // =====================================================

  const handleOpenEdit = (room) => {
    setEditingRoom(room);

    setForm({
      code: room.code || "",
      roomTypeId: room.roomTypeId || "",
      quantity: 1,
      status: room.status || "ACTIVE",
    });

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  // =====================================================
  // CLOSE MODAL
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
    // CREATE
    // ===================================================

    if (!editingRoom) {
      if (!form.roomTypeId) {
        setError("Vui lòng chọn loại phòng.");
        return;
      }

      const quantity = Number(form.quantity);

      if (!Number.isInteger(quantity) || quantity <= 0) {
        setError("Số lượng phòng phải lớn hơn 0.");
        return;
      }

      setSaving(true);

      try {
        const result = await createRooms({
          roomTypeId: form.roomTypeId,

          quantity,
        });

        if (result) {
          setShowModal(false);
        }
      } finally {
        setSaving(false);
      }

      return;
    }

    // ===================================================
    // UPDATE
    // ===================================================

    if (!form.code.trim()) {
      setError("Vui lòng nhập mã phòng.");
      return;
    }

    if (!form.roomTypeId) {
      setError("Vui lòng chọn loại phòng.");
      return;
    }

    setSaving(true);

    try {
      const result = await updateRoom(editingRoom.id, {
        code: form.code.trim(),

        roomTypeId: form.roomTypeId,

        status: form.status,
      });

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

  const handleDelete = async (room) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa phòng "${room.code}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteRoom(room.id);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="cruise-room-page container-fluid py-4">
      {/* =================================================
          HEADER
         ================================================= */}

      <div className="cruise-room-header mb-4">
        <div>
          <Button
            variant="link"
            className="px-0 mb-2"
            onClick={() => navigate(-1)}
          >
            ← Quay lại tầng
          </Button>

          <h2 className="cruise-room-title">Quản lý phòng</h2>

          <p className="cruise-room-description">
            Quản lý các phòng thuộc tầng của du thuyền.
          </p>
        </div>

        <div className="d-flex gap-2">
          {/* ROOM TYPES */}

          <Button
            variant="outline-primary"
            onClick={() => navigate("/admin/room-types")}
          >
            Quản lý loại phòng
          </Button>

          {/* CREATE */}

          <Button variant="primary" onClick={handleOpenCreate}>
            + Thêm phòng
          </Button>
        </div>
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

      <CruiseRoomTable
        rooms={rooms}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

      {/* =================================================
          MODAL
         ================================================= */}

      <CruiseRoomFormModal
        show={showModal}
        saving={saving}
        editingRoom={editingRoom}
        form={form}
        roomTypes={roomTypes}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
