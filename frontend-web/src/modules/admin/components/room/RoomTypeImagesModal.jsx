import { useEffect, useState } from "react";
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Form,
  Image,
  Modal,
  Row,
  Spinner,
} from "react-bootstrap";
import { Trash2, Plus } from "lucide-react";
import useRoomTypeImages from "../../hooks/useRoomTypeImages";

export default function RoomTypeImagesModal({ show, roomType, onClose }) {
  const {
    images,
    loading,
    submitting,
    deletingId,
    error,
    success,
    setError,
    setSuccess,
    addImage,
    deleteImage,
  } = useRoomTypeImages(roomType?.id, show);

  const [imageFile, setImageFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [label, setLabel] = useState("");
  const [sortOrder, setSortOrder] = useState(0);

  useEffect(() => {
    if (show) {
      setImageFile(null);
      setLabel("");
      setSortOrder(0);
    }
  }, [show, roomType?.id]);

  useEffect(() => {
    if (!imageFile) {
      setPreviewUrl(null);
      return;
    }
    const url = URL.createObjectURL(imageFile);
    setPreviewUrl(url);
    return () => URL.revokeObjectURL(url);
  }, [imageFile]);

  const handleFileChange = (event) => {
    const file = event.target.files?.[0] || null;
    setImageFile(file);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const created = await addImage({
      image: imageFile,
      label,
      sortOrder: sortOrder === "" ? 0 : Number(sortOrder),
    });

    if (created) {
      setImageFile(null);
      setLabel("");
      setSortOrder(0);
    }
  };

  const handleHide = () => {
    if (submitting || deletingId) return;
    onClose();
  };

  return (
    <Modal
      show={show}
      onHide={handleHide}
      size="lg"
      centered
      backdrop={submitting ? "static" : true}
      keyboard={!submitting}
    >
      <Modal.Header closeButton={!submitting}>
        <Modal.Title>
          Ảnh chi tiết — {roomType?.name || "Loại phòng"}
        </Modal.Title>
      </Modal.Header>

      <Modal.Body>
        {error && (
          <Alert variant="danger" dismissible onClose={() => setError("")}>
            {error}
          </Alert>
        )}
        {success && (
          <Alert variant="success" dismissible onClose={() => setSuccess("")}>
            {success}
          </Alert>
        )}

        {/* FORM THÊM ẢNH CHI TIẾT */}
        <Form
          onSubmit={handleSubmit}
          className="border rounded p-3 mb-4 bg-light"
        >
          <h6 className="mb-3">Thêm ảnh khu vực mới</h6>

          <Row className="g-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>File ảnh (tùy chọn)</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  disabled={submitting}
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Nhãn khu vực (tùy chọn)</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Phòng khách, Phòng ngủ, Ban công..."
                  maxLength={100}
                  value={label}
                  onChange={(e) => setLabel(e.target.value)}
                  disabled={submitting}
                />
              </Form.Group>
            </Col>

            <Col md={2}>
              <Form.Group>
                <Form.Label>Thứ tự</Form.Label>
                <Form.Control
                  type="number"
                  min={0}
                  value={sortOrder}
                  onChange={(e) => setSortOrder(e.target.value)}
                  disabled={submitting}
                />
              </Form.Group>
            </Col>
          </Row>

          {previewUrl && (
            <div className="mt-3">
              <Image
                src={previewUrl}
                alt="Xem trước ảnh chi tiết"
                thumbnail
                style={{ maxHeight: "120px", objectFit: "cover" }}
              />
            </div>
          )}

          <div className="mt-3 d-flex justify-content-end">
            <Button type="submit" variant="primary" disabled={submitting}>
              {submitting ? (
                <>
                  <Spinner size="sm" animation="border" className="me-2" />
                  Đang thêm...
                </>
              ) : (
                <>
                  <Plus size={16} className="me-1" />
                  Thêm ảnh
                </>
              )}
            </Button>
          </div>
        </Form>

        {/* DANH SÁCH ẢNH CHI TIẾT HIỆN CÓ */}
        <h6 className="mb-3">Danh sách ảnh chi tiết ({images.length})</h6>

        {loading ? (
          <div className="text-center py-4">
            <Spinner animation="border" />
            <p className="mt-2 text-muted">Đang tải ảnh chi tiết...</p>
          </div>
        ) : images.length === 0 ? (
          <div className="text-center py-4 text-muted border rounded">
            Chưa có ảnh chi tiết nào cho loại phòng này.
          </div>
        ) : (
          <Row xs={1} sm={2} md={3} className="g-3">
            {images.map((img) => (
              <Col key={img.id}>
                <Card className="h-100">
                  {img.imageUrl ? (
                    <Card.Img
                      variant="top"
                      src={img.imageUrl}
                      alt={img.label || roomType?.name || "Ảnh chi tiết"}
                      style={{ height: "130px", objectFit: "cover" }}
                    />
                  ) : (
                    <div
                      className="d-flex align-items-center justify-content-center bg-light text-muted small"
                      style={{ height: "130px" }}
                    >
                      Không có file ảnh
                    </div>
                  )}

                  <Card.Body className="p-2 d-flex flex-column justify-content-between">
                    <div className="d-flex justify-content-between align-items-start gap-1">
                      <span className="fw-medium small">
                        {img.label || "Chưa gắn nhãn"}
                      </span>
                      <Badge bg="secondary">#{img.sortOrder ?? 0}</Badge>
                    </div>

                    <div className="mt-2 text-end">
                      <Button
                        size="sm"
                        variant="outline-danger"
                        disabled={deletingId === img.id}
                        onClick={() => deleteImage(img.id)}
                      >
                        {deletingId === img.id ? (
                          <Spinner size="sm" animation="border" />
                        ) : (
                          <Trash2 size={14} />
                        )}
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Modal.Body>

      <Modal.Footer>
        <Button variant="secondary" onClick={handleHide} disabled={submitting}>
          Đóng
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
