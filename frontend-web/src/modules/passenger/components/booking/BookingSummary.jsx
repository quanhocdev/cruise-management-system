import { Button, Spinner } from "react-bootstrap";
import { formatCurrency } from "./formatters";
import "./BookingSummary.css";

export default function BookingSummary({
  tour,
  tourId,
  pkg,
  rooms,
  passengerCount,
  totalPrice,
  submitting,
  exceeded,
  disabled,
}) {
  return (
    <aside className="booking-summary">
      <p className="booking-summary__tour">{tour?.name || "N/A"}</p>
      <p className="booking-summary__code">Mã tour {tour?.code || tourId}</p>

      <dl className="booking-summary__list">
        <div>
          <dt>Gói đã chọn</dt>
          <dd className="is-accent">{pkg?.name || "Chưa chọn"}</dd>
        </div>
        <div>
          <dt>Đơn giá</dt>
          <dd>{pkg ? `${formatCurrency(pkg.price)} / phòng` : "—"}</dd>
        </div>
        <div>
          <dt>Số phòng</dt>
          <dd>{rooms} phòng</dd>
        </div>
        <div>
          <dt>Hành khách</dt>
          <dd>{passengerCount} người</dd>
        </div>
      </dl>

      <div className="booking-summary__total">
        <span>Tổng tiền dự kiến</span>
        <strong>{formatCurrency(totalPrice)}</strong>
      </div>

      {exceeded && (
        <p className="booking-summary__warning">
          Số khách đang vượt sức chứa, hãy thêm phòng để tiếp tục.
        </p>
      )}

      <Button
        type="submit"
        className="booking-summary__submit"
        disabled={disabled}
      >
        {submitting ? (
          <Spinner animation="border" size="sm" />
        ) : (
          "Xác nhận đặt vé"
        )}
      </Button>
    </aside>
  );
}
