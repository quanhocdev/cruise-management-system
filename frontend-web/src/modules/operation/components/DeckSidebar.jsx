// src/modules/operation/components/DeckSidebar.jsx
import React from "react";
import { Layers, Grid, Loader2 } from "lucide-react";

const getDeckId = (deck) => deck?.deckId || deck?.id;

const getDeckName = (deck) => {
  if (deck?.name) return deck.name;
  if (deck?.deckName) return deck.deckName;
  const deckNum =
    deck?.deckNumber ??
    deck?.deckOrder ??
    deck?.number ??
    deck?.level ??
    deck?.floor;
  return deckNum !== undefined && deckNum !== null
    ? `Tầng ${deckNum}`
    : "Tầng -";
};

function DeckSidebar({
  decks,
  selectedDeckId,
  onSelectDeck,
  totalItemsCount,
  totalAssignedCount,
  deckStatsMap,
  loading,
}) {
  return (
    <div className="caam-sidebar">
      <div className="caam-sidebar-header">
        <Layers size={18} />
        <span>Danh sách Tầng ({decks.length})</span>
      </div>

      {loading ? (
        <div className="caam-state-inline">
          <Loader2 size={20} className="caam-spinner" />
          <span>Đang tải...</span>
        </div>
      ) : decks.length === 0 ? (
        <div className="caam-empty-small">Không có dữ liệu tầng</div>
      ) : (
        <div className="caam-deck-nav">
          <button
            type="button"
            className={`caam-deck-item ${selectedDeckId === "ALL" ? "active" : ""}`}
            onClick={() => onSelectDeck("ALL")}
          >
            <div className="caam-deck-name">
              <Grid size={16} />
              <span>Tất cả các tầng</span>
            </div>
            <div className="caam-deck-badges">
              <span className="badge-total">{totalItemsCount} mục</span>
              {totalAssignedCount > 0 && (
                <span className="badge-assigned" title="Số mục đã gán">
                  {totalAssignedCount} đã gán
                </span>
              )}
            </div>
          </button>

          {decks.map((deck, idx) => {
            const dId = getDeckId(deck) || idx;
            const isSelected = String(dId) === String(selectedDeckId);
            const stats = deckStatsMap.get(String(dId)) || {
              total: 0,
              assigned: 0,
            };

            return (
              <button
                key={`deck-${dId}-${idx}`}
                type="button"
                className={`caam-deck-item ${isSelected ? "active" : ""}`}
                onClick={() => onSelectDeck(dId)}
              >
                <div className="caam-deck-name">
                  <Layers size={16} />
                  <span>{getDeckName(deck)}</span>
                </div>
                <div className="caam-deck-badges">
                  <span className="badge-total">{stats.total} mục</span>
                  {stats.assigned > 0 && (
                    <span className="badge-assigned" title="Số mục đã gán">
                      {stats.assigned} đã gán
                    </span>
                  )}
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default React.memo(DeckSidebar);
