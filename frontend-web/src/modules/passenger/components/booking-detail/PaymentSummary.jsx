import { Button, Spinner } from "react-bootstrap";
import { formatMoney } from "../my-bookings/helpers";
import "./PaymentSummary.css";

const REFUND_STATUS_LABELS = {
  PENDING: "Đang chờ xét duyệt",
  APPROVED: "Đã được chấp thuận",
  REJECTED: "Yêu cầu đã bị từ chối",
  PROCESSING: "Đang xử lý hoàn tiền",
  COMPLETED: "Đã hoàn tiền",
  FAILED: "Hoàn tiền thất bại",
};

export default function PaymentSummary({
  booking,
  cancelling,
  onCancel,
  latestRefund,
  canRequestRefund,
  onRefund,
}) {
  const refundStatus = latestRefund?.status;

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
        <>
          <p className="bd-summary__note is-ok">
            Đơn hàng đã được xác nhận thành công.
          </p>

          {canRequestRefund && (
            <Button
              variant="outline-danger"
              className="w-100 mt-3"
              onClick={onRefund}
            >
              Yêu cầu hoàn tiền
            </Button>
          )}
        </>
      )}

      {booking.status === "PENDING_REFUND" && (
        <p className="bd-summary__note">
          Yêu cầu hoàn tiền của bạn đang được xử lý.
        </p>
      )}

      {booking.status === "REFUND_PROCESSING" && (
        <p className="bd-summary__note">
          Yêu cầu hoàn tiền đã được duyệt và đang tiến hành hoàn tiền.
        </p>
      )}

      {booking.status === "REFUNDED" && (
        <p className="bd-summary__note is-ok">Booking đã được hoàn tiền.</p>
      )}

      {refundStatus && (
        <div className="mt-3">
          <div className="bd-summary__note">
            <strong>Trạng thái yêu cầu hoàn tiền:</strong>
            <p className="mb-0 mt-1">
              {REFUND_STATUS_LABELS[refundStatus] || refundStatus}
            </p>
          </div>

          {latestRefund.calculatedRefundAmount != null && (
            <div className="bd-summary__total mt-2">
              <span>Số tiền dự kiến hoàn</span>
              <strong>
                {formatMoney(latestRefund.calculatedRefundAmount)}
              </strong>
            </div>
          )}

          {refundStatus === "REJECTED" && latestRefund.rejectionReason && (
            <p className="text-danger small mt-2">
              Lý do từ chối: {latestRefund.rejectionReason}
            </p>
          )}
        </div>
      )}

      {booking.status === "CANCELLED" && (
        <p className="bd-summary__note is-off">Đơn hàng này đã bị hủy.</p>
      )}
    </aside>
  );
}
