import { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Form,
  Button,
  Alert,
  Spinner,
} from "react-bootstrap";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";
import useBookings from "../hooks/useBookings";
import usePassengers from "../hooks/usePassengers";
import { usePublicTourDetail } from "../../guest/hooks/usePublicTours";
import "../styles/PassengerBooking.css";
import { usePayment } from "../hooks/usePayment";

export default function PassengerBooking() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const tourId = searchParams.get("tourId");
  const packageIdFromUrl = searchParams.get("packageId");

  const { tour, loading: tourLoading } = usePublicTourDetail(tourId);
  const {
    createBooking,
    submitting,
    error: bookingError,
    success,
  } = useBookings();
  const { passengers, loadPassengers } = usePassengers();

  const [tourPackageId, setTourPackageId] = useState(packageIdFromUrl || "");
  const [primaryContactName, setPrimaryContactName] = useState(
    user?.fullName || "",
  );
  const [primaryContactPhone, setPrimaryContactPhone] = useState(
    user?.phone || "",
  );

  const [selectedPassengers, setSelectedPassengers] = useState([
    {
      fullName: "",
      dateOfBirth: "",
      gender: "Nam",
      phoneNumber: "",
      email: "",
      idCardType: "CCCD",
      identificationNumber: "",
      documentNote: "",
      idCardImage: null,
    },
  ]);

  useEffect(() => {
    // Không cần truyền user.id nữa, backend tự lấy qua token
    loadPassengers();
  }, [loadPassengers]);

  useEffect(() => {
    if (!tourPackageId && tour?.packages && tour.packages.length > 0) {
      setTourPackageId(tour.packages[0].id);
    }
  }, [tour, tourPackageId]);

  const selectedPackageInfo = tour?.packages?.find(
    (p) => String(p.id) === String(tourPackageId),
  );

  const handlePassengerChange = (index, field, value) => {
    const updated = [...selectedPassengers];
    updated[index][field] = value;
    setSelectedPassengers(updated);
  };
  const calculateTotalPrice = () => {
    if (!selectedPackageInfo) return "0 đ";

    const numPassengers = selectedPassengers.length;
    if (numPassengers === 0) return "0 đ";

    // Lấy số người tối đa của gói (nếu backend lưu tên là maxPassengers hoặc capacity)
    const packageCapacity = Number(
      selectedPackageInfo.maxPassengers || selectedPackageInfo.capacity || 1,
    );
    const packagePrice = Number(selectedPackageInfo.price);

    // Tính số lượng gói cần mua (ví dụ: gói 4 người mà có 5 khách thì phải mua 2 gói)
    const numberOfPackagesNeeded = Math.ceil(numPassengers / packageCapacity);

    const total = numberOfPackagesNeeded * packagePrice;
    return `${total.toLocaleString("vi-VN")} đ`;
  };

  const addPassengerSlot = () => {
    setSelectedPassengers([
      ...selectedPassengers,
      {
        fullName: "",
        dateOfBirth: "",
        gender: "Nam",
        phoneNumber: "",
        email: "",
        idCardType: "CCCD",
        identificationNumber: "",
        documentNote: "",
        idCardImage: null,
      },
    ]);
  };

  const removePassengerSlot = (index) => {
    if (selectedPassengers.length === 1) return;
    setSelectedPassengers(selectedPassengers.filter((_, i) => i !== index));
  };

  const handleSelectExistingPassenger = (index, passengerId) => {
    const found = passengers.find((p) => p.id === Number(passengerId));
    if (found) {
      const updated = [...selectedPassengers];
      updated[index] = {
        fullName: found.fullName || "",
        dateOfBirth: found.dateOfBirth || "",
        gender: found.gender || "Nam",
        phoneNumber: found.phoneNumber || "",
        email: found.email || "",
        idCardType: found.idCardType || "CCCD",
        identificationNumber: found.identificationNumber || "",
        documentNote: found.documentNote || "",
        idCardImage: null,
      };
      setSelectedPassengers(updated);
    }
  };

  // Bên trong component PassengerBooking:
  const { fetchPaymentUrl } = usePayment();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!tourId || !tourPackageId) {
      alert("Vui lòng chọn đầy đủ thông tin Tour và Gói Tour.");
      return;
    }

    const formData = new FormData();
    formData.append("tourId", tourId);
    formData.append("tourPackageId", tourPackageId);

    if (selectedPackageInfo?.price) {
      formData.append("unitPrice", selectedPackageInfo.price);
    }

    formData.append("primaryContactName", primaryContactName);
    formData.append("primaryContactPhone", primaryContactPhone);

    selectedPassengers.forEach((p, index) => {
      formData.append(`passengers[${index}].fullName`, p.fullName);
      formData.append(`passengers[${index}].dateOfBirth`, p.dateOfBirth);
      formData.append(`passengers[${index}].gender`, p.gender);
      if (p.phoneNumber)
        formData.append(`passengers[${index}].phoneNumber`, p.phoneNumber);
      if (p.email) formData.append(`passengers[${index}].email`, p.email);
      formData.append(`passengers[${index}].idCardType`, p.idCardType);
      formData.append(
        `passengers[${index}].identificationNumber`,
        p.identificationNumber,
      );
      if (p.documentNote)
        formData.append(`passengers[${index}].documentNote`, p.documentNote);
      if (p.idCardImage) {
        formData.append(`passengers[${index}].idCardImage`, p.idCardImage);
      }
    });

    try {
      // Gọi API tạo booking, hệ thống ngầm sẽ tự lo việc bắn Kafka sang Payment Service
      await createBooking(formData);

      alert("Đặt vé thành công!");
      navigate(`/passenger/bookings`); // Nhảy thẳng về trang danh sách vé của tôi
    } catch (err) {
      console.error("Lỗi chi tiết từ Server trả về:", err.response || err);
      alert(
        err.response?.data?.message ||
          err.message ||
          "Đã xảy ra lỗi trong quá trình đặt vé.",
      );
    }
  };
  if (tourLoading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2 text-muted">Đang tải thông tin đặt vé...</p>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <div className="mb-4">
        <Button
          variant="outline-secondary"
          size="sm"
          onClick={() => navigate(-1)}
          className="rounded-pill"
        >
          ← Quay lại
        </Button>
      </div>

      <h2 className="fw-bold text-dark mb-4">🎫 Tiến Hành Đặt Vé Trực Tuyến</h2>

      {bookingError && <Alert variant="danger">{bookingError}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}

      <Form onSubmit={handleSubmit}>
        <Row className="g-4">
          <Col lg={8}>
            {/* Lựa chọn Gói Tour */}
            <Card className="border-0 shadow-sm rounded-4 p-4 mb-4">
              <h5 className="fw-bold text-primary mb-3">1. Chọn Gói Tour</h5>
              <Form.Group className="mb-3">
                <Form.Label className="small fw-semibold">
                  Các gói tour khả dụng
                </Form.Label>
                <Form.Select
                  value={tourPackageId}
                  onChange={(e) => setTourPackageId(e.target.value)}
                  required
                >
                  <option value="" disabled>
                    -- Chọn gói tour --
                  </option>
                  {tour?.packages?.map((pkg) => (
                    <option key={pkg.id} value={pkg.id}>
                      {pkg.name} - {Number(pkg.price).toLocaleString("vi-VN")} đ
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>

              {selectedPackageInfo && (
                <div className="p-3 bg-light rounded-3 border mt-3">
                  <div className="d-flex justify-content-between align-items-center mb-1">
                    <h6 className="fw-bold text-dark m-0">
                      🎁 {selectedPackageInfo.name}
                    </h6>
                    <span className="text-primary fw-bold">
                      {Number(selectedPackageInfo.price).toLocaleString(
                        "vi-VN",
                      )}{" "}
                      đ / người
                    </span>
                  </div>
                  <p className="text-muted small mb-2">
                    {selectedPackageInfo.description ||
                      "Không có mô tả chi tiết cho gói này."}
                  </p>

                  {selectedPackageInfo.benefits &&
                    selectedPackageInfo.benefits.length > 0 && (
                      <>
                        <div className="fw-semibold text-secondary small mb-1">
                          Quyền lợi bao gồm:
                        </div>
                        <ul className="small text-muted ps-3 mb-0">
                          {selectedPackageInfo.benefits.map((benefit, idx) => (
                            <li key={idx}>
                              {benefit.type} (Số lượng: {benefit.quantity}
                              {benefit.discountPercent
                                ? `, Giảm: ${benefit.discountPercent}%`
                                : ""}
                              )
                            </li>
                          ))}
                        </ul>
                      </>
                    )}
                </div>
              )}
            </Card>

            {/* Thông tin liên hệ */}
            <Card className="border-0 shadow-sm rounded-4 p-4 mb-4">
              <h5 className="fw-bold text-primary mb-3">
                2. Thông tin liên hệ chính
              </h5>
              <Row>
                <Col md={6} className="mb-3">
                  <Form.Label className="small fw-semibold">
                    Họ tên người liên hệ
                  </Form.Label>
                  <Form.Control
                    type="text"
                    required
                    value={primaryContactName}
                    onChange={(e) => setPrimaryContactName(e.target.value)}
                    placeholder="Nhập họ tên"
                  />
                </Col>
                <Col md={6} className="mb-3">
                  <Form.Label className="small fw-semibold">
                    Số điện thoại liên hệ
                  </Form.Label>
                  <Form.Control
                    type="text"
                    required
                    value={primaryContactPhone}
                    onChange={(e) => setPrimaryContactPhone(e.target.value)}
                    placeholder="Nhập số điện thoại"
                  />
                </Col>
              </Row>
            </Card>

            {/* Danh sách hành khách */}
            <Card className="border-0 shadow-sm rounded-4 p-4">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold text-primary m-0">
                  3. Danh sách hành khách tham gia
                </h5>
                <Button
                  variant="outline-primary"
                  size="sm"
                  onClick={addPassengerSlot}
                  className="rounded-pill"
                >
                  + Thêm hành khách
                </Button>
              </div>

              {selectedPassengers.map((p, index) => (
                <div
                  key={index}
                  className="p-3 bg-light rounded-3 mb-3 border position-relative"
                >
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <span className="fw-bold small text-secondary">
                      Hành khách #{index + 1}
                    </span>
                    {selectedPassengers.length > 1 && (
                      <Button
                        variant="link"
                        className="text-danger p-0 small"
                        onClick={() => removePassengerSlot(index)}
                      >
                        Xóa
                      </Button>
                    )}
                  </div>

                  {passengers.length > 0 && (
                    <div className="mb-3">
                      <Form.Select
                        size="sm"
                        onChange={(e) =>
                          handleSelectExistingPassenger(index, e.target.value)
                        }
                        defaultValue=""
                      >
                        <option value="" disabled>
                          -- Chọn nhanh từ danh sách hành khách đã lưu --
                        </option>
                        {passengers.map((saved) => (
                          <option key={saved.id} value={saved.id}>
                            {saved.fullName} (
                            {saved.identificationNumber || "Chưa có CCCD"})
                          </option>
                        ))}
                      </Form.Select>
                    </div>
                  )}

                  <Row>
                    <Col md={6} className="mb-2">
                      <Form.Label className="small">Họ và tên</Form.Label>
                      <Form.Control
                        size="sm"
                        type="text"
                        required
                        value={p.fullName}
                        onChange={(e) =>
                          handlePassengerChange(
                            index,
                            "fullName",
                            e.target.value,
                          )
                        }
                      />
                    </Col>
                    <Col md={6} className="mb-2">
                      <Form.Label className="small">Ngày sinh</Form.Label>
                      <Form.Control
                        size="sm"
                        type="date"
                        required
                        value={p.dateOfBirth}
                        onChange={(e) =>
                          handlePassengerChange(
                            index,
                            "dateOfBirth",
                            e.target.value,
                          )
                        }
                      />
                    </Col>
                    <Col md={4} className="mb-2">
                      <Form.Label className="small">Giới tính</Form.Label>
                      <Form.Select
                        size="sm"
                        value={p.gender}
                        onChange={(e) =>
                          handlePassengerChange(index, "gender", e.target.value)
                        }
                      >
                        <option value="Nam">Nam</option>
                        <option value="Nữ">Nữ</option>
                        <option value="Khác">Khác</option>
                      </Form.Select>
                    </Col>
                    <Col md={4} className="mb-2">
                      <Form.Label className="small">Loại giấy tờ</Form.Label>
                      <Form.Select
                        size="sm"
                        value={p.idCardType}
                        onChange={(e) =>
                          handlePassengerChange(
                            index,
                            "idCardType",
                            e.target.value,
                          )
                        }
                      >
                        <option value="CCCD">CCCD</option>
                        <option value="CMND">CMND</option>
                        <option value="Passport">Passport</option>
                      </Form.Select>
                    </Col>
                    <Col md={4} className="mb-2">
                      <Form.Label className="small">
                        Số định danh / CCCD
                      </Form.Label>
                      <Form.Control
                        size="sm"
                        type="text"
                        required
                        value={p.identificationNumber}
                        onChange={(e) =>
                          handlePassengerChange(
                            index,
                            "identificationNumber",
                            e.target.value,
                          )
                        }
                      />
                    </Col>
                    <Col md={6} className="mb-2">
                      <Form.Label className="small">Số điện thoại</Form.Label>
                      <Form.Control
                        size="sm"
                        type="text"
                        value={p.phoneNumber}
                        onChange={(e) =>
                          handlePassengerChange(
                            index,
                            "phoneNumber",
                            e.target.value,
                          )
                        }
                      />
                    </Col>
                    <Col md={6} className="mb-2">
                      <Form.Label className="small">Email</Form.Label>
                      <Form.Control
                        size="sm"
                        type="email"
                        value={p.email}
                        onChange={(e) =>
                          handlePassengerChange(index, "email", e.target.value)
                        }
                      />
                    </Col>

                    {/* Thêm ô nhập ghi chú giấy tờ tùy thân */}
                    <Col md={12} className="mb-2">
                      <Form.Label className="small">
                        Ghi chú giấy tờ (Tùy chọn)
                      </Form.Label>
                      <Form.Control
                        size="sm"
                        type="text"
                        placeholder="Ví dụ: Cấp tại Công an TP.HCM..."
                        value={p.documentNote}
                        onChange={(e) =>
                          handlePassengerChange(
                            index,
                            "documentNote",
                            e.target.value,
                          )
                        }
                      />
                    </Col>
                    <Col md={12} className="mb-2 mt-1">
                      <Form.Label className="small fw-semibold text-primary">
                        📷 Tải ảnh CCCD / Giấy tờ tùy thân từ máy
                      </Form.Label>

                      <Form.Control
                        size="sm"
                        type="file"
                        accept="image/*"
                        onChange={(e) => {
                          const file = e.target.files[0];
                          if (file) {
                            handlePassengerChange(index, "idCardImage", file);
                          }
                        }}
                      />

                      {p.idCardImage && (
                        <div className="text-success small mt-1">
                          ✓ Đã chọn file: {p.idCardImage.name}
                        </div>
                      )}
                    </Col>
                  </Row>
                </div>
              ))}
            </Card>
          </Col>

          {/* Tóm tắt đơn hàng */}
          <Col lg={4}>
            <Card
              className="border-0 shadow-sm rounded-4 p-4 sticky-top"
              style={{ top: "20px" }}
            >
              <h5 className="fw-bold text-dark mb-3">Tóm tắt đặt chỗ</h5>
              <p className="text-muted small mb-1">
                Tên Tour:{" "}
                <strong className="text-dark">{tour?.name || "N/A"}</strong>
              </p>
              <p className="text-muted small mb-1">
                Mã Tour:{" "}
                <strong className="text-dark">{tour?.code || tourId}</strong>
              </p>
              <p className="text-muted small mb-3">
                Gói chọn:{" "}
                <strong className="text-primary">
                  {selectedPackageInfo?.name || "Chưa chọn"}
                </strong>
              </p>
              <hr />
              <div className="d-flex justify-content-between mb-2">
                <span className="text-muted">Đơn giá: </span>
                <span className="fw-bold text-primary fs-5">
                  {calculateTotalPrice()}
                </span>
              </div>
              <div className="d-flex justify-content-between mb-3">
                <span className="text-muted">Tổng hành khách:</span>
                <span className="fw-bold">
                  {selectedPassengers.length} người
                </span>
              </div>
              <div className="d-flex justify-content-between mb-3 border-top pt-2">
                <span className="fw-bold text-dark">Tổng tiền dự kiến:</span>
                <span className="fw-bold text-primary fs-5">
                  {selectedPackageInfo
                    ? `${(Number(selectedPackageInfo.price) * selectedPassengers.length).toLocaleString()} đ`
                    : "0 đ"}
                </span>
              </div>
              <Button
                type="submit"
                variant="success"
                size="lg"
                className="w-100 rounded-pill fw-bold shadow-sm"
                disabled={submitting || !tourPackageId}
              >
                {submitting ? (
                  <Spinner animation="border" size="sm" />
                ) : (
                  "Xác Nhận Đặt Vé"
                )}
              </Button>
            </Card>
          </Col>
        </Row>
      </Form>
    </Container>
  );
}
