import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function CruiseDeckFormModal({
  show,
  saving,
  editingDeck,
  form,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  const isEditing = Boolean(editingDeck);

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={onSubmit}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {isEditing ? "Chỉnh sửa tầng" : "Tạo các tầng du thuyền"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* =================================================
              CREATE
          ================================================= */}
          {!isEditing && (
            <>
              <Form.Group className="mb-3">
                <Form.Label>Số tầng muốn tạo</Form.Label>

                <Form.Control
                  type="number"
                  name="totalDecks"
                  value={form.totalDecks}
                  onChange={onChange}
                  min="1"
                  max="100"
                  placeholder="Ví dụ: 5"
                  disabled={saving}
                  autoFocus
                />

                <Form.Text className="text-muted">
                  Nhập số tầng, hệ thống sẽ tự động tạo từ tầng 1 đến tầng{" "}
                  {form.totalDecks || "..."}.
                </Form.Text>
              </Form.Group>

              {form.totalDecks > 0 && (
                <Alert variant="info">
                  Hệ thống sẽ tạo <strong>{form.totalDecks} tầng</strong>:
                  <div className="mt-2">
                    {Array.from(
                      {
                        length: Number(form.totalDecks),
                      },
                      (_, index) => (
                        <span
                          key={index}
                          className="badge bg-primary me-1 mb-1"
                        >
                          Tầng {index + 1}
                        </span>
                      ),
                    )}
                  </div>
                </Alert>
              )}
            </>
          )}

          {/* =================================================
              UPDATE
          ================================================= */}
          {isEditing && (
            <>
              <Form.Group className="mb-3">
                <Form.Label>Số tầng</Form.Label>

                <Form.Control
                  type="number"
                  name="deckNumber"
                  value={form.deckNumber}
                  onChange={onChange}
                  min="1"
                  placeholder="Ví dụ: 1"
                  disabled={saving}
                />

                <Form.Text className="text-muted">
                  Số tầng phải là số nguyên dương và không được trùng trong cùng
                  một du thuyền.
                </Form.Text>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Trạng thái</Form.Label>

                <Form.Select
                  name="status"
                  value={form.status}
                  onChange={onChange}
                  disabled={saving}
                >
                  <option value="ACTIVE">ACTIVE</option>

                  <option value="INACTIVE">INACTIVE</option>
                </Form.Select>
              </Form.Group>
            </>
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
            ) : isEditing ? (
              "Lưu thay đổi"
            ) : (
              "Tạo tầng"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
