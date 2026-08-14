import { useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

import useCruiseDecks from "../hooks/useCruiseDecks";
import CruiseDeckTable from "../components/cruise/CruiseDeckTable";
import CruiseDeckFormModal from "../components/cruise/CruiseDeckFormModal";

import "../styles/cruise/CruiseDeck.css";

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

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingDeck, setEditingDeck] = useState(null);

  // =====================================================
  // FORM
  // =====================================================

  const [form, setForm] = useState({
    totalDecks: "",
    status: "ACTIVE",
  });

  // =====================================================
  // CREATE
  // =====================================================

  const handleOpenCreate = () => {
    setEditingDeck(null);

    setForm({
      totalDecks: "",
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  // =====================================================
  // EDIT
  // =====================================================

  const handleOpenEdit = (deck) => {
    setEditingDeck(deck);

    setForm({
      totalDecks: String(deck.deckNumber ?? ""),
      status: deck.status || "ACTIVE",
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
    // CREATE MULTIPLE DECKS
    // ===================================================

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

        if (result) {
          setShowModal(false);
        }
      } finally {
        setSaving(false);
      }

      return;
    }

    // ===================================================
    // UPDATE ONE DECK
    // ===================================================

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

  const handleDelete = async (deck) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa tầng ${deck.deckNumber} không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteDeck(deck.id);
  };

  const handleView = (deck) => {
    navigate(`/admin/decks/${deck.id}`);
  };
  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="cruise-deck-page container-fluid py-4">
      <div className="cruise-deck-header mb-4">
        <div>
          <Button
            variant="link"
            className="px-0 mb-2"
            onClick={() => navigate("/admin/cruises")}
          >
            ← Quay lại du thuyền
          </Button>

          <h2 className="cruise-deck-title">Quản lý tầng</h2>

          <p className="cruise-deck-description">
            Quản lý các tầng của du thuyền.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Thêm tầng
        </Button>
      </div>

      {/* SUCCESS */}

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {/* ERROR */}

      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {/* TABLE */}

      <CruiseDeckTable
        decks={decks}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
        onView={handleView}
      />

      {/* MODAL */}

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
