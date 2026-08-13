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
    createDeck,
    updateDeck,
    deleteDeck,
  } = useCruiseDecks(cruiseId);

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingDeck, setEditingDeck] = useState(null);

  const [form, setForm] = useState({
    deckNumber: "",
    status: "ACTIVE",
  });

  const handleOpenCreate = () => {
    setEditingDeck(null);

    setForm({
      deckNumber: "",
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (deck) => {
    setEditingDeck(deck);

    setForm({
      deckNumber: deck.deckNumber ?? "",
      status: deck.status || "ACTIVE",
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

    if (!form.deckNumber || Number(form.deckNumber) <= 0) {
      setError("Số tầng phải lớn hơn 0.");
      return;
    }

    setSaving(true);

    try {
      let result;

      if (!editingDeck) {
        result = await createDeck({
          deckNumber: Number(form.deckNumber),
        });
      } else {
        result = await updateDeck(editingDeck.id, {
          deckNumber: Number(form.deckNumber),
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

  const handleDelete = async (deck) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa tầng ${deck.deckNumber} không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteDeck(deck.id);
  };

  const handleAreas = (deck) => {
    navigate(`/admin/decks/${deck.id}/areas`);
  };

  const handleRooms = (deck) => {
    navigate(`/admin/decks/${deck.id}/rooms`);
  };

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

      <CruiseDeckTable
        decks={decks}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
        onAreas={handleAreas}
        onRooms={handleRooms}
      />

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
