import { formatDateTime } from "../my-bookings/helpers";
import "./OrderOverview.css";

function formatDate(date) {
  if (!date) return "—";

  const parsedDate = new Date(`${date}T00:00:00`);

  if (Number.isNaN(parsedDate.getTime())) return "—";

  return new Intl.DateTimeFormat("vi-VN", {
    timeZone: "Asia/Ho_Chi_Minh",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(parsedDate);
}

export default function OrderOverview({ booking }) {
  return (
    <section className="bd-overview">
      <h2 className="bd-overview__title">Thông tin tour</h2>

      <dl className="bd-overview__grid">
        <div>
          <dt>Mã tour</dt>
          <dd>{booking.tourCode || "—"}</dd>
        </div>

        <div>
          <dt>Tên tour</dt>
          <dd>{booking.tourName || "—"}</dd>
        </div>

        <div>
          <dt>Ngày khởi hành</dt>
          <dd>{formatDate(booking.departureDate)}</dd>
        </div>

        <div>
          <dt>Ngày kết thúc</dt>
          <dd>{formatDate(booking.endDate)}</dd>
        </div>
      </dl>

      <h2 className="bd-overview__title mt-4">Thông tin liên hệ</h2>

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
