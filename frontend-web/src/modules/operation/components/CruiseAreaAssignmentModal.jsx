import { useEffect, useMemo, useState } from "react";
import {
  X,
  Ship,
  Layers3,
  MapPin,
  CheckCircle,
  Trash2,
  AlertCircle,
  Loader2,
} from "lucide-react";
import "../styles/CruiseAreaAssignmentModal.css";

function CruiseAreaAssignmentModal({
  open,
  tour,
  cruiseLayout,
  assignments,
  layoutLoading,
  assignmentLoading,

  onLoadLayout,
  onLoadAssignments,
  onAssignArea,
  onDeleteAssignment,
  onClose,
}) {
  const [selectedAreaId, setSelectedAreaId] = useState(null);

  // =====================================================
  // LOAD DATA WHEN MODAL OPENS
  // =====================================================

  useEffect(() => {
    if (!open || !tour?.id) {
      return;
    }

    setSelectedAreaId(null);

    onLoadLayout?.(tour.id);
    onLoadAssignments?.(tour.id);
  }, [open, tour?.id, onLoadLayout, onLoadAssignments]);

  // =====================================================
  // RESET WHEN CLOSE
  // =====================================================

  useEffect(() => {
    if (!open) {
      setSelectedAreaId(null);
    }
  }, [open]);

  // =====================================================
  // ASSIGNED AREA IDS
  // =====================================================

  const assignedAreaIds = useMemo(() => {
    if (!Array.isArray(assignments)) {
      return new Set();
    }

    return new Set(
      assignments
        .map((assignment) => {
          return (
            assignment.cruiseAreaId ||
            assignment.areaId ||
            assignment.cruiseArea?.id ||
            assignment.area?.id
          );
        })
        .filter(Boolean),
    );
  }, [assignments]);

  // =====================================================
  // NORMALIZE LAYOUT
  // =====================================================

  const decks = useMemo(() => {
    if (!Array.isArray(cruiseLayout)) {
      return [];
    }

    return cruiseLayout;
  }, [cruiseLayout]);

  // =====================================================
  // GET DECK AREAS
  // =====================================================

  const getAreas = (deck) => {
    return (
      deck?.areas || deck?.cruiseAreas || deck?.areaList || deck?.items || []
    );
  };

  // =====================================================
  // GET DECK ID
  // =====================================================

  const getDeckId = (deck) => {
    return deck?.id || deck?.deckId;
  };

  // =====================================================
  // GET DECK NAME
  // =====================================================

  const getDeckName = (deck) => {
    return deck?.name || deck?.deckName || `Tầng ${deck?.number || "-"}`;
  };

  // =====================================================
  // GET AREA ID
  // =====================================================

  const getAreaId = (area) => {
    return area?.id || area?.cruiseAreaId || area?.areaId;
  };

  // =====================================================
  // GET AREA NAME
  // =====================================================

  const getAreaName = (area) => {
    return area?.name || area?.areaName || `Khu vực ${getAreaId(area) || "-"}`;
  };

  // =====================================================
  // GET AREA CODE
  // =====================================================

  const getAreaCode = (area) => {
    return area?.code || area?.areaCode || "";
  };

  // =====================================================
  // GET AREA DESCRIPTION
  // =====================================================

  const getAreaDescription = (area) => {
    return area?.description || "";
  };

  // =====================================================
  // SELECT AREA
  // =====================================================

  const handleSelectArea = (area) => {
    const areaId = getAreaId(area);

    if (!areaId) {
      return;
    }

    if (assignedAreaIds.has(areaId)) {
      return;
    }

    setSelectedAreaId(areaId);
  };

  // =====================================================
  // ASSIGN AREA
  // =====================================================

  const handleAssign = async () => {
    if (!tour?.id || !selectedAreaId) {
      return;
    }

    try {
      await onAssignArea?.(tour.id, selectedAreaId);

      setSelectedAreaId(null);

      // Đồng bộ lại assignment từ backend.
      await onLoadAssignments?.(tour.id);
    } catch (err) {
      console.error("ASSIGN AREA FROM MODAL ERROR:", err);
    }
  };

  // =====================================================
  // DELETE ASSIGNMENT
  // =====================================================

  const handleDeleteAssignment = async (assignment) => {
    const assignmentId = assignment?.id;

    if (!assignmentId) {
      return;
    }

    const areaName =
      assignment?.cruiseArea?.name ||
      assignment?.area?.name ||
      assignment?.areaName ||
      "khu vực này";

    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa phân công "${areaName}" khỏi Tour "${tour?.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await onDeleteAssignment?.(assignmentId);

      if (selectedAreaId === assignment?.cruiseAreaId) {
        setSelectedAreaId(null);
      }
    } catch (err) {
      console.error("DELETE AREA ASSIGNMENT FROM MODAL ERROR:", err);
    }
  };

  // =====================================================
  // CLOSE
  // =====================================================

  const handleClose = () => {
    if (assignmentLoading) {
      return;
    }

    setSelectedAreaId(null);

    onClose?.();
  };

  // =====================================================
  // NOT OPEN
  // =====================================================

  if (!open || !tour) {
    return null;
  }

  // =====================================================
  // LOADING
  // =====================================================

  const loading = layoutLoading || assignmentLoading;

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="operation-area-modal-overlay">
      <div className="operation-area-modal">
        {/* =================================================
            HEADER
            ================================================= */}

        <div className="operation-area-modal-header">
          <div className="operation-area-modal-header-info">
            <div className="operation-area-modal-icon">
              <Ship size={22} />
            </div>

            <div>
              <h2>Phân công khu vực cho Tour</h2>

              <p>Chọn các khu vực trên du thuyền để phân công cho Tour.</p>
            </div>
          </div>

          <button
            type="button"
            className="operation-area-modal-close"
            onClick={handleClose}
            disabled={assignmentLoading}
            title="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* =================================================
            TOUR INFORMATION
            ================================================= */}

        <div className="operation-area-tour-info">
          <div className="operation-area-tour-info-item">
            <span>Tour</span>
            <strong>{tour.name || "-"}</strong>
          </div>

          <div className="operation-area-tour-info-item">
            <span>Mã Tour</span>
            <strong>{tour.code || "-"}</strong>
          </div>

          <div className="operation-area-tour-info-item">
            <span>Du thuyền</span>
            <strong>
              {tour.cruise?.name || tour.cruise?.code || "Chưa xác định"}
            </strong>
          </div>
        </div>

        {/* =================================================
            CONTENT
            ================================================= */}

        <div className="operation-area-modal-content">
          {/* =================================================
              LEFT: CRUISE LAYOUT
              ================================================= */}

          <div className="operation-area-layout-section">
            <div className="operation-area-section-header">
              <div>
                <Layers3 size={19} />

                <h3>Sơ đồ du thuyền</h3>
              </div>

              <span>{decks.length} tầng</span>
            </div>

            {layoutLoading ? (
              <div className="operation-area-state">
                <Loader2 size={28} className="operation-area-spinner" />

                <span>Đang tải sơ đồ du thuyền...</span>
              </div>
            ) : decks.length === 0 ? (
              <div className="operation-area-state empty">
                <AlertCircle size={36} />

                <h3>Không có dữ liệu khu vực</h3>

                <p>Không tìm thấy tầng hoặc khu vực của du thuyền.</p>
              </div>
            ) : (
              <div className="operation-area-deck-list">
                {decks.map((deck, deckIndex) => {
                  const deckId = getDeckId(deck);
                  const areas = getAreas(deck);

                  return (
                    <div
                      key={deckId || deckIndex}
                      className="operation-area-deck"
                    >
                      {/* DECK HEADER */}

                      <div className="operation-area-deck-header">
                        <div className="operation-area-deck-title">
                          <Layers3 size={17} />

                          <strong>{getDeckName(deck)}</strong>
                        </div>

                        <span>{areas.length} khu vực</span>
                      </div>

                      {/* AREAS */}

                      {areas.length === 0 ? (
                        <div className="operation-area-no-area">
                          Chưa có khu vực trong tầng này.
                        </div>
                      ) : (
                        <div className="operation-area-grid">
                          {areas.map((area, areaIndex) => {
                            const areaId = getAreaId(area);

                            const assigned = assignedAreaIds.has(areaId);

                            const selected = selectedAreaId === areaId;

                            return (
                              <button
                                key={areaId || areaIndex}
                                type="button"
                                className={`operation-area-card ${
                                  selected ? "selected" : ""
                                } ${assigned ? "assigned" : ""}`}
                                onClick={() => handleSelectArea(area)}
                                disabled={
                                  assigned || assignmentLoading || !areaId
                                }
                              >
                                <div className="operation-area-card-icon">
                                  {assigned ? (
                                    <CheckCircle size={18} />
                                  ) : (
                                    <MapPin size={18} />
                                  )}
                                </div>

                                <div className="operation-area-card-info">
                                  <strong>{getAreaName(area)}</strong>

                                  {getAreaCode(area) && (
                                    <span>{getAreaCode(area)}</span>
                                  )}

                                  {getAreaDescription(area) && (
                                    <small>{getAreaDescription(area)}</small>
                                  )}
                                </div>

                                <div className="operation-area-card-status">
                                  {assigned ? (
                                    <span className="assigned">
                                      Đã phân công
                                    </span>
                                  ) : selected ? (
                                    <span className="selected">Đang chọn</span>
                                  ) : (
                                    <span>Chọn</span>
                                  )}
                                </div>
                              </button>
                            );
                          })}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* =================================================
              RIGHT: ASSIGNMENTS
              ================================================= */}

          <div className="operation-area-assignment-section">
            <div className="operation-area-section-header">
              <div>
                <CheckCircle size={19} />

                <h3>Khu vực đã phân công</h3>
              </div>

              <span>{assignments?.length || 0}</span>
            </div>

            {assignmentLoading && !layoutLoading ? (
              <div className="operation-area-assignment-loading">
                <Loader2 size={22} className="operation-area-spinner" />

                <span>Đang cập nhật...</span>
              </div>
            ) : !assignments || assignments.length === 0 ? (
              <div className="operation-area-assignment-empty">
                <MapPin size={34} />

                <h3>Chưa phân công</h3>

                <p>Chọn một khu vực bên trái để phân công cho Tour.</p>
              </div>
            ) : (
              <div className="operation-area-assignment-list">
                {assignments.map((assignment, index) => {
                  const area = assignment?.cruiseArea || assignment?.area;

                  const areaName =
                    area?.name || assignment?.areaName || "Khu vực";

                  const areaCode = area?.code || assignment?.areaCode || "";

                  const assignmentId = assignment?.id || index;

                  return (
                    <div
                      key={assignmentId}
                      className="operation-area-assignment-item"
                    >
                      <div className="operation-area-assignment-icon">
                        <MapPin size={17} />
                      </div>

                      <div className="operation-area-assignment-info">
                        <strong>{areaName}</strong>

                        {areaCode && <span>{areaCode}</span>}

                        <small>Đã phân công cho Tour</small>
                      </div>

                      <button
                        type="button"
                        className="operation-area-delete-button"
                        onClick={() => handleDeleteAssignment(assignment)}
                        disabled={assignmentLoading}
                        title="Xóa phân công"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        {/* =================================================
            FOOTER
            ================================================= */}

        <div className="operation-area-modal-footer">
          <div className="operation-area-footer-status">
            {selectedAreaId ? (
              <>
                <MapPin size={16} />

                <span>Đang chọn một khu vực</span>
              </>
            ) : (
              <span>Chọn khu vực để phân công</span>
            )}
          </div>

          <div className="operation-area-footer-actions">
            <button
              type="button"
              className="operation-area-cancel-button"
              onClick={handleClose}
              disabled={assignmentLoading}
            >
              Hủy
            </button>

            <button
              type="button"
              className="operation-area-assign-button"
              onClick={handleAssign}
              disabled={!selectedAreaId || assignmentLoading || layoutLoading}
            >
              {assignmentLoading ? (
                <>
                  <Loader2 size={17} className="operation-area-spinner" />
                  Đang xử lý...
                </>
              ) : (
                <>
                  <CheckCircle size={17} />
                  Phân công khu vực
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default CruiseAreaAssignmentModal;
