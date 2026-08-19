import React, { useEffect, useMemo, useState, useCallback } from "react";
import {
  X,
  Ship,
  Layers,
  CheckCircle,
  Trash2,
  AlertCircle,
  Loader2,
  DoorClosed,
  Grid,
} from "lucide-react";
import AreaFilterToolbar from "./AreaFilterToolbar";
import AreaDetailPreview from "./AreaDetailPreview";
import "../styles/CruiseAreaAssignmentModal.css";

// HELPER FUNCTIONS
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

const getItemId = (item) =>
  item?.id ||
  item?.areaId ||
  item?.cruiseAreaId ||
  item?.roomId ||
  item?.cabinId;

const getItemName = (item) =>
  item?.name ||
  item?.areaName ||
  item?.roomNumber ||
  item?.cabinNumber ||
  item?.title ||
  (item?._type === "ROOM"
    ? `Phòng ${getItemId(item) || "-"}`
    : `Khu vực ${getItemId(item) || "-"}`);

const getItemCode = (item) =>
  item?.code || item?.areaCode || item?.roomCode || "";

function CruiseAreaAssignmentModal({
  open,
  tour,
  cruiseLayout,
  assignments = [],
  layoutLoading = false,
  assignmentLoading = false,
  onLoadLayout,
  onLoadAssignments,
  onAssignArea,
  onDeleteAssignment,
  onClose,
}) {
  const [selectedDeckId, setSelectedDeckId] = useState("ALL");
  const [selectedAreaId, setSelectedAreaId] = useState(null);
  const [selectedConfigType, setSelectedConfigType] = useState("ACTIVITY");
  const [searchTerm, setSearchTerm] = useState("");
  const [viewType, setViewType] = useState("ALL"); // 'ALL' | 'AREA' | 'ROOM'
  const [isDeleting, setIsDeleting] = useState(false); // Quản lý trạng thái xóa riêng

  // NORMALIZE DECKS
  const decks = useMemo(() => {
    if (Array.isArray(cruiseLayout)) return cruiseLayout;
    if (Array.isArray(cruiseLayout?.decks)) return cruiseLayout.decks;
    if (Array.isArray(cruiseLayout?.data)) return cruiseLayout.data;
    return [];
  }, [cruiseLayout]);

  // RESET STATE & LOAD DATA
  useEffect(() => {
    if (open && tour?.id) {
      setSelectedAreaId(null);
      setSelectedConfigType("ACTIVITY");
      setSearchTerm("");
      setSelectedDeckId("ALL");
      setViewType("ALL");

      onLoadLayout?.(tour.id);
      onLoadAssignments?.(tour.id);
    }
  }, [open, tour?.id, onLoadLayout, onLoadAssignments]);

  // MAP PHÂN CÔNG (UUID String Key -> Assignment Object)
  const assignmentMap = useMemo(() => {
    if (!Array.isArray(assignments)) return new Map();
    const map = new Map();
    assignments.forEach((item) => {
      const id =
        item.cruiseAreaId ||
        item.areaId ||
        item.cruiseArea?.id ||
        item.area?.id ||
        item.roomId ||
        item.cabinId;
      if (id !== undefined && id !== null) {
        map.set(String(id), item);
      }
    });
    return map;
  }, [assignments]);

  const getCruiseName = useCallback(() => {
    if (tour?.cruise?.name) return tour.cruise.name;
    if (tour?.cruiseName) return tour.cruiseName;
    if (cruiseLayout?.cruise?.name) return cruiseLayout.cruise.name;
    if (cruiseLayout?.cruiseName) return cruiseLayout.cruiseName;
    if (Array.isArray(assignments) && assignments.length > 0) {
      const first = assignments[0];
      const cruise = first?.cruiseArea?.cruise || first?.cruise;
      if (cruise?.name) return cruise.name;
    }
    return "Chưa xác định";
  }, [tour, cruiseLayout, assignments]);

  // TỔNG HỢP AREAS VÀ ROOMS
  const allItemsWithDeckInfo = useMemo(() => {
    const list = [];
    decks.forEach((deck) => {
      const dId = getDeckId(deck);
      const dName = getDeckName(deck);

      const rawAreas = deck?.areas || deck?.cruiseAreas || deck?.areaList || [];
      rawAreas.forEach((area) => {
        list.push({
          ...area,
          _type: "AREA",
          _deckId: dId,
          _deckName: dName,
        });
      });

      const rawRooms =
        deck?.rooms || deck?.cabins || deck?.roomList || deck?.cabinList || [];
      rawRooms.forEach((room) => {
        list.push({
          ...room,
          _type: "ROOM",
          _deckId: dId,
          _deckName: dName,
        });
      });
    });

    return list;
  }, [decks]);

  // THỐNG KÊ THEO TẦNG
  const deckStatsMap = useMemo(() => {
    const stats = new Map();
    allItemsWithDeckInfo.forEach((item) => {
      const key = String(item._deckId);
      const current = stats.get(key) || { total: 0, assigned: 0 };
      current.total += 1;
      if (assignmentMap.has(String(getItemId(item)))) {
        current.assigned += 1;
      }
      stats.set(key, current);
    });
    return stats;
  }, [allItemsWithDeckInfo, assignmentMap]);

  const selectedItemObject = useMemo(() => {
    if (!selectedAreaId) return null;
    return allItemsWithDeckInfo.find(
      (item) => String(getItemId(item)) === String(selectedAreaId),
    );
  }, [selectedAreaId, allItemsWithDeckInfo]);

  const totalAssignedCount = useMemo(() => {
    return allItemsWithDeckInfo.filter((item) =>
      assignmentMap.has(String(getItemId(item))),
    ).length;
  }, [allItemsWithDeckInfo, assignmentMap]);

  // LỌC DANH SÁCH
  const filteredItems = useMemo(() => {
    let result = allItemsWithDeckInfo;

    if (selectedDeckId !== "ALL") {
      result = result.filter(
        (item) => String(item._deckId) === String(selectedDeckId),
      );
    }

    if (viewType === "ROOM") {
      result = result.filter((item) => item._type === "ROOM");
    } else if (viewType === "AREA") {
      result = result.filter((item) => item._type === "AREA");
    }

    if (searchTerm.trim()) {
      const query = searchTerm.toLowerCase().trim();
      result = result.filter((item) => {
        const name = getItemName(item).toLowerCase();
        const code = getItemCode(item).toLowerCase();
        const deckName = (item._deckName || "").toLowerCase();
        return (
          name.includes(query) ||
          code.includes(query) ||
          deckName.includes(query)
        );
      });
    }

    return result;
  }, [allItemsWithDeckInfo, selectedDeckId, viewType, searchTerm]);

  // HANDLERS
  const handleSelectItem = (item) => {
    const itemId = getItemId(item);
    if (!itemId) return;

    if (String(selectedAreaId) === String(itemId)) {
      setSelectedAreaId(null);
    } else {
      setSelectedAreaId(itemId);
      if (!selectedConfigType) setSelectedConfigType("ACTIVITY");
    }
  };

  const handleAssign = async () => {
    const rawTourId = tour?.id ?? tour?.tourId;
    const rawAreaId =
      typeof selectedAreaId === "object" ? selectedAreaId?.id : selectedAreaId;

    const tourIdStr = rawTourId ? String(rawTourId).trim() : "";
    const areaIdStr = rawAreaId ? String(rawAreaId).trim() : "";

    if (!tourIdStr || !areaIdStr) {
      console.error("❌ Thiếu tourId hoặc cruiseAreaId:", {
        tourIdStr,
        areaIdStr,
      });
      return;
    }

    const payload = {
      tourId: tourIdStr,
      cruiseAreaId: areaIdStr,
      configType: selectedConfigType,
    };

    try {
      await onAssignArea?.(payload);
      setSelectedAreaId(null);
      await onLoadAssignments?.(tour.id);
    } catch (err) {
      console.error("Lỗi phân công:", err);
    }
  };

  const handleDeleteAssignment = async (e, cruiseAreaId) => {
    if (e && typeof e.stopPropagation === "function") {
      e.stopPropagation();
    }

    const currentTourId = tour?.id || tour?.tourId;

    console.log("🚀 Gọi DELETE với:", { tourId: currentTourId, cruiseAreaId });

    if (!currentTourId || !cruiseAreaId || cruiseAreaId === "undefined") {
      console.error("❌ Thiếu tourId hoặc cruiseAreaId hợp lệ!", {
        currentTourId,
        cruiseAreaId,
      });
      alert("Không thể xóa do thông tin khu vực hoặc tour không hợp lệ.");
      return;
    }

    if (!window.confirm("Bạn có chắc chắn muốn xóa phân công này?")) {
      return;
    }

    try {
      setIsDeleting(true);

      // TRUYỀN ĐỦ 2 THAM SỐ: tourId VÀ cruiseAreaId
      await onDeleteAssignment?.(currentTourId, cruiseAreaId);

      // Tải lại danh sách phân công mới
      await onLoadAssignments?.(currentTourId);
    } catch (err) {
      console.error("Lỗi khi xóa phân công:", err);
    } finally {
      setIsDeleting(false);
    }
  };
  const handleClose = () => {
    if (assignmentLoading || isDeleting) return;
    onClose?.();
  };

  if (!open || !tour) return null;

  const isGlobalLoading = assignmentLoading || isDeleting;

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
                Chọn tầng và phân công khu vực hoặc phòng nghỉ cho Tour hiện tại
              </p>
            </div>
          </div>
          <button
            type="button"
            className="caam-close-btn"
            onClick={handleClose}
            disabled={isGlobalLoading}
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
            <strong className="value">{getCruiseName()}</strong>
          </div>
        </div>

        {/* MAIN BODY */}
        <div className="caam-body">
          {/* SIDEBAR TẦNG */}
          <div className="caam-sidebar">
            <div className="caam-sidebar-header">
              <Layers size={18} />
              <span>Danh sách Tầng ({decks.length})</span>
            </div>

            {layoutLoading ? (
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
                  className={`caam-deck-item ${
                    selectedDeckId === "ALL" ? "active" : ""
                  }`}
                  onClick={() => setSelectedDeckId("ALL")}
                >
                  <div className="caam-deck-name">
                    <Grid size={16} />
                    <span>Tất cả các tầng</span>
                  </div>
                  <div className="caam-deck-badges">
                    <span className="badge-total">
                      {allItemsWithDeckInfo.length} mục
                    </span>
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
                      onClick={() => setSelectedDeckId(dId)}
                    >
                      <div className="caam-deck-name">
                        <Layers size={16} />
                        <span>{getDeckName(deck)}</span>
                      </div>
                      <div className="caam-deck-badges">
                        <span className="badge-total">{stats.total} mục</span>
                        {stats.assigned > 0 && (
                          <span
                            className="badge-assigned"
                            title="Số mục đã gán"
                          >
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

          {/* CONTENT CHÍNH */}
          <div className="caam-main-content">
            <AreaFilterToolbar
              searchTerm={searchTerm}
              setSearchTerm={setSearchTerm}
              viewType={viewType}
              setViewType={setViewType}
            />

            <div
              className={`caam-split-workspace ${
                selectedAreaId ? "has-preview" : ""
              }`}
            >
              {/* GRID */}
              <div className="caam-grid-container">
                {layoutLoading ? (
                  <div className="caam-state-box">
                    <Loader2 size={28} className="caam-spinner" />
                    <span>Đang tải dữ liệu...</span>
                  </div>
                ) : filteredItems.length === 0 ? (
                  <div className="caam-state-box empty">
                    <AlertCircle size={32} />
                    <p>
                      {searchTerm
                        ? "Không tìm thấy khu vực hoặc phòng phù hợp"
                        : "Không có dữ liệu phù hợp với bộ lọc"}
                    </p>
                  </div>
                ) : (
                  <div className="caam-area-grid">
                    {filteredItems.map((item) => {
                      const itemId = getItemId(item);
                      const assignment = assignmentMap.get(String(itemId));
                      const isAssigned = Boolean(assignment);
                      const isSelected =
                        String(selectedAreaId) === String(itemId);
                      const isArea = item._type === "AREA";

                      return (
                        <div
                          key={`${item._type}-${itemId}`}
                          className={`caam-card ${
                            isArea ? "is-area" : "is-room"
                          } ${isAssigned ? "assigned" : ""} ${
                            isSelected ? "selected" : ""
                          }`}
                          onClick={() => handleSelectItem(item)}
                        >
                          <div className="caam-card-header">
                            <span
                              className={`caam-card-type ${
                                isArea ? "type-area" : "type-room"
                              }`}
                            >
                              {isArea ? (
                                <Grid size={16} />
                              ) : (
                                <DoorClosed size={16} />
                              )}
                              {isArea ? "Khu vực" : "Phòng nghỉ"}
                            </span>

                            {isAssigned && (
                              <button
                                type="button"
                                className="caam-delete-btn"
                                onClick={(e) =>
                                  handleDeleteAssignment(e, itemId)
                                }
                                title="Hủy phân công"
                                disabled={isGlobalLoading}
                              >
                                <Trash2 size={16} />
                              </button>
                            )}
                          </div>

                          <div className="caam-card-body">
                            <h4>{getItemName(item)}</h4>
                            <p className="deck-info">{item._deckName}</p>

                            {!isArea && (
                              <span className="room-status-badge available">
                                Trống
                              </span>
                            )}
                          </div>

                          {isAssigned && (
                            <div className="caam-card-badge">
                              <CheckCircle size={14} /> Đã phân công
                            </div>
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              {/* PREVIEW SIDEBAR */}
              {selectedAreaId && (
                <div className="caam-preview-sidebar">
                  {(() => {
                    const selectedAssignment = assignmentMap.get(
                      String(selectedAreaId),
                    );
                    const isAssigned = Boolean(selectedAssignment);

                    return (
                      <AreaDetailPreview
                        area={selectedItemObject}
                        isAssigned={isAssigned}
                        assignedType={selectedAssignment?.configType}
                        selectedConfigType={selectedConfigType}
                        onChangeConfigType={setSelectedConfigType}
                        onSaveAssignment={handleAssign}
                        onUnassign={(item) => {
                          const areaId = getItemId(item) || selectedAreaId;
                          if (!areaId) {
                            alert("Không tìm thấy ID khu vực để xóa!");
                            return;
                          }
                          handleDeleteAssignment(null, areaId);
                        }}
                        loading={isGlobalLoading}
                      />
                    );
                  })()}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* FOOTER */}
        <div className="caam-footer">
          <div className="caam-footer-info">
            {selectedAreaId ? (
              <span className="info-selected">
                <CheckCircle size={16} /> Đã chọn mục ID: {selectedAreaId} (
                {selectedConfigType})
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
              onClick={handleClose}
              disabled={isGlobalLoading}
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
