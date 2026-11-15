import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Button,
  Card,
  Col,
  Container,
  Row,
  Spinner,
} from "react-bootstrap";
import adminProfileService from "../services/adminProfileService";

const GENDER_LABELS = {
  MALE: "Nam",
  FEMALE: "Nữ",
  OTHER: "Khác",
};

function InfoField({ label, value }) {
  return (
    <Col md={6} className="mb-3">
      <div className="text-muted small mb-1">{label}</div>
      <div className="fw-medium">{value || "Chưa cập nhật"}</div>
    </Col>
  );
}

export default function AdminProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    const loadProfile = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await adminProfileService.getByUserId(userId);

        if (active) {
          setProfile(response.data);
        }
      } catch (err) {
        if (active) {
          setError(
            err.response?.status === 404
              ? "Tài khoản này chưa có hồ sơ."
              : err.response?.data?.message || "Không thể tải hồ sơ tài khoản.",
          );
        }
      } finally {
        if (active) setLoading(false);
      }
    };

    loadProfile();

    return () => {
      active = false;
    };
  }, [userId]);

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" />
        <div className="mt-2 text-muted">Đang tải hồ sơ...</div>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="mb-1">Hồ sơ tài khoản</h3>
          <div className="text-muted small">
            Thông tin hồ sơ của tài khoản được chọn
          </div>
        </div>

        <Button
          variant="outline-secondary"
          onClick={() => navigate("/admin/accounts")}
        >
          <i className="bi bi-arrow-left me-2" />
          Quay lại
        </Button>
      </div>

      {error && <Alert variant="warning">{error}</Alert>}

      {profile && (
        <>
          <Card className="border-0 shadow-sm mb-4">
            <Card.Body className="d-flex align-items-center gap-3">
              {profile.avatarUrl ? (
                <img
                  src={profile.avatarUrl}
                  alt="Ảnh đại diện"
                  className="rounded-circle"
                  style={{
                    width: 88,
                    height: 88,
                    objectFit: "cover",
                  }}
                />
              ) : (
                <div
                  className="rounded-circle bg-light d-flex align-items-center justify-content-center fw-bold text-secondary"
                  style={{ width: 88, height: 88, fontSize: 28 }}
                >
                  {(profile.fullName || "?").charAt(0).toUpperCase()}
                </div>
              )}

              <div>
                <h5 className="mb-1">
                  {profile.fullName || "Chưa cập nhật họ tên"}
                </h5>
                <div className="text-muted small">
                  User ID: {profile.userId}
                </div>
              </div>
            </Card.Body>
          </Card>

          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-white py-3">
              <h5 className="mb-0">Thông tin cá nhân</h5>
            </Card.Header>

            <Card.Body className="p-4">
              <Row>
                <InfoField label="Họ và tên" value={profile.fullName} />
                <InfoField label="Ngày sinh" value={profile.dateOfBirth} />
                <InfoField label="Số điện thoại" value={profile.phone} />
                <InfoField
                  label="Giới tính"
                  value={GENDER_LABELS[profile.gender] || profile.gender}
                />
                <InfoField label="Địa chỉ" value={profile.address} />
                <InfoField label="Quốc tịch" value={profile.nationality} />
                <InfoField
                  label="Số giấy tờ tùy thân"
                  value={profile.identityNumber}
                />
              </Row>
            </Card.Body>
          </Card>
        </>
      )}
    </Container>
  );
}
