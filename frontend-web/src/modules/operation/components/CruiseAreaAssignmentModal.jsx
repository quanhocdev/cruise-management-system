// src/modules/operation/components/CruiseAreaAssignmentModal.jsx
import React, { useEffect, useMemo, useState, useCallback } from "react";
import { X, Ship, CheckCircle, AlertCircle, Loader2 } from "lucide-react";
import AreaFilterToolbar from "./AreaFilterToolbar";
import AreaDetailPreview from "./AreaDetailPreview";
import DeckSidebar from "./DeckSidebar";
import AreaItemCard from "./AreaItemCard";
import "../styles/CruiseAreaAssignmentModal.css";

// Helper functions nhỏ cho Modal
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
  activityAssignments = [],
  productAssignments = [],
  serviceAssignments = [],
  layoutLoading = false,
  assignmentLoading = false,
  defaultConfigType = "ACTIVITY",
  onLoadLayout,
  onLoadAssignments,
  onAssignArea,
  onAssignProduct,
  onAssignService,
  onDeleteActivityAssignment,
  onDeleteProductAssignment,
  onDeleteServiceAssignment,
  onClose,
}) {
  const [selectedDeckId, setSelectedDeckId] = useState("ALL");
  const [selectedAreaId, setSelectedAreaId] = useState(null);
  const [selectedConfigType, setSelectedConfigType] =
    useState(defaultConfigType);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewType, setViewType] = useState("ALL");
  const [isDeleting, setIsDeleting] = useState(false);

  // Chuẩn hóa danh sách Decks
  const decks = useMemo(() => {
    if (Array.isArray(cruiseLayout)) return cruiseLayout;
    if (Array.isArray(cruiseLayout?.decks)) return cruiseLayout.decks;
    if (Array.isArray(cruiseLayout?.data)) return cruiseLayout.data;
    return [];
  }, [cruiseLayout]);

  // Reset State & Gọi API
  useEffect(() => {
    if (!open || !tour?.id) return;
    setSelectedAreaId(null);
    setSelectedConfigType(defaultConfigType);
    setSearchTerm("");
    setSelectedDeckId("ALL");
    setViewType("ALL");

    onLoadLayout?.(tour.id);
    onLoadAssignments?.(tour.id);
  }, [open, tour?.id]);

  // Tạo Map Phân Công
  const activityAssignmentMap = useMemo(() => {
    const map = new Map();
    activityAssignments.forEach((item) => {
      const id =
        item.cruiseAreaId ||
        item.areaId ||
        item.cruiseArea?.id ||
        item.area?.id;
      if (id) map.set(String(id), item);
    });
    return map;
  }, [activityAssignments]);

  const productAssignmentMap = useMemo(() => {
    const map = new Map();
    productAssignments.forEach((item) => {
      const id =
        item.cruiseAreaId ||
        item.areaId ||
        item.cruiseArea?.id ||
        item.area?.id;
      if (id) map.set(String(id), item);
    });
    return map;
  }, [productAssignments]);

  const serviceAssignmentMap = useMemo(() => {
    const map = new Map();
    serviceAssignments.forEach((item) => {
      const id =
        item.cruiseAreaId ||
        item.areaId ||
        item.cruiseArea?.id ||
        item.area?.id;
      if (id) map.set(String(id), item);
    });
    return map;
  }, [serviceAssignments]);

  const getAssignment = useCallback(
    (itemId) => {
      const key = String(itemId);
      if (activityAssignmentMap.has(key))
        return { type: "ACTIVITY", data: activityAssignmentMap.get(key) };
      if (productAssignmentMap.has(key))
        return { type: "PRODUCT", data: productAssignmentMap.get(key) };
      if (serviceAssignmentMap.has(key))
        return { type: "SERVICE", data: serviceAssignmentMap.get(key) };
      return null;
    },
    [activityAssignmentMap, productAssignmentMap, serviceAssignmentMap],
  );

  const getCruiseName = useCallback(() => {
    if (tour?.cruise?.name) return tour.cruise.name;
    if (tour?.cruiseName) return tour.cruiseName;
    if (cruiseLayout?.cruise?.name) return cruiseLayout.cruise.name;
    if (cruiseLayout?.cruiseName) return cruiseLayout.cruiseName;
    return "Chưa xác định";
  }, [tour, cruiseLayout]);

  // Danh sách Items kèm thông tin Tầng
  const allItemsWithDeckInfo = useMemo(() => {
    const list = [];
    decks.forEach((deck) => {
      const dId = getDeckId(deck);
      const dName = getDeckName(deck);
      (deck?.areas || deck?.cruiseAreas || deck?.areaList || []).forEach(
        (area) => {
          list.push({ ...area, _type: "AREA", _deckId: dId, _deckName: dName });
        },
      );
      (
        deck?.rooms ||
        deck?.cabins ||
        deck?.roomList ||
        deck?.cabinList ||
        []
      ).forEach((room) => {
        list.push({ ...room, _type: "ROOM", _deckId: dId, _deckName: dName });
      });
    });
    return list;
  }, [decks]);

  // Thống kê từng Tầng
  const deckStatsMap = useMemo(() => {
    const stats = new Map();
    allItemsWithDeckInfo.forEach((item) => {
      const key = String(item._deckId);
      const current = stats.get(key) || { total: 0, assigned: 0 };
      current.total += 1;
      if (getAssignment(getItemId(item))) current.assigned += 1;
      stats.set(key, current);
    });
    return stats;
  }, [allItemsWithDeckInfo, getAssignment]);

  const totalAssignedCount = useMemo(() => {
    return allItemsWithDeckInfo.filter((item) => getAssignment(getItemId(item)))
      .length;
  }, [allItemsWithDeckInfo, getAssignment]);

  // Danh sách lọc
  const filteredItems = useMemo(() => {
    let result = allItemsWithDeckInfo;
    if (selectedDeckId !== "ALL") {
      result = result.filter(
        (item) => String(item._deckId) === String(selectedDeckId),
      );
    }
    if (viewType === "ROOM")
      result = result.filter((item) => item._type === "ROOM");
    else if (viewType === "AREA")
      result = result.filter((item) => item._type === "AREA");

    if (searchTerm.trim()) {
      const query = searchTerm.toLowerCase().trim();
      result = result.filter((item) => {
        return (
          getItemName(item).toLowerCase().includes(query) ||
          getItemCode(item).toLowerCase().includes(query) ||
          (item._deckName || "").toLowerCase().includes(query)
        );
      });
    }
    return result;
  }, [allItemsWithDeckInfo, selectedDeckId, viewType, searchTerm]);

  const selectedItemObject = useMemo(() => {
    if (!selectedAreaId) return null;
    return allItemsWithDeckInfo.find(
      (item) => String(getItemId(item)) === String(selectedAreaId),
    );
  }, [selectedAreaId, allItemsWithDeckInfo]);

  // Handlers
  const handleSelectItem = (item) => {
    const itemId = getItemId(item);
    if (!itemId) return;

    if (String(selectedAreaId) === String(itemId)) {
      setSelectedAreaId(null);
    } else {
      setSelectedAreaId(itemId);
      const existing = getAssignment(itemId);
      if (existing) {
        setSelectedConfigType(existing.type);
      } else {
        setSelectedConfigType(
          item._type === "ROOM" ? "PRODUCT" : defaultConfigType,
        );
      }
    }
  };

  const handleAssign = async () => {
    const tourId = tour?.id ?? tour?.tourId;
    if (!tourId || !selectedAreaId) return;

    const payload = {
      tourId: String(tourId),
      cruiseAreaId: String(selectedAreaId),
    };
    try {
      if (selectedConfigType === "ACTIVITY") await onAssignArea?.(payload);
      if (selectedConfigType === "PRODUCT") await onAssignProduct?.(payload);
      if (selectedConfigType === "SERVICE") await onAssignService?.(payload);

      setSelectedAreaId(null);
      await onLoadAssignments?.(tourId);
    } catch (err) {
      console.error("❌ LỖI PHÂN CÔNG:", err);
    }
  };

  const handleDeleteAssignment = async (e, cruiseAreaId, configType) => {
    if (e?.stopPropagation) e.stopPropagation();
    const currentTourId = tour?.id || tour?.tourId;
    if (!currentTourId || !cruiseAreaId || cruiseAreaId === "undefined") return;

    if (!window.confirm("Bạn có chắc chắn muốn xóa phân công này?")) return;

    try {
      setIsDeleting(true);
      if (configType === "ACTIVITY")
        await onDeleteActivityAssignment?.(currentTourId, cruiseAreaId);
      else if (configType === "PRODUCT")
        await onDeleteProductAssignment?.(currentTourId, cruiseAreaId);
      else if (configType === "SERVICE")
        await onDeleteServiceAssignment?.(currentTourId, cruiseAreaId);

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
          <DeckSidebar
            decks={decks}
            selectedDeckId={selectedDeckId}
            onSelectDeck={setSelectedDeckId}
            totalItemsCount={allItemsWithDeckInfo.length}
            totalAssignedCount={totalAssignedCount}
            deckStatsMap={deckStatsMap}
            loading={layoutLoading}
          />

          <div className="caam-main-content">
            <AreaFilterToolbar
              searchTerm={searchTerm}
              setSearchTerm={setSearchTerm}
              viewType={viewType}
              setViewType={setViewType}
            />

            <div
              className={`caam-split-workspace ${selectedAreaId ? "has-preview" : ""}`}
            >
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
                      const assignment = getAssignment(itemId);
                      return (
                        <AreaItemCard
                          key={`${item._type}-${itemId}`}
                          item={item}
                          assignment={assignment}
                          isSelected={String(selectedAreaId) === String(itemId)}
                          onSelect={handleSelectItem}
                          onDelete={handleDeleteAssignment}
                          disabled={isGlobalLoading}
                        />
                      );
                    })}
                  </div>
                )}
              </div>

              {selectedAreaId && (
                <div className="caam-preview-sidebar">
                  {(() => {
                    const selectedAssignment = getAssignment(selectedAreaId);
                    return (
                      <AreaDetailPreview
                        area={selectedItemObject}
                        isAssigned={Boolean(selectedAssignment)}
                        assignedType={selectedAssignment?.type || null}
                        selectedConfigType={selectedConfigType}
                        onChangeConfigType={setSelectedConfigType}
                        onSaveAssignment={handleAssign}
                        onUnassign={(item) => {
                          const areaId = getItemId(item) || selectedAreaId;
                          const assignment = getAssignment(areaId);
                          handleDeleteAssignment(
                            null,
                            areaId,
                            assignment?.type || selectedConfigType,
                          );
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
                <CheckCircle size={16} /> Đã chọn:{" "}
                {getItemName(selectedItemObject)} ({selectedConfigType})
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
