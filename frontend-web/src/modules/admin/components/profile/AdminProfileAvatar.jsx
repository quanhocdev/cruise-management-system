import { Button, Card, Form, Spinner } from "react-bootstrap";

export default function AdminProfileAvatar({
  avatarUrl,
  avatarFile,
  hasProfile,
  loading,
  onFileChange,
  onUpload,
  onDelete,
}) {
  return (
    <Card className="admin-profile-card mb-4">
      <Card.Header>
        <h5>Ảnh đại diện</h5>
      </Card.Header>

      <Card.Body>
        <div className="admin-profile-avatar-layout">
          {avatarUrl ? (
            <img
              src={avatarUrl}
              alt="Ảnh đại diện"
              className="admin-profile-avatar-image"
            />
          ) : (
            <div className="admin-profile-avatar-placeholder">
              <i className="bi bi-person" />
            </div>
          )}

          <div className="admin-profile-avatar-controls">
            <Form.Control
              type="file"
              accept="image/*"
              disabled={!hasProfile || loading}
              onChange={(event) =>
                onFileChange(event.target.files?.[0] || null)
              }
            />

            <div className="d-flex flex-wrap gap-2 mt-3">
              <Button
                variant="primary"
                disabled={!hasProfile || !avatarFile || loading}
                onClick={onUpload}
              >
                {loading ? (
                  <Spinner size="sm" className="me-2" />
                ) : (
                  <i className="bi bi-upload me-2" />
                )}
                Tải ảnh lên
              </Button>

              {avatarUrl && (
                <Button
                  variant="outline-danger"
                  disabled={loading}
                  onClick={onDelete}
                >
                  <i className="bi bi-trash me-2" />
                  Xóa ảnh
                </Button>
              )}
            </div>

            {!hasProfile && (
              <Form.Text className="text-muted">
                Cần tạo hồ sơ trước khi tải ảnh đại diện.
              </Form.Text>
            )}
          </div>
        </div>
      </Card.Body>
    </Card>
  );
}
