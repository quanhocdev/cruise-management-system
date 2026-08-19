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

/**
 * 🎯 HÀM NHẬN BIẾT LOẠI HÌNH PHÂN CÔNG TỪ DATABASE / API RESPONSE
 */
const detectConfigType = (assignment, fallbackType = "ACTIVITY") => {
  if (!assignment) return fallbackType;

  // 1. Nếu Backend trả về trực tiếp configType hoặc type
  const type =
    assignment.configType || assignment.type || assignment.assignmentType;
  if (type) {
    const upper = String(type).toUpperCase();
    if (upper.includes("PRODUCT") || upper.includes("ROOM")) return "PRODUCT";
    if (upper.includes("SERVICE")) return "SERVICE";
    if (upper.includes("ACTIVITY")) return "ACTIVITY";
  }

  // 2. Nhận biết dựa trên các Object / ID con trả về từ DB
  if (assignment.activityId || assignment.activity || assignment.tourActivity) {
    return "ACTIVITY";
  }
  if (
    assignment.productId ||
    assignment.product ||
    assignment.room ||
    assignment.cabin
  ) {
    return "PRODUCT";
  }
  if (assignment.serviceId || assignment.service) {
    return "SERVICE";
  }

  return fallbackType;
};

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

  // NORMALIZE DECKS
  const decks = useMemo(() => {
    if (Array.isArray(cruiseLayout)) return cruiseLayout;
    if (Array.isArray(cruiseLayout?.decks)) return cruiseLayout.decks;
    if (Array.isArray(cruiseLayout?.data)) return cruiseLayout.data;
    return [];
  }, [cruiseLayout]);

  // RESET STATE & LOAD DATA
  useEffect(() => {
    if (!open || !tour?.id) return;

    setSelectedAreaId(null);
    setSelectedConfigType(defaultConfigType);
    setSearchTerm("");
    setSelectedDeckId("ALL");
    setViewType("ALL");

    onLoadLayout?.(tour.id);
    onLoadAssignments?.(tour.id);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, tour?.id]);

  // MAP PHÂN CÔNG TỪ DATABASE
  const activityAssignmentMap = useMemo(() => {
    const map = new Map();

    activityAssignments.forEach((item) => {
      const id =
        item.cruiseAreaId ||
        item.areaId ||
        item.cruiseArea?.id ||
        item.area?.id;

      if (id) {
        map.set(String(id), item);
      }
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

      if (id) {
        map.set(String(id), item);
      }
    });

    return map;
  }, [productAssignments]);
  const getAssignment = (itemId) => {
    const key = String(itemId);

    const activity = activityAssignmentMap.get(key);
    if (activity) {
      return {
        type: "ACTIVITY",
        data: activity,
      };
    }

    const product = productAssignmentMap.get(key);
    if (product) {
      return {
        type: "PRODUCT",
        data: product,
      };
    }

    const service = serviceAssignmentMap.get(key);
    if (service) {
      return {
        type: "SERVICE",
        data: service,
      };
    }

    return null;
  };

  const serviceAssignmentMap = useMemo(() => {
    const map = new Map();

    serviceAssignments.forEach((item) => {
      const id =
        item.cruiseAreaId ||
        item.areaId ||
        item.cruiseArea?.id ||
        item.area?.id;

      if (id) {
        map.set(String(id), item);
      }
    });

    return map;
  }, [serviceAssignments]);
  const getCruiseName = useCallback(() => {
    if (tour?.cruise?.name) return tour.cruise.name;
    if (tour?.cruiseName) return tour.cruiseName;
    if (cruiseLayout?.cruise?.name) return cruiseLayout.cruise.name;
    if (cruiseLayout?.cruiseName) return cruiseLayout.cruiseName;

    const allAssignments = [
      ...(Array.isArray(activityAssignments) ? activityAssignments : []),
      ...(Array.isArray(productAssignments) ? productAssignments : []),
      ...(Array.isArray(serviceAssignments) ? serviceAssignments : []),
    ];
    if (allAssignments.length > 0) {
      const first = allAssignments[0];
      const cruise = first?.cruiseArea?.cruise || first?.cruise;

      if (cruise?.name) return cruise.name;
    }

    return "Chưa xác định";
  }, [
    tour,
    cruiseLayout,
    activityAssignments,
    productAssignments,
    serviceAssignments,
  ]);

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
      if (getAssignment(getItemId(item))) {
        current.assigned += 1;
      }
      stats.set(key, current);
    });
    return stats;
  }, [
    allItemsWithDeckInfo,
    activityAssignmentMap,
    productAssignmentMap,
    serviceAssignmentMap,
  ]);

  const selectedItemObject = useMemo(() => {
    if (!selectedAreaId) return null;
    return allItemsWithDeckInfo.find(
      (item) => String(getItemId(item)) === String(selectedAreaId),
    );
  }, [selectedAreaId, allItemsWithDeckInfo]);

  const totalAssignedCount = useMemo(() => {
    return allItemsWithDeckInfo.filter((item) => getAssignment(getItemId(item)))
      .length;
  }, [
    allItemsWithDeckInfo,
    activityAssignmentMap,
    productAssignmentMap,
    serviceAssignmentMap,
  ]);

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

      // 🎯 LẤY LOẠI HÌNH THỰC TẾ TỪ DATABASE
      const existingAssignment = getAssignment(itemId);

      if (existingAssignment) {
        setSelectedConfigType(existingAssignment.type);
      } else {
        const itemType = item._type === "ROOM" ? "PRODUCT" : defaultConfigType;

        setSelectedConfigType(itemType);
      }
    }
  };

  const handleAssign = async () => {
    const tourId = tour?.id ?? tour?.tourId;
    const cruiseAreaId = selectedAreaId;

    console.log("========== HANDLE ASSIGN ==========");
    console.log("tourId:", tourId);
    console.log("cruiseAreaId:", cruiseAreaId);
    console.log("selectedConfigType:", selectedConfigType);
    console.log("onAssignArea:", onAssignArea);
    console.log("onAssignProduct:", onAssignProduct);
    console.log("onAssignService:", onAssignService);

    if (!tourId || !cruiseAreaId) {
      console.error("❌ Thiếu tourId hoặc cruiseAreaId");
      return;
    }

    const payload = {
      tourId: String(tourId),
      cruiseAreaId: String(cruiseAreaId),
    };

    console.log("📦 Payload:", payload);

    try {
      if (selectedConfigType === "ACTIVITY") {
        console.log("🔥 ASSIGN ACTIVITY");

        if (!onAssignArea) {
          console.error("❌ onAssignArea chưa được truyền vào!");
          return;
        }

        await onAssignArea(payload);
      }

      if (selectedConfigType === "PRODUCT") {
        console.log("🔥 ASSIGN PRODUCT");

        if (!onAssignProduct) {
          console.error("❌ onAssignProduct chưa được truyền vào!");
          return;
        }

        await onAssignProduct(payload);
      }

      if (selectedConfigType === "SERVICE") {
        console.log("🔥 ASSIGN SERVICE");

        if (!onAssignService) {
          console.error("❌ onAssignService chưa được truyền vào!");
          return;
        }

        await onAssignService(payload);
      }

      console.log("✅ ASSIGN SUCCESS");

      setSelectedAreaId(null);

      await onLoadAssignments?.(tourId);
    } catch (err) {
      console.error("❌ LỖI PHÂN CÔNG:", err);
      console.error("Response:", err?.response);
      console.error("Response data:", err?.response?.data);
    }
  };
  const handleDeleteAssignment = async (e, cruiseAreaId, configType) => {
    if (e && typeof e.stopPropagation === "function") {
      e.stopPropagation();
    }

    const currentTourId = tour?.id || tour?.tourId;

    if (!currentTourId || !cruiseAreaId || cruiseAreaId === "undefined") {
      console.error("❌ Thiếu tourId hoặc cruiseAreaId hợp lệ!");
      alert("Không thể xóa do thông tin khu vực hoặc tour không hợp lệ.");
      return;
    }

    if (!window.confirm("Bạn có chắc chắn muốn xóa phân công này?")) {
      return;
    }

    try {
      setIsDeleting(true);

      if (configType === "ACTIVITY") {
        await onDeleteActivityAssignment?.(currentTourId, cruiseAreaId);
      } else if (configType === "PRODUCT") {
        await onDeleteProductAssignment?.(currentTourId, cruiseAreaId);
      } else if (configType === "SERVICE") {
        await onDeleteServiceAssignment?.(currentTourId, cruiseAreaId);
      }

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
                      const assignment = getAssignment(itemId);
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
                                onClick={(e) => {
                                  handleDeleteAssignment(
                                    e,
                                    itemId,
                                    assignment.type,
                                  );
                                }}
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
                    const selectedAssignment = getAssignment(selectedAreaId);

                    const isAssigned = Boolean(selectedAssignment);

                    const assignedTypeFromDb = isAssigned
                      ? selectedAssignment.type
                      : null;

                    return (
                      <AreaDetailPreview
                        area={selectedItemObject}
                        isAssigned={isAssigned}
                        assignedType={assignedTypeFromDb}
                        selectedConfigType={selectedConfigType}
                        onChangeConfigType={setSelectedConfigType}
                        onSaveAssignment={handleAssign}
                        onUnassign={(item) => {
                          const areaId = getItemId(item) || selectedAreaId;

                          if (!areaId) {
                            alert("Không tìm thấy ID khu vực để xóa!");
                            return;
                          }

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
