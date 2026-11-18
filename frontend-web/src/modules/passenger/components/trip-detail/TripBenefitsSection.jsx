import React from "react";
import { Card, Spinner, Alert, Button, Row, Col } from "react-bootstrap";
import {
  Gift,
  CheckCircle,
  Package,
  Utensils,
  Ship,
  MapPin,
  RefreshCw,
  ShieldCheck,
} from "lucide-react";

import "./TripBenefitsSection.css";

function unwrapList(value) {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return [];

  if (Array.isArray(value.benefits)) return value.benefits;
  if (Array.isArray(value.items)) return value.items;
  if (Array.isArray(value.content)) return value.content;
  if (Array.isArray(value.data)) return value.data;

  return [];
}

function getBenefitName(benefit) {
  return (
    benefit?.benefitName ||
    benefit?.name ||
    benefit?.productName ||
    benefit?.serviceName ||
    benefit?.activityName ||
    benefit?.visitName ||
    benefit?.title ||
    "Quyền lợi"
  );
}

function getBenefitDescription(benefit) {
  return (
    benefit?.description ||
    benefit?.benefitDescription ||
    benefit?.content ||
    ""
  );
}

function getBenefitType(benefit) {
  return (
    benefit?.benefitType ||
    benefit?.type ||
    benefit?.category ||
    benefit?.usageType ||
    ""
  );
}

function getBenefitQuantity(benefit) {
  return (
    benefit?.quantity ??
    benefit?.totalQuantity ??
    benefit?.includedQuantity ??
    benefit?.limit ??
    null
  );
}

function getBenefitId(benefit, index) {
  return (
    benefit?.id ?? benefit?.benefitId ?? benefit?.bookingBenefitId ?? index
  );
}

function getBenefitIcon(type) {
  const normalized = String(type || "").toUpperCase();

  if (normalized.includes("PRODUCT") || normalized.includes("FOOD")) {
    return Package;
  }

  if (normalized.includes("SERVICE") || normalized.includes("RESTAURANT")) {
    return Utensils;
  }

  if (normalized.includes("ACTIVITY") || normalized.includes("CRUISE")) {
    return Ship;
  }

  if (normalized.includes("VISIT") || normalized.includes("SHORE")) {
    return MapPin;
  }

  return Gift;
}

function BenefitCard({ benefit }) {
  const name = getBenefitName(benefit);
  const description = getBenefitDescription(benefit);
  const type = getBenefitType(benefit);
  const quantity = getBenefitQuantity(benefit);
  const Icon = getBenefitIcon(type);

  const status = benefit?.status || benefit?.benefitStatus;

  return (
    <Card className="trip-benefit-card h-100">
      <Card.Body>
        <div className="trip-benefit-card-header">
          <div className="trip-benefit-icon">
            <Icon size={21} />
          </div>

          <div className="trip-benefit-title">
            <h5>{name}</h5>

            {type && <span className="trip-benefit-type">{type}</span>}
          </div>
        </div>

        {description && (
          <p className="trip-benefit-description">{description}</p>
        )}

        <div className="trip-benefit-footer">
          {quantity !== null && (
            <span className="trip-benefit-quantity">
              Số lượng quyền lợi: <strong>{quantity}</strong>
            </span>
          )}

          <span className="trip-benefit-included">
            <CheckCircle size={15} />
            Được bao gồm
          </span>
        </div>

        {status && (
          <div className="trip-benefit-status">Trạng thái: {status}</div>
        )}
      </Card.Body>
    </Card>
  );
}

export default function TripBenefitsSection({
  benefits,
  loading = false,
  error = "",
  onReload,
}) {
  const items = unwrapList(benefits);

  return (
    <section className="trip-benefits-section">
      <div className="trip-benefits-heading">
        <div>
          <span className="trip-benefits-eyebrow">QUYỀN LỢI ĐẶT CHỖ</span>

          <h3>Quyền lợi của bạn</h3>

          <p>Các quyền lợi được cấp theo đơn đặt chỗ của bạn.</p>
        </div>

        {onReload && (
          <Button
            variant="outline-secondary"
            size="sm"
            onClick={onReload}
            disabled={loading}
          >
            <RefreshCw size={15} className="me-1" />
            Tải lại
          </Button>
        )}
      </div>

      {loading && (
        <div className="trip-benefits-state">
          <Spinner animation="border" size="sm" />
          <span>Đang tải quyền lợi...</span>
        </div>
      )}

      {!loading && error && (
        <Alert variant="danger" className="trip-benefits-alert">
          <div>{error}</div>

          {onReload && (
            <Button
              variant="outline-danger"
              size="sm"
              className="mt-2"
              onClick={onReload}
            >
              Thử lại
            </Button>
          )}
        </Alert>
      )}

      {!loading && !error && items.length === 0 && (
        <div className="trip-benefits-empty">
          <ShieldCheck size={32} />
          <h5>Chưa có thông tin quyền lợi</h5>
          <p>Chưa tìm thấy quyền lợi nào được ghi nhận cho booking này.</p>
        </div>
      )}

      {!loading && !error && items.length > 0 && (
        <>
          <div className="trip-benefits-summary">
            <div className="trip-benefits-summary-icon">
              <Gift size={19} />
            </div>

            <div>
              <strong>{items.length} quyền lợi</strong>
              <span>Được ghi nhận trong đơn đặt chỗ</span>
            </div>
          </div>

          <Row className="g-3">
            {items.map((benefit, index) => (
              <Col key={getBenefitId(benefit, index)} xs={12} md={6} xl={4}>
                <BenefitCard benefit={benefit} />
              </Col>
            ))}
          </Row>
        </>
      )}
    </section>
  );
}
