import { useMemo, useState } from "react";
import { Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { Plus, Tag, Search, X, ArrowLeft } from "lucide-react";

import useRoomTypes from "../hooks/useRoomTypes";
import RoomTypeFormModal from "../components/room/RoomTypeFormModal";
import RoomTypeTable from "../components/room/RoomTypeTable";

import "../styles/cruise/ManagerCruise.css";

export default function ManagerRoomType() {
  const navigate = useNavigate();

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

  const [keyword, setKeyword] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingRoomType, setEditingRoomType] = useState(null);
  const [imageFile, setImageFile] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: "",
    capacity: 1,
  });

  // ---------- LỌC & TÌM KIẾM ----------
  const filteredRoomTypes = useMemo(() => {
    const query = keyword.trim().toLowerCase();
    if (!query) return roomTypes;

    return roomTypes.filter(
      (rt) =>
        String(rt.name || "")
          .toLowerCase()
          .includes(query) ||
        String(rt.description || "")
          .toLowerCase()
          .includes(query),
    );
  }, [roomTypes, keyword]);

  const isFiltered = keyword.trim() !== "";

  // ---------- MODAL HANDLERS ----------
  const handleOpenCreate = () => {
    setEditingRoomType(null);
    setForm({ name: "", description: "", capacity: 1 });
    setImageFile(null);
    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (roomType) => {
    setEditingRoomType(roomType);
    setForm({
      name: roomType.name || "",
      description: roomType.description || "",
      capacity: roomType.capacity || 1,
    });
    setImageFile(null);
    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleCloseModal = () => {
    if (saving) return;
    setImageFile(null);
    setShowModal(false);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({
      ...previous,
      [name]: name === "capacity" ? (value === "" ? "" : Number(value)) : value,
    }));
  };

  const handleFileChange = (event) => {
    const file = event.target.files?.[0] || null;
    setImageFile(file);
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
      image: imageFile,
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
        setImageFile(null);
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
    <div className="mc-page container-fluid py-4">
      {/* HEADER */}
      <header className="mc-header">
        <div>
          <button
            type="button"
            className="btn btn-link px-0 mb-2 text-decoration-none d-inline-flex align-items-center gap-1"
            onClick={() => navigate(-1)}
          >
            <ArrowLeft size={16} /> Quay lại quản lý phòng
          </button>
          <h2>Quản lý loại phòng</h2>
          <p>
            Định nghĩa các tiêu chuẩn hạng phòng, sức chứa hành khách và mô tả
            dịch vụ.
          </p>
        </div>

        <button
          type="button"
          className="mc-btn mc-btn--primary"
          onClick={handleOpenCreate}
        >
          <Plus size={18} />
          Thêm loại phòng
        </button>
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
      <section
        className="mc-stats"
        style={{ gridTemplateColumns: "repeat(2, minmax(0, 1fr))" }}
      >
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--blue">
            <Tag size={22} />
          </span>
          <div>
            <strong>{roomTypes.length}</strong>
            <span>Tổng số hạng loại phòng</span>
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
            placeholder="Tìm kiếm theo tên loại phòng hoặc mô tả..."
          />
          {keyword && (
            <button type="button" onClick={() => setKeyword("")}>
              <X size={16} />
            </button>
          )}
        </div>

        <p className="mc-count">
          Hiển thị <strong>{filteredRoomTypes.length}</strong> /{" "}
          {roomTypes.length} loại phòng
        </p>
      </section>

      {/* CONTENT TABLE */}
      {!loading && filteredRoomTypes.length === 0 ? (
        <div className="mc-empty">
          <Tag size={44} />
          <h3>
            {isFiltered
              ? "Không tìm thấy loại phòng phù hợp"
              : "Chưa có loại phòng nào"}
          </h3>
          <p>
            {isFiltered
              ? "Thử đổi từ khóa tìm kiếm khác."
              : "Thêm loại phòng đầu tiên (Standard, Deluxe, Suite...) để gán cho các phòng trên du thuyền."}
          </p>
          {isFiltered ? (
            <button
              type="button"
              className="mc-btn mc-btn--ghost"
              onClick={() => setKeyword("")}
            >
              Xóa bộ lọc
            </button>
          ) : (
            <button
              type="button"
              className="mc-btn mc-btn--primary"
              onClick={handleOpenCreate}
            >
              <Plus size={16} /> Thêm loại phòng ngay
            </button>
          )}
        </div>
      ) : (
        <div className="bg-white rounded-3 p-3 border">
          <RoomTypeTable
            roomTypes={filteredRoomTypes}
            loading={loading}
            onEdit={handleOpenEdit}
            onDelete={handleDelete}
          />
        </div>
      )}

      {/* MODAL FORM */}
      <RoomTypeFormModal
        show={showModal}
        saving={saving}
        editingRoomType={editingRoomType}
        form={form}
        imageFile={imageFile}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
        onFileChange={handleFileChange}
      />
    </div>
  );
}
