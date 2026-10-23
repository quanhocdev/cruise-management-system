import { formatDateTime } from "../my-bookings/helpers";
import "./OrderOverview.css";

export default function OrderOverview({ booking }) {
  return (
    <section className="bd-overview">
      <h2 className="bd-overview__title">Thông tin liên hệ</h2>

      <dl className="bd-overview__grid">
        <div>
          <dt>Người liên hệ</dt>
          <dd>{booking.primaryContactName || "—"}</dd>
        </div>
        <div>
          <dt>Số điện thoại</dt>
          <dd>{booking.primaryContactPhone || "—"}</dd>
        </div>
        <div>
          <dt>Ngày tạo đơn</dt>
          <dd>{formatDateTime(booking.createdAt)}</dd>
        </div>
      </dl>
    </section>
  );
}
