// src/modules/admin/pages/CruiseDeck.jsx
import { useMemo, useState } from "react";
import { Alert } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import {
  Plus,
  Layers,
  CheckCircle2,
  PauseCircle,
  Search,
  X,
  Pencil,
  Trash2,
  Eye,
} from "lucide-react";

import useCruiseDecks from "../hooks/useCruiseDecks";
import CruiseDeckFormModal from "../components/cruise/CruiseDeckFormModal";

import "../styles/cruise/ManagerCruise.css"; // Dùng chung hệ thống CSS hiện đại

export default function CruiseDeck() {
  const { cruiseId } = useParams();
  const navigate = useNavigate();

  const {
    decks,
    loading,
    error,
    success,
    setError,
    setSuccess,
    createDecks,
    updateDeck,
    deleteDeck,
  } = useCruiseDecks(cruiseId);

  const [keyword, setKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingDeck, setEditingDeck] = useState(null);

  const [form, setForm] = useState({
    totalDecks: "",
    deckNumber: "",
    status: "ACTIVE",
  });

  // ---------- LỌC & THỐNG KÊ ----------
  const counts = useMemo(
    () => ({
      ALL: decks.length,
      ACTIVE: decks.filter((d) => d.status === "ACTIVE").length,
      INACTIVE: decks.filter((d) => d.status !== "ACTIVE").length,
    }),
    [decks],
  );

  const filteredDecks = useMemo(() => {
    const query = keyword.trim().toLowerCase();

    return decks.filter((deck) => {
      if (statusFilter === "ACTIVE" && deck.status !== "ACTIVE") return false;
      if (statusFilter === "INACTIVE" && deck.status === "ACTIVE") return false;

      if (!query) return true;

      return String(`Tầng ${deck.deckNumber}`).toLowerCase().includes(query);
    });
  }, [decks, keyword, statusFilter]);

  const isFiltered = keyword.trim() !== "" || statusFilter !== "ALL";

  const clearFilter = () => {
    setKeyword("");
    setStatusFilter("ALL");
  };

  // ---------- MODAL HANDLERS ----------
  const handleOpenCreate = () => {
    setEditingDeck(null);
    setForm({ totalDecks: "", status: "ACTIVE" });
    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (deck) => {
    setEditingDeck(deck);
    setForm({
      totalDecks: String(deck.deckNumber || ""),
      deckNumber: deck.deckNumber,
      status: deck.status || "ACTIVE",
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

    if (!editingDeck) {
      const totalDecks = Number(form.totalDecks);
      if (
        !form.totalDecks ||
        !Number.isInteger(totalDecks) ||
        totalDecks <= 0
      ) {
        setError("Tổng số tầng phải là số nguyên lớn hơn 0.");
        return;
      }

      setSaving(true);
      try {
        const result = await createDecks(totalDecks);
        if (result) setShowModal(false);
      } finally {
        setSaving(false);
      }
      return;
    }

    const deckNumber = Number(form.totalDecks);
    if (!form.totalDecks || !Number.isInteger(deckNumber) || deckNumber <= 0) {
      setError("Số tầng phải là số nguyên lớn hơn 0.");
      return;
    }

    setSaving(true);
    try {
      const result = await updateDeck(editingDeck.id, {
        deckNumber,
        status: form.status,
      });
      if (result) setShowModal(false);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (deck) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa tầng ${deck.deckNumber} không?`,
    );
    if (!confirmed) return;
    await deleteDeck(deck.id);
  };

  return (
    <div className="mc-page container-fluid py-4">
      {/* HEADER */}
      <header className="mc-header">
        <div>
          <button
            type="button"
            className="btn btn-link px-0 mb-2 text-decoration-none"
            onClick={() => navigate("/admin/cruises")}
          >
            ← Quay lại danh sách du thuyền
          </button>
          <h2>Quản lý tầng du thuyền</h2>
          <p>Quản lý các tầng, cấu trúc không gian và trạng thái hoạt động.</p>
        </div>

        <button
          type="button"
          className="mc-btn mc-btn--primary"
          onClick={handleOpenCreate}
        >
          <Plus size={18} />
          Thêm tầng
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
            <Layers size={22} />
          </span>
          <div>
            <strong>{decks.length}</strong>
            <span>Tổng số tầng</span>
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
            placeholder="Tìm kiếm tầng (Ví dụ: Tầng 1)..."
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
          Hiển thị <strong>{filteredDecks.length}</strong> / {decks.length} tầng
        </p>
      </section>

      {/* CONTENT GRID */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status" />
          <p className="mt-2 text-muted">Đang tải danh sách tầng...</p>
        </div>
      ) : filteredDecks.length === 0 ? (
        <div className="mc-empty">
          <Layers size={44} />
          <h3>
            {isFiltered ? "Không tìm thấy tầng phù hợp" : "Chưa có tầng nào"}
          </h3>
          <p>
            {isFiltered
              ? "Thử đổi từ khóa hoặc bộ lọc trạng thái."
              : "Thêm tầng để bắt đầu phân chia khu vực và phòng cho du thuyền."}
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
              <Plus size={16} /> Thêm tầng ngay
            </button>
          )}
        </div>
      ) : (
        <div className="mc-grid">
          {filteredDecks.map((deck, index) => {
            const isActive = deck.status === "ACTIVE";
            return (
              <article
                key={deck.id}
                className={`mc-card ${isActive ? "" : "mc-card--inactive"}`}
              >
                <div className="mc-card-body">
                  <div className="mc-card-title">
                    <h3>Tầng {deck.deckNumber}</h3>
                    <span
                      className={`mc-badge ${isActive ? "mc-badge--active" : "mc-badge--inactive"}`}
                    >
                      {isActive ? "Hoạt động" : "Ngừng hoạt động"}
                    </span>
                  </div>
                  <p className="mc-card-desc">
                    Thứ tự STT hiển thị: #{index + 1}
                  </p>
                </div>

                <footer className="mc-card-actions">
                  <button
                    type="button"
                    className="mc-btn mc-btn--primary"
                    onClick={() => navigate(`/admin/decks/${deck.id}`)}
                  >
                    <Eye size={16} />
                    Xem chi tiết
                  </button>

                  <button
                    type="button"
                    className="mc-icon-btn"
                    onClick={() => handleOpenEdit(deck)}
                    title="Chỉnh sửa"
                  >
                    <Pencil size={17} />
                  </button>

                  <button
                    type="button"
                    className="mc-icon-btn mc-icon-btn--danger"
                    onClick={() => handleDelete(deck)}
                    title="Xóa"
                  >
                    <Trash2 size={17} />
                  </button>
                </footer>
              </article>
            );
          })}
        </div>
      )}

      {/* FORM MODAL */}
      <CruiseDeckFormModal
        show={showModal}
        saving={saving}
        editingDeck={editingDeck}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
