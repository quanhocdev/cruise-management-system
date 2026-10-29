// src/modules/operation/components/CruiseAreaAssignmentModal.jsx

import React from "react";
import { X, Ship, CheckCircle, AlertCircle, Loader2, Save } from "lucide-react";

import useCruiseAreaAssignment from "../hooks/useCruiseAreaAssignment";

import AreaFilterToolbar from "./AreaFilterToolbar";
import AreaDetailPreview from "./AreaDetailPreview";
import DeckSidebar from "./DeckSidebar";
import AreaItemCard from "./AreaItemCard";

import "../styles/CruiseAreaAssignmentModal.css";

function CruiseAreaAssignmentModal(props) {
  const { open, tour, layoutLoading = false } = props;

  const a = useCruiseAreaAssignment(props);

  if (!open || !tour) return null;

  return (
    <div className="caam-overlay">
      <div className="caam-container">
        {/* HEADER */}
        <div className="caam-header">
          <div className="caam-header-title">
            <div className="caam-icon-badge">
              <Ship size={22} />
            </div>

            <div>
              <h2>Phân công khu vực / phòng cho Tour</h2>
              <p>
                Chọn tầng, phân công khu vực hoặc phòng và thiết lập nhân viên
                tuần tra
              </p>
            </div>
          </div>

          <button
            type="button"
            className="caam-close-btn"
            onClick={a.handleClose}
            disabled={a.isGlobalLoading || a.patrolLoading}
          >
            <X size={20} />
          </button>
        </div>

        {/* TOUR DETAILS BAR */}
        <div className="caam-tour-bar">
          <div className="caam-tour-info">
            <span className="label">Tour:</span>
            <strong className="value">{tour.name || "-"}</strong>
          </div>

          <div className="caam-tour-info">
            <span className="label">Mã Tour:</span>
            <strong className="value">{tour.code || "-"}</strong>
          </div>

          <div className="caam-tour-info">
            <span className="label">Du thuyền:</span>
            <strong className="value">{a.cruiseName}</strong>
          </div>
        </div>

        {/* MAIN BODY */}
        <div className="caam-body">
          {/* LEFT: DECK SIDEBAR */}
          <DeckSidebar
            decks={a.decks}
            selectedDeckId={a.selectedDeckId}
            onSelectDeck={a.setSelectedDeckId}
            totalItemsCount={a.totalItemsCount}
            totalAssignedCount={a.totalAssignedCount}
            deckStatsMap={a.deckStatsMap}
            loading={layoutLoading}
          />

          {/* RIGHT: PATROL + AREA ASSIGNMENT */}
          <div className="caam-main-content">
            {/* PATROL REQUIREMENTS */}
            <section className="caam-patrol-panel">
              <div className="caam-patrol-header">
                <div>
                  <h3>Yêu cầu nhân viên tuần tra</h3>
                  <p>
                    Cấu hình số lượng nhân viên ONBOARD cần tuần tra tại từng
                    tầng.
                  </p>
                </div>

                <div className="caam-patrol-total">
                  Tổng nhân viên:
                  <strong>{a.totalPatrolRequired}</strong>
                </div>
              </div>

              {a.patrolLoading ? (
                <div className="caam-patrol-state">
                  <Loader2 size={18} className="caam-spinner" />
                  <span>Đang tải yêu cầu tuần tra...</span>
                </div>
              ) : layoutLoading ? (
                <div className="caam-patrol-state">
                  <Loader2 size={18} className="caam-spinner" />
                  <span>Đang tải danh sách tầng...</span>
                </div>
              ) : a.patrolError && a.patrolDecks.length === 0 ? (
                <div className="caam-patrol-error">{a.patrolError}</div>
              ) : a.patrolDecks.length === 0 ? (
                <div className="caam-patrol-state">
                  Chưa có dữ liệu tầng của du thuyền.
                </div>
              ) : (
                <div className="caam-patrol-grid">
                  {a.patrolDecks.map((deck) => (
                    <div className="caam-patrol-item" key={deck.cruiseDeckId}>
                      <label htmlFor={`patrol-count-${deck.cruiseDeckId}`}>
                        {deck.deckName}
                      </label>

                      <input
                        id={`patrol-count-${deck.cruiseDeckId}`}
                        type="number"
                        min="0"
                        step="1"
                        value={deck.requiredCount}
                        disabled={a.patrolSaving}
                        onChange={(event) =>
                          a.updatePatrolRequirement(
                            deck.cruiseDeckId,
                            event.target.value,
                          )
                        }
                      />

                      <span>Nhân viên</span>
                    </div>
                  ))}
                </div>
              )}

              {a.patrolError && a.patrolDecks.length > 0 && (
                <div className="caam-patrol-error">{a.patrolError}</div>
              )}

              {a.patrolSuccess && (
                <div className="caam-patrol-success">
                  <CheckCircle size={16} />
                  <span>{a.patrolSuccess}</span>
                </div>
              )}

              <div className="caam-patrol-footer">
                <span>
                  {a.patrolDirty
                    ? "Bạn có thay đổi chưa được lưu."
                    : "Yêu cầu tuần tra đã được tải."}
                </span>

                <button
                  type="button"
                  className="caam-patrol-save-btn"
                  onClick={a.savePatrolRequirements}
                  disabled={
                    a.patrolLoading ||
                    a.patrolSaving ||
                    layoutLoading ||
                    !a.patrolDirty ||
                    a.patrolDecks.length === 0
                  }
                >
                  {a.patrolSaving ? (
                    <>
                      <Loader2 size={16} className="caam-spinner" />
                      Đang lưu...
                    </>
                  ) : (
                    <>
                      <Save size={16} />
                      Lưu yêu cầu tuần tra
                    </>
                  )}
                </button>
              </div>
            </section>

            {/* EXISTING AREA ASSIGNMENT */}
            <AreaFilterToolbar
              searchTerm={a.searchTerm}
              setSearchTerm={a.setSearchTerm}
              viewType={a.viewType}
              setViewType={a.setViewType}
            />

            <div
              className={`caam-split-workspace ${
                a.selectedAreaId ? "has-preview" : ""
              }`}
            >
              <div className="caam-grid-container">
                {layoutLoading ? (
                  <div className="caam-state-box">
                    <Loader2 size={28} className="caam-spinner" />
                    <span>Đang tải dữ liệu...</span>
                  </div>
                ) : a.rows.length === 0 ? (
                  <div className="caam-state-box empty">
                    <AlertCircle size={32} />
                    <p>
                      {a.searchTerm
                        ? "Không tìm thấy khu vực hoặc phòng phù hợp"
                        : "Không có dữ liệu phù hợp với bộ lọc"}
                    </p>
                  </div>
                ) : (
                  <div className="caam-area-grid">
                    {a.rows.map(({ key, item, assignment, isSelected }) => (
                      <AreaItemCard
                        key={key}
                        item={item}
                        assignment={assignment}
                        isSelected={isSelected}
                        onSelect={a.handleSelectItem}
                        onDelete={a.handleDeleteAssignment}
                        disabled={a.isGlobalLoading}
                      />
                    ))}
                  </div>
                )}
              </div>

              {a.selectedAreaId && (
                <div className="caam-preview-sidebar">
                  <AreaDetailPreview
                    area={a.selectedItem}
                    isAssigned={Boolean(a.selectedAssignment)}
                    assignedType={a.selectedAssignment?.type || null}
                    selectedConfigType={a.selectedConfigType}
                    onChangeConfigType={a.setSelectedConfigType}
                    onSaveAssignment={a.handleAssign}
                    onUnassign={a.handleUnassign}
                    loading={a.isGlobalLoading}
                  />
                </div>
              )}
            </div>
          </div>
        </div>

        {/* FOOTER */}
        <div className="caam-footer">
          <div className="caam-footer-info">
            {a.selectedAreaId ? (
              <span className="info-selected">
                <CheckCircle size={16} />
                Đã chọn: {a.selectedItemName} ({a.selectedConfigType})
              </span>
            ) : (
              <span className="info-hint">
                Chọn một khu vực hoặc phòng ở trên để xem hình ảnh và phân công
              </span>
            )}
          </div>

          <div className="caam-footer-buttons">
            <button
              type="button"
              className="caam-btn caam-btn-cancel"
              onClick={a.handleClose}
              disabled={a.isGlobalLoading || a.patrolLoading}
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CruiseAreaAssignmentModal;
