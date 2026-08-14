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
    createRoom,
    updateRoom,
    deleteRoom,
  } = useCruiseRooms(deckId);

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);

  const [form, setForm] = useState({
    code: "",
    roomTypeId: "",
    status: "ACTIVE",
  });

  const handleOpenCreate = () => {
    setEditingRoom(null);

    setForm({
      code: "",
      roomTypeId: "",
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (room) => {
    setEditingRoom(room);

    setForm({
      code: room.code || "",
      roomTypeId: room.roomTypeId || "",
      status: room.status || "ACTIVE",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

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
      let result;

      if (!editingRoom) {
        result = await createRoom({
          code: form.code.trim(),
          roomTypeId: form.roomTypeId,
        });
      } else {
        result = await updateRoom(editingRoom.id, {
          code: form.code.trim(),
          roomTypeId: form.roomTypeId,
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

  const handleDelete = async (room) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa phòng "${room.code}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteRoom(room.id);
  };

  return (
    <div className="cruise-room-page container-fluid py-4">
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
          {/* QUẢN LÝ LOẠI PHÒNG */}
          <Button
            variant="outline-primary"
            onClick={() => navigate("/admin/room-types")}
          >
            Quản lý loại phòng
          </Button>

          {/* THÊM PHÒNG */}
          <Button variant="primary" onClick={handleOpenCreate}>
            + Thêm phòng
          </Button>
        </div>
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

      <CruiseRoomTable
        rooms={rooms}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

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
