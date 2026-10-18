import { Row, Col, Form, Button } from "react-bootstrap";
import "./PassengerForm.css";

export default function PassengerForm({
  index,
  data,
  canRemove,
  savedPassengers,
  onChange,
  onRemove,
  onPickSaved,
}) {
  const set = (field) => (e) => onChange(index, field, e.target.value);

  return (
    <div className="passenger-form">
      <div className="passenger-form__head">
        <h3 className="passenger-form__title">Hành khách {index + 1}</h3>
        {canRemove && (
          <Button
            variant="link"
            className="passenger-form__remove"
            onClick={() => onRemove(index)}
          >
            Xóa
          </Button>
        )}
      </div>

      {savedPassengers.length > 0 && (
        <Form.Select
          size="sm"
          className="passenger-form__saved"
          defaultValue=""
          onChange={(e) => onPickSaved(index, e.target.value)}
        >
          <option value="" disabled>
            Chọn nhanh từ danh sách hành khách đã lưu
          </option>
          {savedPassengers.map((saved) => (
            <option key={saved.id} value={saved.id}>
              {saved.fullName} ({saved.identificationNumber || "Chưa có CCCD"})
            </option>
          ))}
        </Form.Select>
      )}

      <Row className="g-3">
        <Col md={6}>
          <Form.Label>Họ và tên</Form.Label>
          <Form.Control
            size="sm"
            type="text"
            required
            value={data.fullName}
            onChange={set("fullName")}
          />
        </Col>
        <Col md={6}>
          <Form.Label>Ngày sinh</Form.Label>
          <Form.Control
            size="sm"
            type="date"
            required
            value={data.dateOfBirth}
            onChange={set("dateOfBirth")}
          />
        </Col>

        <Col md={4}>
          <Form.Label>Giới tính</Form.Label>
          <Form.Select size="sm" value={data.gender} onChange={set("gender")}>
            <option value="Nam">Nam</option>
            <option value="Nữ">Nữ</option>
            <option value="Khác">Khác</option>
          </Form.Select>
        </Col>
        <Col md={4}>
          <Form.Label>Loại giấy tờ</Form.Label>
          <Form.Select
            size="sm"
            value={data.idCardType}
            onChange={set("idCardType")}
          >
            <option value="CCCD">CCCD</option>
            <option value="CMND">CMND</option>
            <option value="Passport">Passport</option>
          </Form.Select>
        </Col>
        <Col md={4}>
          <Form.Label>Số định danh / CCCD</Form.Label>
          <Form.Control
            size="sm"
            type="text"
            required
            value={data.identificationNumber}
            onChange={set("identificationNumber")}
          />
        </Col>

        <Col md={6}>
          <Form.Label>Số điện thoại</Form.Label>
          <Form.Control
            size="sm"
            type="tel"
            value={data.phoneNumber}
            onChange={set("phoneNumber")}
          />
        </Col>
        <Col md={6}>
          <Form.Label>Email</Form.Label>
          <Form.Control
            size="sm"
            type="email"
            value={data.email}
            onChange={set("email")}
          />
        </Col>

        <Col xs={12}>
          <Form.Label>Ghi chú giấy tờ (tùy chọn)</Form.Label>
          <Form.Control
            size="sm"
            type="text"
            placeholder="Ví dụ: Cấp tại Công an TP.HCM"
            value={data.documentNote}
            onChange={set("documentNote")}
          />
        </Col>

        <Col xs={12}>
          <Form.Label>Ảnh CCCD / giấy tờ tùy thân</Form.Label>
          <Form.Control
            size="sm"
            type="file"
            accept="image/*"
            onChange={(e) => {
              const file = e.target.files[0];
              if (file) onChange(index, "idCardImage", file);
            }}
          />
          {data.idCardImage && (
            <div className="passenger-form__file">
              Đã chọn: {data.idCardImage.name}
            </div>
          )}
        </Col>
      </Row>
    </div>
  );
}
