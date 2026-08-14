import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function AccountFormModal({
  show,
  saving,
  editingAccount,
  form,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  return (
    <Modal show={show} onHide={onClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>
          {editingAccount ? "Cập nhật tài khoản" : "Tạo tài khoản nhân viên"}
        </Modal.Title>
      </Modal.Header>

      <Form onSubmit={onSubmit}>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form.Group className="mb-3">
            <Form.Label>
              Tài khoản <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="text"
              name="username"
              value={form.username}
              onChange={onChange}
              placeholder="Nhập tên tài khoản"
              minLength={3}
              maxLength={50}
              required
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>
              Email <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="email"
              name="email"
              value={form.email}
              onChange={onChange}
              placeholder="Nhập email nhân viên"
              required
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>
              Vai trò <span className="text-danger">*</span>
            </Form.Label>

            <Form.Select
              name="roleId"
              value={form.roleId}
              onChange={onChange}
              required
            >
              <option value="">-- Chọn vai trò --</option>

              <option value="2">Scheduler</option>

              <option value="3">Shore</option>

              <option value="4">Onboard</option>

              <option value="5">Convenience</option>

              <option value="6">Finance</option>

              <option value="7">Operation</option>
            </Form.Select>
          </Form.Group>

          {editingAccount && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                name="status"
                value={form.status}
                onChange={onChange}
              >
                <option value="ACTIVE">ACTIVE</option>

                <option value="INACTIVE">INACTIVE</option>
              </Form.Select>
            </Form.Group>
          )}

          {!editingAccount && (
            <Alert variant="light">
              Nhân viên sẽ nhận email kích hoạt và tự thiết lập mật khẩu. Admin
              không cần tạo mật khẩu.
            </Alert>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button variant="primary" type="submit" disabled={saving}>
            {saving ? (
              <>
                <Spinner size="sm" animation="border" className="me-2" />
                Đang lưu...
              </>
            ) : editingAccount ? (
              "Cập nhật"
            ) : (
              "Tạo tài khoản"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
