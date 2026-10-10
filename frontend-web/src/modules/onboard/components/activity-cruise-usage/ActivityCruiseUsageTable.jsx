// src/modules/onboard/components/activity-cruise-usage/ActivityCruiseUsageTable.jsx
import { Activity, Loader2 } from "lucide-react";

const formatDateTime = (value) => {
  if (!value) {
    return "—";
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "—";
  }

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatMoney = (value) => {
  if (value === null || value === undefined) {
    return "—";
  }

  return Number(value).toLocaleString("vi-VN") + " ₫";
};

const getStatusLabel = (status) => {
  switch (status) {
    case "WAITING_CONFIG":
      return "Chờ cấu hình";

    case "CONFIGURED":
      return "Đã cấu hình";

    case "NOT_STARTED":
      return "Chưa diễn ra";

    case "IN_PROGRESS":
      return "Đang diễn ra";

    case "COMPLETED":
      return "Đã kết thúc";

    default:
      return status || "—";
  }
};

const ActivityCruiseUsageTable = ({ usages, loading }) => {
  return (
    <div className="activity-cruise-usage-table-wrapper">
      <table className="activity-cruise-usage-table">
        <thead>
          <tr>
            <th>Hành khách</th>
            <th>Mã Booking</th>
            <th>Hoạt động</th>
            <th>Thời gian hoạt động</th>
            <th>Trạng thái</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
            <th>Giảm giá</th>
            <th>Thành tiền</th>
            <th>Thời gian sử dụng</th>
          </tr>
        </thead>

        <tbody>
          {loading ? (
            <tr>
              <td colSpan="10" className="activity-cruise-usage-table__loading">
                <Loader2
                  size={22}
                  className="activity-cruise-usage-table__loading-icon"
                />
                <span>Đang tải dữ liệu...</span>
              </td>
            </tr>
          ) : usages.length === 0 ? (
            <tr>
              <td colSpan="10" className="activity-cruise-usage-table__empty">
                <Activity size={24} />
                <span>Chưa có dữ liệu sử dụng hoạt động</span>
              </td>
            </tr>
          ) : (
            usages.map((usage) => (
              <tr key={usage.id}>
                {/* PASSENGER */}
                <td>
                  <div className="activity-cruise-usage-table__passenger">
                    <strong>{usage.passengerName || "—"}</strong>
                    <span>ID: {usage.bookingPassengerId ?? "—"}</span>
                  </div>
                </td>

                {/* BOOKING */}
                <td>
                  <span className="activity-cruise-usage-table__booking-code">
                    {usage.bookingCode || "—"}
                  </span>
                </td>

                {/* ACTIVITY */}
                <td>
                  <div className="activity-cruise-usage-table__activity">
                    <strong>{usage.activityName || "—"}</strong>

                    {usage.maxPassengers !== null &&
                      usage.maxPassengers !== undefined && (
                        <span>Tối đa {usage.maxPassengers} khách</span>
                      )}
                  </div>
                </td>

                {/* ACTIVITY TIME */}
                <td>
                  <div className="activity-cruise-usage-table__time">
                    <span>{formatDateTime(usage.startTime)}</span>
                    <span>→ {formatDateTime(usage.endTime)}</span>
                  </div>
                </td>

                {/* STATUS */}
                <td>
                  <span
                    className={`activity-cruise-usage-table__status activity-cruise-usage-table__status--${(
                      usage.activityCruiseTourStatus || "unknown"
                    ).toLowerCase()}`}
                  >
                    {getStatusLabel(usage.activityCruiseTourStatus)}
                  </span>
                </td>

                {/* QUANTITY */}
                <td className="activity-cruise-usage-table__number">
                  {usage.quantity ?? "—"}
                </td>

                {/* UNIT PRICE */}
                <td className="activity-cruise-usage-table__money">
                  {formatMoney(usage.unitPrice)}
                </td>

                {/* DISCOUNT */}
                <td className="activity-cruise-usage-table__money">
                  {formatMoney(usage.discountAmount)}
                </td>

                {/* FINAL AMOUNT */}
                <td className="activity-cruise-usage-table__money activity-cruise-usage-table__money--final">
                  {formatMoney(usage.finalAmount)}
                </td>

                {/* USED AT */}
                <td>{formatDateTime(usage.usedAt)}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ActivityCruiseUsageTable;
