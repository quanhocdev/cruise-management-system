import PassengerForm from "./PassengerForm";
import "./PassengerList.css";

export default function PassengerList({
  passengers,
  savedPassengers,
  exceeded,
  rooms,
  totalCapacity,
  onChange,
  onRemove,
  onPickSaved,
}) {
  return (
    <div className="passenger-list">
      {exceeded && (
        <p className="passenger-list__warning" role="alert">
          Số khách ({passengers.length}) vượt quá sức chứa của {rooms} phòng
          (tối đa {totalCapacity} khách). Hãy tăng số phòng hoặc bớt hành khách.
        </p>
      )}

      {passengers.map((p, index) => (
        <PassengerForm
          key={p._key}
          index={index}
          data={p}
          canRemove={passengers.length > 1}
          savedPassengers={savedPassengers}
          onChange={onChange}
          onRemove={onRemove}
          onPickSaved={onPickSaved}
        />
      ))}
    </div>
  );
}
