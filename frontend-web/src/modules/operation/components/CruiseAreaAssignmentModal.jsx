import React, { useEffect, useMemo, useState } from "react";
import {
  X,
  Ship,
  Layers,
  MapPin,
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
  // State chọn Tầng ('ALL' hoặc deckId) và Item được chọn để gán
  const [selectedDeckId, setSelectedDeckId] = useState("ALL");
  const [selectedAreaId, setSelectedAreaId] = useState(null);

  // State lưu loại hình được chọn (PRODUCT | SERVICE | ACTIVITY)
  const [selectedConfigType, setSelectedConfigType] = useState("ACTIVITY");

  // State bộ lọc truyền xuống AreaFilterToolbar
  const [searchTerm, setSearchTerm] = useState("");
  const [viewType, setViewType] = useState("ALL"); // 'ALL' | 'AREA' | 'ROOM'

  // NORMALIZE CÁC TẦNG (DECKS)
  const decks = useMemo(() => {
    if (Array.isArray(cruiseLayout)) return cruiseLayout;
    if (Array.isArray(cruiseLayout?.decks)) return cruiseLayout.decks;
    if (Array.isArray(cruiseLayout?.data)) return cruiseLayout.data;
    return [];
  }, [cruiseLayout]);

  // RESET STATE & LOAD DATA KHI MỞ/ĐÓNG MODAL HOẶC ĐỔI TOUR
  useEffect(() => {
    if (open && tour?.id) {
      setSelectedAreaId(null);
      setSelectedConfigType("ACTIVITY");
      setSearchTerm("");
      setSelectedDeckId("ALL");
      setViewType("ALL");

      onLoadLayout?.(tour.id);
      onLoadAssignments?.(tour.id);
    } else if (!open) {
      // Clear nhẹ state khi đóng modal
      setSelectedAreaId(null);
    }
  }, [open, tour?.id, onLoadLayout, onLoadAssignments]);

  // MAP PHÂN CÔNG THEO ITEM ID
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

  // HELPER FUNCTIONS Truy xuất dữ liệu
  const getCruiseName = () => {
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
  };

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

  // TỔNG HỢP CẢ AREAS VÀ ROOMS DƯỚI DẠNG DANH SÁCH MỤC ĐỘC LẬP
  const allItemsWithDeckInfo = useMemo(() => {
    const list = [];

    decks.forEach((deck) => {
      const dId = getDeckId(deck);
      const dName = getDeckName(deck);

      // 1. Bóc tách danh sách Khu vực (Areas)
      const rawAreas = deck?.areas || deck?.cruiseAreas || deck?.areaList || [];
      rawAreas.forEach((area) => {
        list.push({
          ...area,
          _type: "AREA",
          _deckId: dId,
          _deckName: dName,
        });
      });

      // 2. Bóc tách danh sách Phòng (Rooms/Cabins) nằm độc lập ở Deck
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

  // LẤY DỮ LIỆU CỦA ITEM ĐANG ĐƯỢC CHỌN
  const selectedItemObject = useMemo(() => {
    if (!selectedAreaId) return null;
    return allItemsWithDeckInfo.find(
      (item) => String(getItemId(item)) === String(selectedAreaId),
    );
  }, [selectedAreaId, allItemsWithDeckInfo]);

  // TỔNG SỐ ĐÃ GÁN
  const totalAssignedCount = useMemo(() => {
    return allItemsWithDeckInfo.filter((item) =>
      assignmentMap.has(String(getItemId(item))),
    ).length;
  }, [allItemsWithDeckInfo, assignmentMap]);

  // LỌC DANH SÁCH THEO TẦNG, LOẠI (AREA/ROOM) VÀ TỪ KHÓA TÌM KIẾM
  const filteredItems = useMemo(() => {
    let result = allItemsWithDeckInfo;

    // 1. Lọc theo Tầng
    if (selectedDeckId !== "ALL") {
      result = result.filter(
        (item) => String(item._deckId) === String(selectedDeckId),
      );
    }

    // 2. Lọc theo Loại (Phòng vs Khu vực)
    if (viewType === "ROOM") {
      result = result.filter((item) => item._type === "ROOM");
    } else if (viewType === "AREA") {
      result = result.filter((item) => item._type === "AREA");
    }

    // 3. Lọc theo Từ khóa tìm kiếm
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
    if (assignmentMap.has(String(itemId))) return;

    if (selectedAreaId === itemId) {
      setSelectedAreaId(null);
    } else {
      setSelectedAreaId(itemId);
      if (!selectedConfigType) setSelectedConfigType("ACTIVITY");
    }
  };

  const handleAssign = async () => {
    if (!tour?.id || !selectedAreaId) return;

    try {
      // Nếu cần truyền cả selectedConfigType lên API thì bổ sung tham số ở đây:
      // await onAssignArea(tour.id, selectedAreaId, selectedConfigType);
      await onAssignArea(tour.id, selectedAreaId);
      setSelectedAreaId(null);
    } catch (err) {
      console.error("Lỗi phân công:", err);
    }
  };

  const handleDeleteAssignment = async (e, itemId) => {
    e.stopPropagation();
    const assignment = assignmentMap.get(String(itemId));
    if (!assignment?.id) return;

    const itemName =
      assignment?.cruiseArea?.name ||
      assignment?.area?.name ||
      assignment?.room?.name ||
      "Mục này";

    if (
      !window.confirm(
        `Bạn có chắc chắn muốn hủy phân công "${itemName}" không?`,
      )
    )
      return;

    try {
      await onDeleteAssignment?.(assignment.id);
      if (String(selectedAreaId) === String(itemId)) {
        setSelectedAreaId(null);
      }
      await onLoadAssignments?.(tour.id);
    } catch (err) {
      console.error("DELETE ASSIGNMENT ERROR:", err);
    }
  };

  const handleClose = () => {
    if (assignmentLoading) return;
    onClose?.();
  };

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
                Chọn tầng và phân công khu vực hoặc phòng nghỉ cho Tour hiện tại
              </p>
            </div>
          </div>
          <button
            type="button"
            className="caam-close-btn"
            onClick={handleClose}
            disabled={assignmentLoading}
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
                  const deckItems = allItemsWithDeckInfo.filter(
                    (item) => String(item._deckId) === String(dId),
                  );
                  const assignedCount = deckItems.filter((item) =>
                    assignmentMap.has(String(getItemId(item))),
                  ).length;

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
                        <span className="badge-total">
                          {deckItems.length} mục
                        </span>
                        {assignedCount > 0 && (
                          <span
                            className="badge-assigned"
                            title="Số mục đã gán"
                          >
                            {assignedCount} đã gán
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

            {/* KHU VỰC HIỂN THỊ CHÍNH */}
            <div
              className={`caam-split-workspace ${
                selectedAreaId ? "has-preview" : ""
              }`}
            >
              {/* CỘT TRÁI: GRID CÁC KHU VỰC / PHÒNG */}
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
                    {filteredItems.map((item, idx) => {
                      const itemId = getItemId(item);
                      const isAssigned = assignmentMap.has(String(itemId));
                      const isSelected = selectedAreaId === itemId;
                      const isRoom = item._type === "ROOM";

                      // TẠO UNIQUE KEY TRÁNH LỖI DUPLICATE KEY CỦA REACT
                      const uniqueKey = `item-${item._type}-${item._deckId}-${itemId || idx}`;

                      return (
                        <div
                          key={uniqueKey}
                          className={`caam-area-card ${
                            isAssigned ? "is-assigned" : ""
                          } ${isSelected ? "is-selected" : ""}`}
                          onClick={() => handleSelectItem(item)}
                        >
                          <div className="caam-card-header">
                            <div className="caam-area-identity">
                              {isRoom ? (
                                <DoorClosed
                                  size={16}
                                  className="icon-pin"
                                  style={{ color: "#059669" }}
                                />
                              ) : (
                                <MapPin size={16} className="icon-pin" />
                              )}
                              <strong className="caam-area-title">
                                {getItemName(item)}
                              </strong>
                            </div>
                            {getItemCode(item) && (
                              <span className="caam-area-code">
                                {getItemCode(item)}
                              </span>
                            )}
                          </div>

                          {selectedDeckId === "ALL" && (
                            <div className="caam-card-deck-tag">
                              <Layers size={13} />
                              <span>{item._deckName}</span>
                            </div>
                          )}

                          <div className="caam-card-footer">
                            {isAssigned ? (
                              <div className="caam-assigned-row">
                                <span className="tag-assigned">
                                  <CheckCircle size={13} /> Đã phân công
                                </span>
                                <button
                                  type="button"
                                  className="caam-delete-btn"
                                  onClick={(e) =>
                                    handleDeleteAssignment(e, itemId)
                                  }
                                  title="Gỡ phân công"
                                >
                                  <Trash2 size={15} />
                                </button>
                              </div>
                            ) : (
                              <div className="caam-select-row">
                                <span
                                  className={`tag-select ${
                                    isSelected ? "active" : ""
                                  }`}
                                >
                                  {isSelected ? "Đã chọn" : "Bấm để chọn"}
                                </span>
                              </div>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              {/* CỘT PHẢI: PREVIEW */}
              {selectedAreaId && (
                <div className="caam-preview-sidebar">
                  <AreaDetailPreview
                    area={selectedItemObject}
                    selectedConfigType={selectedConfigType}
                    onChangeConfigType={setSelectedConfigType}
                    onSaveAssignment={handleAssign}
                    loading={assignmentLoading}
                  />
                </div>
              )}
            </div>
          </div>
        </div>

        {/* FOOTER MODAL */}
        <div className="caam-footer">
          <div className="caam-footer-info">
            {selectedAreaId ? (
              <span className="info-selected">
                <CheckCircle size={16} /> Đã chọn khu vực ID: {selectedAreaId} (
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
              disabled={assignmentLoading}
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
