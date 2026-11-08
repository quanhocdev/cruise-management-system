// src/modules/admin/pages/CruiseArea.jsx
import { useMemo, useState } from "react";
import { Alert } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import {
  Plus,
  Compass,
  CheckCircle2,
  PauseCircle,
  Search,
  X,
  Pencil,
  Trash2,
  Image as ImageIcon,
} from "lucide-react";

import useCruiseAreas from "../hooks/useCruiseAreas";
import CruiseAreaFormModal from "../components/cruise/CruiseAreaFormModal";

import "../styles/cruise/ManagerCruise.css";

export default function CruiseArea() {
  const { deckId } = useParams();
  const navigate = useNavigate();

  const {
    areas,
    loading,
    error,
    success,
    setError,
    setSuccess,
    createArea,
    updateArea,
    deleteArea,
  } = useCruiseAreas(deckId);

  const [keyword, setKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingArea, setEditingArea] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: "",
    status: "ACTIVE",
    image: null,
  });

  // ---------- LỌC & THỐNG KÊ ----------
  const counts = useMemo(
    () => ({
      ALL: areas.length,
      ACTIVE: areas.filter((a) => a.status === "ACTIVE").length,
      INACTIVE: areas.filter((a) => a.status !== "ACTIVE").length,
    }),
    [areas],
  );

  const filteredAreas = useMemo(() => {
    const query = keyword.trim().toLowerCase();

    return areas.filter((area) => {
      if (statusFilter === "ACTIVE" && area.status !== "ACTIVE") return false;
      if (statusFilter === "INACTIVE" && area.status === "ACTIVE") return false;

      if (!query) return true;

      return (
        String(area.name || "")
          .toLowerCase()
          .includes(query) ||
        String(area.description || "")
          .toLowerCase()
          .includes(query)
      );
    });
  }, [areas, keyword, statusFilter]);

  const isFiltered = keyword.trim() !== "" || statusFilter !== "ALL";

  const clearFilter = () => {
    setKeyword("");
    setStatusFilter("ALL");
  };

  // ---------- MODAL HANDLERS ----------
  const handleOpenCreate = () => {
    setEditingArea(null);
    setForm({ name: "", description: "", status: "ACTIVE", image: null });
    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (area) => {
    setEditingArea(area);
    setForm({
      name: area.name || "",
      description: area.description || "",
      status: area.status || "ACTIVE",
      image: null,
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
    const { name, value, files } = event.target;
    setForm((previous) => ({
      ...previous,
      [name]: files ? files[0] : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (!form.name.trim()) {
      setError("Vui lòng nhập tên khu vực.");
      return;
    }

    const formData = new FormData();
    formData.append("name", form.name.trim());
    formData.append("description", form.description.trim());

    if (editingArea) {
      formData.append("status", form.status);
    }

    if (form.image) {
      formData.append("image", form.image);
    }

    setSaving(true);
    try {
      let result;
      if (!editingArea) {
        result = await createArea(formData);
      } else {
        result = await updateArea(editingArea.id, formData);
      }

      if (result) {
        setShowModal(false);
      }
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (area) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa khu vực "${area.name}" không?`,
    );
    if (!confirmed) return;
    await deleteArea(area.id);
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
          <h2>Quản lý khu vực</h2>
          <p>Quản lý các không gian, tiện ích và vị trí trên tầng du thuyền.</p>
        </div>

        <button
          type="button"
          className="mc-btn mc-btn--primary"
          onClick={handleOpenCreate}
        >
          <Plus size={18} />
          Thêm khu vực
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
      <section className="mc-stats">
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--blue">
            <Compass size={22} />
          </span>
          <div>
            <strong>{areas.length}</strong>
            <span>Tổng khu vực</span>
          </div>
        </div>
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--green">
            <CheckCircle2 size={22} />
          </span>
          <div>
            <strong>{counts.ACTIVE}</strong>
            <span>Đang hoạt động</span>
          </div>
        </div>
        <div className="mc-stat">
          <span className="mc-stat-icon mc-stat-icon--gray">
            <PauseCircle size={22} />
          </span>
          <div>
            <strong>{counts.INACTIVE}</strong>
            <span>Ngừng hoạt động</span>
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
            placeholder="Tìm theo tên khu vực hoặc mô tả..."
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
          Hiển thị <strong>{filteredAreas.length}</strong> / {areas.length} khu
          vực
        </p>
      </section>

      {/* CONTENT GRID */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Đang tải danh sách khu vực...</p>
        </div>
      ) : filteredAreas.length === 0 ? (
        <div className="mc-empty">
          <Compass size={44} />
          <h3>
            {isFiltered
              ? "Không tìm thấy khu vực phù hợp"
              : "Chưa có khu vực nào"}
          </h3>
          <p>
            {isFiltered
              ? "Thử đổi từ khóa hoặc bộ lọc trạng thái."
              : "Thêm khu vực đầu tiên (nhà hàng, quầy bar, hồ bơi...) cho tầng này."}
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
              <Plus size={16} /> Thêm khu vực ngay
            </button>
          )}
        </div>
      ) : (
        <div className="mc-grid">
          {filteredAreas.map((area) => {
            const isActive = area.status === "ACTIVE";
            return (
              <article
                key={area.id}
                className={`mc-card ${isActive ? "" : "mc-card--inactive"}`}
              >
                <div className="mc-card-media">
                  {area.imageUrl ? (
                    <img src={area.imageUrl} alt={area.name} loading="lazy" />
                  ) : (
                    <div className="mc-card-placeholder">
                      <ImageIcon size={40} />
                    </div>
                  )}
                  <span
                    className={`mc-badge ${isActive ? "mc-badge--active" : "mc-badge--inactive"}`}
                  >
                    {isActive ? "Hoạt động" : "Ngừng hoạt động"}
                  </span>
                </div>

                <div className="mc-card-body">
                  <div className="mc-card-title">
                    <h3 title={area.name}>{area.name}</h3>
                  </div>
                  <p className="mc-card-desc">
                    {area.description || "Chưa có mô tả cho khu vực này."}
                  </p>
                </div>

                <footer className="mc-card-actions">
                  <button
                    type="button"
                    className="mc-icon-btn"
                    style={{ flex: 1 }}
                    onClick={() => handleOpenEdit(area)}
                  >
                    <Pencil size={16} />
                    <span>Chỉnh sửa</span>
                  </button>

                  <button
                    type="button"
                    className="mc-icon-btn mc-icon-btn--danger"
                    onClick={() => handleDelete(area)}
                    title="Xóa khu vực"
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
      <CruiseAreaFormModal
        show={showModal}
        saving={saving}
        editingArea={editingArea}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
