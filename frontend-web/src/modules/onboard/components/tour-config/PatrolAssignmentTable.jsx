import React, { useState } from "react";
import {
  AssignmentStatus,
  AssignmentSummary,
  EmptyState,
  LoadingState,
} from "./OnboardAssignmentShared";

function PatrolAssignmentTable({
  tour,
  decks,
  loading,
  onAssign,
  onViewAssignments,
}) {
  const [expandedDeckId, setExpandedDeckId] = useState(null);

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
      {decks.map((deck) => {
        const isExpanded = expandedDeckId === deck.id;

        const assignments = deck.assignments || [];

        return (
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
                  className="onboard-button onboard-button-outline"
                  onClick={() => setExpandedDeckId(isExpanded ? null : deck.id)}
                >
                  {isExpanded ? "Thu gọn" : "Xem phân công"}
                </button>

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
                  + Phân công
                </button>
              </div>
            </div>

            {isExpanded && (
              <div className="onboard-target-details">
                {assignments.length > 0 ? (
                  <div className="onboard-assigned-list">
                    {assignments.map((item) => (
                      <div className="onboard-assigned-row" key={item.id}>
                        <div>
                          <strong>
                            {item.fullName || item.username || item.email}
                          </strong>
                          <span>{item.email}</span>
                        </div>

                        <AssignmentStatus status={item.status} />
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="onboard-muted">
                    Chưa có dữ liệu phân công. Nhấn tải lại hoặc mở chức năng
                    phân công để kiểm tra.
                  </p>
                )}

                {onViewAssignments && (
                  <button
                    type="button"
                    className="onboard-text-button"
                    onClick={() => onViewAssignments(deck)}
                  >
                    Làm mới danh sách
                  </button>
                )}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

export default PatrolAssignmentTable;
