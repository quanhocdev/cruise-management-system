import { Button, Spinner } from "react-bootstrap";
import { formatMoney } from "../my-bookings/helpers";
import "./PaymentSummary.css";

export default function PaymentSummary({ booking, cancelling, onCancel }) {
  return (
    <aside className="bd-summary">
      <h2 className="bd-summary__title">Tóm tắt thanh toán</h2>

      <dl className="bd-summary__list">
        <div>
          <dt>Mã tour</dt>
          <dd>{booking.tourCode || booking.tourId || "N/A"}</dd>
        </div>
        <div>
          <dt>Hành khách</dt>
          <dd>{booking.passengers?.length || 0} người</dd>
        </div>
      </dl>

      <div className="bd-summary__total">
        <span>Tổng tiền</span>
        <strong>{formatMoney(booking.totalAmount)}</strong>
      </div>

      {booking.status === "PENDING_PAYMENT" && (
        <Button
          className="bd-summary__cancel"
          onClick={onCancel}
          disabled={cancelling}
        >
          {cancelling ? (
            <Spinner animation="border" size="sm" />
          ) : (
            "Hủy đơn hàng"
          )}
        </Button>
      )}

      {booking.status === "CONFIRMED" && (
        <p className="bd-summary__note is-ok">
          Đơn hàng đã được xác nhận thành công.
        </p>
      )}

      {booking.status === "CANCELLED" && (
        <p className="bd-summary__note is-off">Đơn hàng này đã bị hủy.</p>
      )}
    </aside>
  );
}
