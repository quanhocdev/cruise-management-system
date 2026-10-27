// src/modules/operation/hooks/useCruiseAreaAssignment.js

import { useCallback, useEffect, useMemo, useState } from "react";
import patrolRequirementService from "../services/patrolRequirementService";

// ---------- HELPER ----------
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

const getAssignmentAreaId = (item) =>
  item.cruiseAreaId || item.areaId || item.cruiseArea?.id || item.area?.id;

const getErrorMessage = (error) =>
  error?.response?.data?.message ||
  error?.response?.data?.error ||
  error?.message ||
  "Có lỗi xảy ra. Vui lòng thử lại.";

const normalizePatrolResponse = (response) => {
  if (Array.isArray(response)) return response;
  if (Array.isArray(response?.data)) return response.data;
  if (Array.isArray(response?.data?.data)) return response.data.data;
  return [];
};

const useCruiseAreaAssignment = ({
  open,
  tour,
  cruiseLayout,
  activityAssignments = [],
  productAssignments = [],
  serviceAssignments = [],
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
}) => {
  const [selectedDeckId, setSelectedDeckId] = useState("ALL");
  const [selectedAreaId, setSelectedAreaId] = useState(null);
  const [selectedConfigType, setSelectedConfigType] =
    useState(defaultConfigType);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewType, setViewType] = useState("ALL");
  const [isDeleting, setIsDeleting] = useState(false);

  // ---------- PATROL REQUIREMENTS ----------
  const [patrolRequirements, setPatrolRequirements] = useState([]);
  const [patrolLoading, setPatrolLoading] = useState(false);
  const [patrolSaving, setPatrolSaving] = useState(false);
  const [patrolError, setPatrolError] = useState("");
  const [patrolSuccess, setPatrolSuccess] = useState("");
  const [patrolDirty, setPatrolDirty] = useState(false);

  // ---------- RESET + LOAD WHEN OPEN ----------
  useEffect(() => {
    if (!open || !(tour?.id || tour?.tourId)) return;

    const tourId = tour.id || tour.tourId;

    setSelectedAreaId(null);
    setSelectedConfigType(defaultConfigType);
    setSearchTerm("");
    setSelectedDeckId("ALL");
    setViewType("ALL");

    setPatrolRequirements([]);
    setPatrolError("");
    setPatrolSuccess("");
    setPatrolDirty(false);

    onLoadLayout?.(tourId);
    onLoadAssignments?.(tourId);

    let cancelled = false;

    const loadPatrolRequirements = async () => {
      try {
        setPatrolLoading(true);

        const response = await patrolRequirementService.getByTour(tourId);

        if (cancelled) return;

        setPatrolRequirements(normalizePatrolResponse(response));
      } catch (error) {
        if (cancelled) return;

        console.error("LOAD PATROL REQUIREMENTS ERROR:", error);
        setPatrolError(getErrorMessage(error));
      } finally {
        if (!cancelled) setPatrolLoading(false);
      }
    };

    loadPatrolRequirements();

    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, tour?.id, tour?.tourId]);

  // ---------- NORMALIZE CRUISE LAYOUT ----------
  const decks = useMemo(() => {
    if (Array.isArray(cruiseLayout)) return cruiseLayout;
    if (Array.isArray(cruiseLayout?.decks)) return cruiseLayout.decks;
    if (Array.isArray(cruiseLayout?.data)) return cruiseLayout.data;
    return [];
  }, [cruiseLayout]);

  // ---------- NORMALIZE PATROL REQUIREMENTS ----------
  // Mọi tầng đều xuất hiện. Tầng chưa có dữ liệu mặc định = 0.
  const patrolDecks = useMemo(() => {
    return decks.map((deck) => {
      const deckId = getDeckId(deck);

      const existing = patrolRequirements.find(
        (item) => String(item.cruiseDeckId) === String(deckId),
      );

      return {
        cruiseDeckId: deckId,
        deckNumber:
          deck.deckNumber ??
          deck.deckOrder ??
          deck.number ??
          deck.level ??
          deck.floor ??
          null,
        deckName: getDeckName(deck),
        requiredCount: Number(existing?.requiredCount ?? 0),
      };
    });
  }, [decks, patrolRequirements]);

  const selectedPatrolDeck = useMemo(
    () =>
      patrolDecks.find(
        (item) => String(item.cruiseDeckId) === String(selectedDeckId),
      ) || null,
    [patrolDecks, selectedDeckId],
  );

  const totalPatrolRequired = useMemo(
    () =>
      patrolDecks.reduce(
        (total, deck) => total + Number(deck.requiredCount || 0),
        0,
      ),
    [patrolDecks],
  );

  // ---------- UPDATE PATROL REQUIREMENT ----------
  const updatePatrolRequirement = useCallback((cruiseDeckId, value) => {
    if (value !== "" && !/^\d+$/.test(String(value))) return;

    const requiredCount = value === "" ? 0 : Number(value);

    if (!Number.isSafeInteger(requiredCount) || requiredCount < 0) {
      return;
    }

    setPatrolRequirements((previous) => {
      const index = previous.findIndex(
        (item) => String(item.cruiseDeckId) === String(cruiseDeckId),
      );

      if (index >= 0) {
        return previous.map((item, i) =>
          i === index ? { ...item, requiredCount } : item,
        );
      }

      return [
        ...previous,
        {
          cruiseDeckId,
          requiredCount,
        },
      ];
    });

    setPatrolDirty(true);
    setPatrolSuccess("");
    setPatrolError("");
  }, []);

  // ---------- SAVE ALL PATROL REQUIREMENTS ----------
  const savePatrolRequirements = useCallback(async () => {
    const tourId = tour?.id || tour?.tourId;

    if (!tourId) {
      setPatrolError("Không xác định được Tour.");
      return false;
    }

    if (patrolLoading || patrolSaving) return false;

    const requirements = patrolDecks.map((deck) => ({
      cruiseDeckId: deck.cruiseDeckId,
      requiredCount: Number(deck.requiredCount || 0),
    }));

    const invalid = requirements.some(
      (item) =>
        !item.cruiseDeckId ||
        !Number.isSafeInteger(item.requiredCount) ||
        item.requiredCount < 0,
    );

    if (invalid) {
      setPatrolError("Số lượng nhân viên tuần tra không hợp lệ.");
      return false;
    }

    try {
      setPatrolSaving(true);
      setPatrolError("");
      setPatrolSuccess("");

      const response = await patrolRequirementService.save(tourId, {
        requirements,
      });

      const savedRequirements = normalizePatrolResponse(response);

if (savedRequirements.length > 0) {
  setPatrolRequirements(savedRequirements);
}
      setPatrolDirty(false);
      setPatrolSuccess("Đã lưu yêu cầu tuần tra cho tất cả các tầng.");

      return true;
    } catch (error) {
      console.error("SAVE PATROL REQUIREMENTS ERROR:", error);
      setPatrolError(getErrorMessage(error));
      return false;
    } finally {
      setPatrolSaving(false);
    }
  }, [tour, patrolDecks, patrolLoading, patrolSaving]);

  // ---------- ASSIGNMENT MAP ----------
  // Ưu tiên ACTIVITY > PRODUCT > SERVICE
  const assignmentMap = useMemo(() => {
    const map = new Map();

    [
      ["SERVICE", serviceAssignments],
      ["PRODUCT", productAssignments],
      ["ACTIVITY", activityAssignments],
    ].forEach(([type, list]) => {
      list.forEach((item) => {
        const id = getAssignmentAreaId(item);
        if (id) map.set(String(id), { type, data: item });
      });
    });

    return map;
  }, [activityAssignments, productAssignments, serviceAssignments]);

  const getAssignment = useCallback(
    (itemId) => assignmentMap.get(String(itemId)) || null,
    [assignmentMap],
  );

  const cruiseName =
    tour?.cruise?.name ||
    tour?.cruiseName ||
    cruiseLayout?.cruise?.name ||
    cruiseLayout?.cruiseName ||
    "Chưa xác định";

  // ---------- ALL AREAS + ROOMS ----------
  const allItems = useMemo(() => {
    const list = [];

    decks.forEach((deck) => {
      const deckInfo = {
        _deckId: getDeckId(deck),
        _deckName: getDeckName(deck),
      };

      (deck?.areas || deck?.cruiseAreas || deck?.areaList || []).forEach(
        (area) => list.push({ ...area, _type: "AREA", ...deckInfo }),
      );

      (
        deck?.rooms ||
        deck?.cabins ||
        deck?.roomList ||
        deck?.cabinList ||
        []
      ).forEach((room) => list.push({ ...room, _type: "ROOM", ...deckInfo }));
    });

    return list;
  }, [decks]);

  const deckStatsMap = useMemo(() => {
    const stats = new Map();

    allItems.forEach((item) => {
      const key = String(item._deckId);
      const current = stats.get(key) || { total: 0, assigned: 0 };

      current.total += 1;

      if (getAssignment(getItemId(item))) {
        current.assigned += 1;
      }

      stats.set(key, current);
    });

    return stats;
  }, [allItems, getAssignment]);

  const totalAssignedCount = useMemo(
    () => allItems.filter((item) => getAssignment(getItemId(item))).length,
    [allItems, getAssignment],
  );

  // ---------- FILTER ----------
  const filteredItems = useMemo(() => {
    const query = searchTerm.trim().toLowerCase();

    return allItems.filter((item) => {
      if (
        selectedDeckId !== "ALL" &&
        String(item._deckId) !== String(selectedDeckId)
      ) {
        return false;
      }

      if (viewType !== "ALL" && item._type !== viewType) return false;

      if (query) {
        return (
          getItemName(item).toLowerCase().includes(query) ||
          getItemCode(item).toLowerCase().includes(query) ||
          (item._deckName || "").toLowerCase().includes(query)
        );
      }

      return true;
    });
  }, [allItems, selectedDeckId, viewType, searchTerm]);

  const rows = filteredItems.map((item) => {
    const id = getItemId(item);

    return {
      key: `${item._type}-${id}`,
      item,
      assignment: getAssignment(id),
      isSelected: String(selectedAreaId) === String(id),
    };
  });

  // ---------- SELECTED ITEM ----------
  const selectedItem = useMemo(() => {
    if (!selectedAreaId) return null;

    return (
      allItems.find(
        (item) => String(getItemId(item)) === String(selectedAreaId),
      ) || null
    );
  }, [selectedAreaId, allItems]);

  const selectedAssignment = selectedAreaId
    ? getAssignment(selectedAreaId)
    : null;

  // ---------- HANDLERS ----------
  const handleSelectItem = (item) => {
    const itemId = getItemId(item);
    if (!itemId) return;

    if (String(selectedAreaId) === String(itemId)) {
      setSelectedAreaId(null);
      return;
    }

    setSelectedAreaId(itemId);

    const existing = getAssignment(itemId);

    setSelectedConfigType(
      existing
        ? existing.type
        : item._type === "ROOM"
          ? "PRODUCT"
          : defaultConfigType,
    );
  };

  const handleAssign = async () => {
    const tourId = tour?.id ?? tour?.tourId;
    if (!tourId || !selectedAreaId) return;

    const payload = {
      tourId: String(tourId),
      cruiseAreaId: String(selectedAreaId),
    };

    try {
      if (selectedConfigType === "ACTIVITY") {
        await onAssignArea?.(payload);
      }

      if (selectedConfigType === "PRODUCT") {
        await onAssignProduct?.(payload);
      }

      if (selectedConfigType === "SERVICE") {
        await onAssignService?.(payload);
      }

      setSelectedAreaId(null);
      await onLoadAssignments?.(tourId);
    } catch (err) {
      console.error("LỖI PHÂN CÔNG:", err);
    }
  };

  const handleDeleteAssignment = async (e, cruiseAreaId, configType) => {
    e?.stopPropagation?.();

    const tourId = tour?.id || tour?.tourId;

    if (!tourId || !cruiseAreaId || cruiseAreaId === "undefined") return;

    if (!window.confirm("Bạn có chắc chắn muốn xóa phân công này?")) return;

    const deleteHandlers = {
      ACTIVITY: onDeleteActivityAssignment,
      PRODUCT: onDeleteProductAssignment,
      SERVICE: onDeleteServiceAssignment,
    };

    try {
      setIsDeleting(true);
      await deleteHandlers[configType]?.(tourId, cruiseAreaId);
      await onLoadAssignments?.(tourId);
    } catch (err) {
      console.error("Lỗi khi xóa phân công:", err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleUnassign = (item) => {
    const areaId = getItemId(item) || selectedAreaId;

    handleDeleteAssignment(
      null,
      areaId,
      getAssignment(areaId)?.type || selectedConfigType,
    );
  };

  const isGlobalLoading = assignmentLoading || isDeleting || patrolSaving;

  const handleClose = () => {
    if (isGlobalLoading || patrolLoading) return;

    if (
      patrolDirty &&
      !window.confirm(
        "Bạn chưa lưu thay đổi yêu cầu tuần tra. Bạn có chắc chắn muốn đóng và bỏ các thay đổi này?",
      )
    ) {
      return;
    }

    onClose?.();
  };

  return {
    decks,
    cruiseName,
    isGlobalLoading,

    // FILTER
    selectedDeckId,
    setSelectedDeckId,
    searchTerm,
    setSearchTerm,
    viewType,
    setViewType,

    // STATISTICS / GRID
    totalItemsCount: allItems.length,
    totalAssignedCount,
    deckStatsMap,
    rows,

    // SELECTED ITEM
    selectedAreaId,
    selectedItem,
    selectedItemName: selectedItem ? getItemName(selectedItem) : "",
    selectedAssignment,
    selectedConfigType,
    setSelectedConfigType,

    // PATROL REQUIREMENTS
    patrolDecks,
    selectedPatrolDeck,
    totalPatrolRequired,
    patrolLoading,
    patrolSaving,
    patrolError,
    patrolSuccess,
    patrolDirty,
    updatePatrolRequirement,
    savePatrolRequirements,

    // HANDLERS
    handleSelectItem,
    handleAssign,
    handleDeleteAssignment,
    handleUnassign,
    handleClose,
  };
};

export default useCruiseAreaAssignment;
