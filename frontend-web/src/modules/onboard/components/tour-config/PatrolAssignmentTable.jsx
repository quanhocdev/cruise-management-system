import React from "react";
import { EmptyState, LoadingState } from "./OnboardAssignmentShared";

function PatrolAssignmentTable({ tour, decks, loading, onAssign }) {
  if (loading) {
    return <LoadingState>Đang tải danh sách tầng boong...</LoadingState>;
  }

  if (!decks.length) {
    return (
      <EmptyState>
        Du thuyền của Tour này chưa có tầng boong đang hoạt động.
      </EmptyState>
    );
  }

  return (
    <div className="onboard-target-list">
      {decks.map((deck) => (
        <div className="onboard-target-card" key={deck.id}>
          <div className="onboard-target-main">
            <div className="onboard-target-info">
              <div className="onboard-target-icon">🛳️</div>

              <div>
                <h3>Tầng {deck.deckNumber}</h3>
                <p>{tour?.cruiseName || "Du thuyền"} · Tuần tra an ninh</p>
              </div>
            </div>

            <div className="onboard-target-actions">
              <button
                type="button"
                className="onboard-button onboard-button-primary"
                onClick={() =>
                  onAssign({
                    tourId: tour.id,
                    cruiseDeckId: deck.id,
                    title: `Tuần tra - Tầng ${deck.deckNumber}`,
                    subtitle: tour.cruiseName,
                  })
                }
              >
                + Phân công nhân viên
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default PatrolAssignmentTable;
