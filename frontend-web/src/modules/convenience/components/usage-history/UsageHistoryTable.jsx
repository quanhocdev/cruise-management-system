// src/modules/convenience/components/usage-history/UsageHistoryTable.jsx
import { Package, Loader2, Wrench } from "lucide-react";

const formatDateTime = (value) => {
  if (!value) return "—";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatMoney = (value) => {
  if (value === null || value === undefined || value === "") {
    return "—";
  }

  const amount = Number(value);

  if (!Number.isFinite(amount)) return "—";

  return `${amount.toLocaleString("vi-VN")} ₫`;
};

const getStatusLabel = (status) => {
  if (!status) return "—";

  const labels = {
    WAITING_CONFIG: "Chờ cấu hình",
    CONFIGURED: "Đã cấu hình",
    NOT_STARTED: "Chưa diễn ra",
    IN_PROGRESS: "Đang diễn ra",
    COMPLETED: "Đã kết thúc",
    DRAFT: "Bản nháp",
    APPROVAL_PENDING: "Chờ duyệt",
    APPROVED: "Đã duyệt",
    READY: "Sẵn sàng",
    CANCELLED: "Đã hủy",
    OPEN: "Đang mở",
    CLOSED: "Đã đóng",
    WAITING: "Đang chờ",
    NOT_OPEN: "Chưa mở",
  };

  return labels[status] || status;
};

const getUsageStatus = (usage, type) => {
  return type === "product" ? usage.productTourStatus : usage.serviceTourStatus;
};

const getUsageName = (usage, type) => {
  return type === "product" ? usage.productName : usage.serviceName;
};

const getUsageActive = (usage, type) => {
  return type === "product" ? usage.productActive : usage.serviceActive;
};

const UsageHistoryTable = ({
  usages = [],
  loading = false,
  type = "product",
}) => {
  const isProduct = type === "product";
  const ItemIcon = isProduct ? Package : Wrench;
  const itemLabel = isProduct ? "sản phẩm" : "dịch vụ";

  const columnCount = isProduct ? 9 : 10;

  return (
    <div className="usage-history-table-wrapper">
      <table className="usage-history-table">
        <thead>
          <tr>
            <th>Hành khách</th>
            <th>Mã Booking</th>
            <th>{isProduct ? "Sản phẩm" : "Dịch vụ"}</th>

            {isProduct && <th>Số lượng</th>}

            <th>Trạng thái</th>
            <th>Đơn giá</th>
            <th>Giảm giá</th>
            <th>Thành tiền</th>
            <th>Thời gian sử dụng</th>

            {!isProduct && <th>Thời gian kết thúc</th>}
          </tr>
        </thead>

        <tbody>
          {loading ? (
            <tr>
              <td
                colSpan={columnCount}
                className="usage-history-table__loading"
              >
                <Loader2
                  size={22}
                  className="usage-history-table__loading-icon"
                />
                <span>Đang tải dữ liệu...</span>
              </td>
            </tr>
          ) : usages.length === 0 ? (
            <tr>
              <td colSpan={columnCount} className="usage-history-table__empty">
                <ItemIcon size={24} />
                <span>Chưa có dữ liệu sử dụng {itemLabel}</span>
              </td>
            </tr>
          ) : (
            usages.map((usage) => {
              const status = getUsageStatus(usage, type);
              const name = getUsageName(usage, type);
              const active = getUsageActive(usage, type);

              return (
                <tr key={usage.id}>
                  <td>
                    <div className="usage-history-table__passenger">
                      <strong>{usage.passengerName || "—"}</strong>

                      <span>ID: {usage.bookingPassengerId ?? "—"}</span>
                    </div>
                  </td>

                  <td>
                    <span className="usage-history-table__booking-code">
                      {usage.bookingCode || "—"}
                    </span>
                  </td>

                  <td>
                    <div className="usage-history-table__item">
                      <strong>{name || "—"}</strong>

                      {active !== null && active !== undefined && (
                        <span
                          className={
                            active
                              ? "usage-history-table__active"
                              : "usage-history-table__inactive"
                          }
                        >
                          {active ? "Đang hoạt động" : "Ngừng hoạt động"}
                        </span>
                      )}
                    </div>
                  </td>

                  {isProduct && (
                    <td className="usage-history-table__number">
                      {usage.quantity ?? "—"}
                    </td>
                  )}

                  <td>
                    <span
                      className={`usage-history-table__status usage-history-table__status--${(
                        status || "unknown"
                      )
                        .toLowerCase()
                        .replace(/_/g, "-")}`}
                    >
                      {getStatusLabel(status)}
                    </span>
                  </td>

                  <td className="usage-history-table__money">
                    {formatMoney(usage.unitPrice)}
                  </td>

                  <td className="usage-history-table__money">
                    {formatMoney(usage.discountAmount)}
                  </td>

                  <td className="usage-history-table__money usage-history-table__money--final">
                    {formatMoney(usage.finalAmount)}
                  </td>

                  <td>{formatDateTime(usage.usedAt)}</td>

                  {!isProduct && (
                    <td>
                      <div className="usage-history-table__time">
                        <span>Hết hạn: {formatDateTime(usage.expiresAt)}</span>

                        <span>Kết thúc: {formatDateTime(usage.endedAt)}</span>
                      </div>
                    </td>
                  )}
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>
  );
};

export default UsageHistoryTable;
