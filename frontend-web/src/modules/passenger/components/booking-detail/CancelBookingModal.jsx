import { Modal, Button, Spinner } from "react-bootstrap";
import "./CancelBookingModal.css";

export default function CancelBookingModal({
  show,
  loading,
  onHide,
  onConfirm,
}) {
  return (
    <Modal show={show} onHide={onHide} centered className="bd-cancel-modal">
      <Modal.Header closeButton>
        <Modal.Title>Hủy đơn hàng?</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        Bạn có chắc muốn hủy đơn hàng này không? Sau khi hủy, đơn sẽ không thể
        khôi phục.
      </Modal.Body>
      <Modal.Footer>
        <Button variant="light" onClick={onHide} disabled={loading}>
          Giữ đơn hàng
        </Button>
        <Button
          className="bd-cancel-modal__confirm"
          onClick={onConfirm}
          disabled={loading}
        >
          {loading ? <Spinner animation="border" size="sm" /> : "Hủy đơn hàng"}
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
