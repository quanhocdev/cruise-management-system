import "./RoomStepper.css";

export default function RoomStepper({
  value,
  onChange,
  capacityPerRoom,
  totalCapacity,
}) {
  return (
    <div className="room-stepper">
      <div className="room-stepper__label">
        <span className="room-stepper__title">Số lượng phòng</span>
        <span className="room-stepper__note">
          Mỗi phòng tối đa {capacityPerRoom} khách. Tổng sức chứa{" "}
          <strong>{totalCapacity} khách</strong>.
        </span>
      </div>

      <div className="room-stepper__control">
        <button
          type="button"
          aria-label="Giảm số phòng"
          onClick={() => onChange(value - 1)}
          disabled={value <= 1}
        >
          −
        </button>
        <input
          type="number"
          min="1"
          max="50"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          aria-label="Số lượng phòng"
        />
        <button
          type="button"
          aria-label="Tăng số phòng"
          onClick={() => onChange(value + 1)}
          disabled={value >= 50}
        >
          +
        </button>
      </div>
    </div>
  );
}
