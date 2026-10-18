import { Modal, Button } from "react-bootstrap";
import "./LoginRequiredModal.css";

export default function LoginRequiredModal({ show, onHide, onLogin }) {
  return (
    <Modal show={show} onHide={onHide} centered className="login-modal">
      <Modal.Header closeButton>
        <Modal.Title>Yêu cầu đăng nhập</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        Bạn cần đăng nhập bằng tài khoản hành khách (Passenger) để đặt vé cho
        hành trình này.
      </Modal.Body>
      <Modal.Footer>
        <Button variant="light" onClick={onHide}>
          Hủy
        </Button>
        <Button className="login-modal__confirm" onClick={onLogin}>
          Đăng nhập ngay
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
