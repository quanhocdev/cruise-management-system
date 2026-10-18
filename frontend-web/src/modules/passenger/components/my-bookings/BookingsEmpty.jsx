import { Link } from "react-router-dom";
import { Ship } from "lucide-react";
import "./BookingsEmpty.css";

export default function BookingsEmpty({ hasAnyBooking, onReset }) {
  return (
    <div className="my-bookings-empty">
      <Ship size={36} className="my-bookings-empty__icon" aria-hidden="true" />

      {hasAnyBooking ? (
        <>
          <h2>Không có đơn vé nào phù hợp</h2>
          <p>Thử bỏ bớt bộ lọc để xem các đơn khác.</p>
          <button type="button" onClick={onReset}>
            Xóa bộ lọc
          </button>
        </>
      ) : (
        <>
          <h2>Bạn chưa có đơn vé nào</h2>
          <p>Chọn một hành trình du thuyền và đặt vé để bắt đầu.</p>
          <Link to="/passenger/dashboard">Khám phá tour</Link>
        </>
      )}
    </div>
  );
}
