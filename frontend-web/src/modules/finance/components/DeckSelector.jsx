import React, { useEffect } from "react";
import { useDecksByCruise } from "../hooks/useFinanceCruise";
import "./DeckSelector.css";

const DeckSelector = ({ cruiseId, selectedDeckId, onSelectDeck }) => {
  const { decks, loading } = useDecksByCruise(cruiseId);

  // Đổi tour/tàu thì bỏ deck đang chọn nếu không còn thuộc tàu mới
  useEffect(() => {
    if (selectedDeckId && !decks.some((d) => d.id === selectedDeckId)) {
      onSelectDeck("");
    }
  }, [decks, selectedDeckId, onSelectDeck]);

  return (
    <div className="deck-selector-container">
      <label htmlFor="deck-select">
        <strong>Chọn tầng: </strong>
      </label>
      <select
        id="deck-select"
        value={selectedDeckId || ""}
        onChange={(e) => onSelectDeck(e.target.value)}
        disabled={!cruiseId || loading}
      >
        <option value="">
          {!cruiseId
            ? "-- Chọn Tour trước --"
            : loading
              ? "Đang tải..."
              : "-- Chọn một tầng --"}
        </option>
        {decks.map((deck) => (
          <option key={deck.id} value={deck.id}>
            Tầng {deck.deckNumber}
          </option>
        ))}
      </select>
    </div>
  );
};

export default DeckSelector;
