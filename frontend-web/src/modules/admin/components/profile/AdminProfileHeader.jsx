import { Button } from "react-bootstrap";

export default function AdminProfileHeader({ userId, onBack }) {
  return (
    <div className="admin-profile-header">
      <div>
        <h2>Quản lý hồ sơ tài khoản</h2>
        <p>UUID tài khoản: {userId}</p>
      </div>

      <Button variant="outline-secondary" onClick={onBack}>
        <i className="bi bi-arrow-left me-2" />
        Quay lại
      </Button>
    </div>
  );
}
