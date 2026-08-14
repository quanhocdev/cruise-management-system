import { Button, Card, Col, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

export default function CruiseDeckDetail() {
  const { deckId } = useParams();
  const navigate = useNavigate();

  return (
    <div className="container-fluid py-4">
      {/* =========================
          HEADER
         ========================= */}

      <div className="mb-4">
        <Button
          variant="link"
          className="px-0 mb-2"
          onClick={() => navigate(-1)}
        >
          ← Quay lại danh sách tầng
        </Button>

        <h2>Quản lý tầng</h2>

        <p className="text-muted">Quản lý khu vực và phòng của tầng này.</p>
      </div>

      {/* =========================
          ACTIONS
         ========================= */}

      <Row className="g-4">
        {/* =====================
            AREA
           ===================== */}

        <Col md={6}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <Card.Title>Khu vực</Card.Title>

              <Card.Text className="text-muted">
                Quản lý các khu vực trên tầng này như nhà hàng, hồ bơi, bar, khu
                vui chơi...
              </Card.Text>

              <Button
                variant="primary"
                onClick={() => navigate(`/admin/decks/${deckId}/areas`)}
              >
                + Quản lý khu vực
              </Button>
            </Card.Body>
          </Card>
        </Col>

        {/* =====================
            ROOM
           ===================== */}

        <Col md={6}>
          <Card className="h-100 shadow-sm">
            <Card.Body>
              <Card.Title>Phòng</Card.Title>

              <Card.Text className="text-muted">
                Quản lý các phòng trên tầng. Có thể tạo nhiều phòng cùng lúc
                theo dãy.
              </Card.Text>

              <Button
                variant="success"
                onClick={() => navigate(`/admin/decks/${deckId}/rooms`)}
              >
                + Quản lý phòng
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* =========================
          CURRENT DECK ID
         ========================= */}

      <div className="mt-4 text-muted">
        <small>Deck ID: {deckId}</small>
      </div>
    </div>
  );
}
