import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function AccountFormModal({
  show,
  saving,
  editingAccount,
  form,
  error,
  roles = [],
  rolesLoading = false,
  onClose,
  onSubmit,
  onChange,
}) {
  const currentRole = editingAccount
    ? editingAccount.role?.name ||
      editingAccount.roleName ||
      editingAccount.role ||
      "—"
    : "";

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
              placeholder="Nhập email"
              required
            />
          </Form.Group>

          {editingAccount ? (
            <Form.Group className="mb-3">
              <Form.Label>Vai trò</Form.Label>

              <Form.Control type="text" value={currentRole} disabled readOnly />

              <Form.Text className="text-muted">
                Vai trò tài khoản không thể thay đổi.
              </Form.Text>
            </Form.Group>
          ) : (
            <Form.Group className="mb-3">
              <Form.Label>
                Vai trò <span className="text-danger">*</span>
              </Form.Label>

              <Form.Select
                name="roleId"
                value={form.roleId}
                onChange={onChange}
                required
                disabled={rolesLoading || saving}
              >
                <option value="">
                  {rolesLoading
                    ? "Đang tải vai trò..."
                    : roles.length === 0
                      ? "Không có vai trò khả dụng"
                      : "-- Chọn vai trò --"}
                </option>

                {roles.map((role) => (
                  <option key={role.id} value={String(role.id)}>
                    {role.name}
                  </option>
                ))}
              </Form.Select>

              {!rolesLoading && roles.length === 0 && (
                <Form.Text className="text-danger">
                  Không có vai trò khả dụng. Vui lòng kiểm tra API lấy danh sách
                  vai trò.
                </Form.Text>
              )}
            </Form.Group>
          )}

          {!editingAccount && (
            <Alert variant="light">
              <i className="bi bi-info-circle me-2" />
              Nhân viên sẽ nhận email kích hoạt và tự thiết lập mật khẩu. Admin
              không cần tạo mật khẩu.
            </Alert>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button
            variant="primary"
            type="submit"
            disabled={
              saving ||
              (!editingAccount && (rolesLoading || roles.length === 0))
            }
          >
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
