// src/modules/admin/pages/CruiseRoom.jsx
import { useMemo, useState } from "react";
import { Alert } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import {
  Plus,
  DoorClosed,
  CheckCircle2,
  PauseCircle,
  Search,
  X,
  Pencil,
  Trash2,
  Tag,
} from "lucide-react";

import useCruiseRooms from "../hooks/useCruiseRooms";
import CruiseRoomFormModal from "../components/cruise/CruiseRoomFormModal";

import "../styles/cruise/ManagerCruise.css";

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

  const [keyword, setKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);

  const [form, setForm] = useState({
    code: "",
    roomTypeId: "",
    quantity: 1,
    status: "ACTIVE",
  });

  // ---------- LỌC & THỐNG KÊ ----------
  const counts = useMemo(
    () => ({
      ALL: rooms.length,
      ACTIVE: rooms.filter((r) => r.status === "ACTIVE").length,
      INACTIVE: rooms.filter((r) => r.status !== "ACTIVE").length,
    }),
    [rooms],
  );

  const filteredRooms = useMemo(() => {
    const query = keyword.trim().toLowerCase();

    return rooms.filter((room) => {
      if (statusFilter === "ACTIVE" && room.status !== "ACTIVE") return false;
      if (statusFilter === "INACTIVE" && room.status === "ACTIVE") return false;

      if (!query) return true;

      return (
        String(room.code || "")
          .toLowerCase()
          .includes(query) ||
        String(room.roomTypeName || "")
          .toLowerCase()
          .includes(query)
      );
    });
  }, [rooms, keyword, statusFilter]);

  const isFiltered = keyword.trim() !== "" || statusFilter !== "ALL";

  const clearFilter = () => {
    setKeyword("");
    setStatusFilter("ALL");
  };

  // ---------- MODAL HANDLERS ----------
  const handleOpenCreate = () => {
    setEditingRoom(null);
    setForm({ code: "", roomTypeId: "", quantity: 1, status: "ACTIVE" });
    setError("");
    setSuccess("");
    setShowModal(true);
  };

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

  const handleCloseModal = () => {
    if (saving) return;
    setShowModal(false);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

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
        const result = await createRoom({
          roomTypeId: form.roomTypeId,
          quantity,
        });
        if (result) setShowModal(false);
      } finally {
        setSaving(false);
      }
      return;
    }

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
      if (result) setShowModal(false);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (room) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa phòng "${room.code}" không?`,
    );
    if (!confirmed) return;
    await deleteRoom(room.id);
  };

  return (
    <div className="mc-page container-fluid py-4">
      {/* HEADER */}
      <header className="mc-header">
        <div>
          <button
            type="button"
            className="btn btn-link px-0 mb-2 text-decoration-none"
            onClick={() => navigate(-1)}
          >
            ← Quay lại chi tiết tầng
          </button>
          <h2>Quản lý phòng</h2>
          <p>
            Quản lý các phòng lưu trú, phân loại và trạng thái sẵn sàng đón
            khách.
          </p>
        </div>

        <div className="d-flex gap-2">
          <button
            type="button"
            className="mc-btn mc-btn--ghost"
            onClick={() => navigate("/admin/room-types")}
          >
            <Tag size={16} />
            Quản lý loại phòng
          </button>
          <button
            type="button"
            className="mc-btn mc-btn--primary"
            onClick={handleOpenCreate}
          >
            <Plus size={18} />
            Thêm phòng
          </button>
        </div>
      </header>

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

      {/* STATS OVERVIEW */}
      <section className="mc-stats">
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--blue">
            <DoorClosed size={22} />
          </span>
          <div>
            <strong>{rooms.length}</strong>
            <span>Tổng số phòng</span>
          </div>
        </div>
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--green">
            <CheckCircle2 size={22} />
          </span>
          <div>
            <strong>{counts.ACTIVE}</strong>
            <span>Sẵn sàng hoạt động</span>
          </div>
        </div>
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--gray">
            <PauseCircle size={22} />
          </span>
          <div>
            <strong>{counts.INACTIVE}</strong>
            <span>Đang bảo trì / Khóa</span>
          </div>
        </div>
      </section>

      {/* TOOLBAR */}
      <section className="mc-toolbar">
        <div className="mc-search">
          <Search size={18} />
          <input
            type="text"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="Tìm theo mã phòng hoặc loại phòng..."
          />
          {keyword && (
            <button type="button" onClick={() => setKeyword("")}>
              <X size={16} />
            </button>
          )}
        </div>

        <div className="mc-filter">
          {[
            { value: "ALL", label: "Tất cả" },
            { value: "ACTIVE", label: "Hoạt động" },
            { value: "INACTIVE", label: "Ngừng hoạt động" },
          ].map((filter) => (
            <button
              key={filter.value}
              type="button"
              className={statusFilter === filter.value ? "is-active" : ""}
              onClick={() => setStatusFilter(filter.value)}
            >
              {filter.label}
              <span>{counts[filter.value]}</span>
            </button>
          ))}
        </div>

        <p className="mc-count">
          Hiển thị <strong>{filteredRooms.length}</strong> / {rooms.length}{" "}
          phòng
        </p>
      </section>

      {/* CONTENT GRID */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Đang tải danh sách phòng...</p>
        </div>
      ) : filteredRooms.length === 0 ? (
        <div className="mc-empty">
          <DoorClosed size={44} />
          <h3>
            {isFiltered ? "Không tìm thấy phòng phù hợp" : "Chưa có phòng nào"}
          </h3>
          <p>
            {isFiltered
              ? "Thử đổi từ khóa hoặc bộ lọc trạng thái."
              : "Thêm phòng mới theo dãy hoặc theo loại phòng cho tầng này."}
          </p>
          {isFiltered ? (
            <button
              type="button"
              className="mc-btn mc-btn--ghost"
              onClick={clearFilter}
            >
              Xóa bộ lọc
            </button>
          ) : (
            <button
              type="button"
              className="mc-btn mc-btn--primary"
              onClick={handleOpenCreate}
            >
              <Plus size={16} /> Thêm phòng ngay
            </button>
          )}
        </div>
      ) : (
        <div className="mc-grid">
          {filteredRooms.map((room) => {
            const isActive = room.status === "ACTIVE";
            return (
              <article
                key={room.id}
                className={`mc-card ${isActive ? "" : "mc-card--inactive"}`}
              >
                <div className="mc-card-body">
                  <div className="mc-card-title">
                    <h3>Phòng {room.code}</h3>
                    <span
                      className={`mc-badge ${isActive ? "mc-badge--active" : "mc-badge--inactive"}`}
                    >
                      {isActive ? "Hoạt động" : "Ngừng hoạt động"}
                    </span>
                  </div>
                  <p className="mc-card-meta">
                    <Tag size={15} />
                    <span>
                      Loại phòng:{" "}
                      <strong>{room.roomTypeName || "Chưa phân loại"}</strong>
                    </span>
                  </p>
                </div>

                <footer className="mc-card-actions">
                  <button
                    type="button"
                    className="mc-icon-btn"
                    style={{ flex: 1 }}
                    onClick={() => handleOpenEdit(room)}
                  >
                    <Pencil size={16} />
                    <span>Chỉnh sửa</span>
                  </button>

                  <button
                    type="button"
                    className="mc-icon-btn mc-icon-btn--danger"
                    onClick={() => handleDelete(room)}
                    title="Xóa phòng"
                  >
                    <Trash2 size={17} />
                  </button>
                </footer>
              </article>
            );
          })}
        </div>
      )}

      {/* MODAL */}
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
