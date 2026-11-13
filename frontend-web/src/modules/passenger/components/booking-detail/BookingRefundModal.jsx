import { useState, useEffect } from "react";
import { Modal, Button, Form, Spinner, Alert } from "react-bootstrap";
import { formatMoney } from "../my-bookings/helpers";
import "./BookingRefundModal.css";

function formatDate(value) {
  if (!value) return "N/A";

  return new Date(`${value}T00:00:00`).toLocaleDateString("vi-VN");
}

export default function BookingRefundModal({
  show,
  preview,
  loading,
  submitting,
  error,
  onHide,
  onRetry,
  onConfirm,
}) {
  const [reason, setReason] = useState("");

  useEffect(() => {
    if (show) {
      setReason("");
    }
  }, [show]);

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!reason.trim()) return;

    onConfirm(reason.trim());
  };

  return (
    <Modal
      show={show}
      onHide={submitting ? undefined : onHide}
      centered
      size="lg"
      backdrop={submitting ? "static" : true}
    >
      <Modal.Header closeButton={!submitting}>
        <Modal.Title>Yêu cầu hoàn tiền</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        {error && (
          <Alert variant="danger">
            {error}
            {!loading && (
              <div className="mt-2">
                <Button variant="outline-danger" size="sm" onClick={onRetry}>
                  Thử tính toán lại
                </Button>
              </div>
            )}
          </Alert>
        )}

        {loading ? (
          <div className="refund-loading">
            <Spinner animation="border" variant="primary" />
            <p>Đang tính toán số tiền hoàn trả...</p>
          </div>
        ) : preview ? (
          <Form onSubmit={handleSubmit}>
            <div className="refund-preview">
              <div className="refund-preview__item">
                <span>Ngày khởi hành</span>
                <strong>{formatDate(preview.departureDate)}</strong>
              </div>

              <div className="refund-preview__item">
                <span>Số ngày trước khởi hành</span>
                <strong>{preview.daysBeforeDeparture} ngày</strong>
              </div>

              <div className="refund-preview__item">
                <span>Tổng tiền booking</span>
                <strong>{formatMoney(preview.originalAmount)}</strong>
              </div>

              <div className="refund-preview__item">
                <span>Tỷ lệ hoàn tiền</span>
                <strong>{preview.refundPercentage}%</strong>
              </div>

              <div className="refund-preview__total">
                <span>Số tiền dự kiến được hoàn</span>
                <strong>{formatMoney(preview.calculatedRefundAmount)}</strong>
              </div>
            </div>

            <Form.Group className="mt-4">
              <Form.Label>
                Lý do yêu cầu hoàn tiền <span className="text-danger">*</span>
              </Form.Label>

              <Form.Control
                as="textarea"
                rows={4}
                maxLength={1000}
                placeholder="Nhập lý do bạn muốn yêu cầu hoàn tiền..."
                value={reason}
                onChange={(event) => setReason(event.target.value)}
                required
                disabled={submitting}
              />

              <Form.Text muted>Tối đa 1000 ký tự.</Form.Text>
            </Form.Group>

            <Alert variant="warning" className="mt-3 mb-0">
              Số tiền trên là số tiền dự kiến theo chính sách hoàn tiền hiện
              tại. Yêu cầu sẽ được gửi đến bộ phận tài chính để xem xét.
            </Alert>
          </Form>
        ) : (
          !error && (
            <p className="text-center text-muted mb-0">
              Không có dữ liệu tính toán hoàn tiền.
            </p>
          )
        )}
      </Modal.Body>

      <Modal.Footer>
        <Button variant="secondary" onClick={onHide} disabled={submitting}>
          Đóng
        </Button>

        <Button
          variant="danger"
          onClick={handleSubmit}
          disabled={!preview || !reason.trim() || submitting || loading}
        >
          {submitting ? (
            <>
              <Spinner animation="border" size="sm" className="me-2" />
              Đang gửi...
            </>
          ) : (
            "Xác nhận yêu cầu hoàn tiền"
          )}
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
