import React, { useState, useMemo } from "react";
import useNfcCard from "../hooks/useNfcCard";
import NfcCardTable from "../components/nfc/NfcCardTable";
import FilterStatusNfc from "../components/nfc/FilterStatusNfc";
import NfcCardFormModal from "../components/nfc/NfcCardFormModal";
import "../styles/ManagerNfcCard.css";

export default function ManagerNfcCard() {
  const {
    cards,
    loading,
    saving,
    error,
    success,
    createCard,
    updateCard,
    deleteCard,
  } = useNfcCard();

  const [filterStatus, setFilterStatus] = useState("ALL");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCard, setEditingCard] = useState(null);

  const filteredCards = useMemo(() => {
    if (filterStatus === "ALL") return cards;
    return cards.filter((card) => card.status === filterStatus);
  }, [cards, filterStatus]);

  const handleOpenModal = (card = null) => {
    setEditingCard(card);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setEditingCard(null);
    setIsModalOpen(false);
  };

  const handleSave = async (formData) => {
    let result;
    if (editingCard) {
      result = await updateCard(editingCard.id, formData);
    } else {
      result = await createCard(formData);
    }

    if (result) {
      handleCloseModal();
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa thẻ NFC này không?")) {
      await deleteCard(id);
    }
  };

  return (
    <div className="nfc-management-page">
      <div className="nfc-header">
        <h1 className="nfc-title">Quản lý thẻ NFC</h1>
        <button onClick={() => handleOpenModal()} className="nfc-btn-primary">
          + Thêm thẻ NFC mới
        </button>
      </div>

      {error && <div className="nfc-alert-error">{error}</div>}
      {success && <div className="nfc-alert-success">{success}</div>}

      <div className="nfc-card-container">
        <div className="nfc-toolbar">
          <FilterStatusNfc
            currentFilter={filterStatus}
            onFilterChange={setFilterStatus}
          />
          <div className="nfc-total-count">
            Tổng số: <strong>{filteredCards.length}</strong> thẻ
          </div>
        </div>

        {loading ? (
          <div
            style={{ textAlign: "center", padding: "40px", color: "#6b7280" }}
          >
            Đang tải dữ liệu...
          </div>
        ) : (
          <NfcCardTable
            cards={filteredCards}
            onEdit={handleOpenModal}
            onDelete={handleDelete}
          />
        )}
      </div>

      {isModalOpen && (
        <NfcCardFormModal
          card={editingCard}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          onSave={handleSave}
          saving={saving}
        />
      )}
    </div>
  );
}
