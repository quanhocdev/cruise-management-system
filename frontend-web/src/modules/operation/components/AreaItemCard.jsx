// src/modules/operation/components/AreaItemCard.jsx
import React from "react";
import { Grid, DoorClosed, Trash2, CheckCircle } from "lucide-react";

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

function AreaItemCard({
  item,
  assignment,
  isSelected,
  onSelect,
  onDelete,
  disabled,
}) {
  const itemId = getItemId(item);
  const isAssigned = Boolean(assignment);
  const isArea = item._type === "AREA";

  return (
    <div
      className={`caam-card ${isArea ? "is-area" : "is-room"} ${isAssigned ? "assigned" : ""} ${
        isSelected ? "selected" : ""
      }`}
      onClick={() => onSelect(item)}
    >
      <div className="caam-card-header">
        <span
          className={`caam-card-type ${isArea ? "type-area" : "type-room"}`}
        >
          {isArea ? <Grid size={16} /> : <DoorClosed size={16} />}
          {isArea ? "Khu vực" : "Phòng nghỉ"}
        </span>

        {isAssigned && (
          <button
            type="button"
            className="caam-delete-btn"
            onClick={(e) => onDelete(e, itemId, assignment.type)}
            title="Hủy phân công"
            disabled={disabled}
          >
            <Trash2 size={16} />
          </button>
        )}
      </div>

      <div className="caam-card-body">
        <h4>{getItemName(item)}</h4>
        <p className="deck-info">{item._deckName}</p>
        {!isArea && <span className="room-status-badge available">Trống</span>}
      </div>

      {isAssigned && (
        <div className="caam-card-badge">
          <CheckCircle size={14} /> Đã phân công
        </div>
      )}
    </div>
  );
}

export default React.memo(AreaItemCard);
