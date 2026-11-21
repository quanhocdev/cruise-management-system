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

function unwrapBenefits(value) {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return [];

  if (Array.isArray(value.benefits)) return value.benefits;
  if (Array.isArray(value.consumptions)) return value.consumptions;
  if (Array.isArray(value.items)) return value.items;
  if (Array.isArray(value.content)) return value.content;
  if (Array.isArray(value.data)) return value.data;

  return [];
}

function getBenefitName(benefit) {
  return (
    benefit?.resolvedName ||
    benefit?.benefitName ||
    benefit?.name ||
    benefit?.productName ||
    benefit?.serviceName ||
    benefit?.activityName ||
    benefit?.visitName ||
    benefit?.title ||
    (benefit?.type ? `Quyền lợi ${benefit.type}` : "Quyền lợi")
  );
}

function getBenefitDescription(benefit) {
  const parts = [];
  const baseDesc =
    benefit?.resolvedDescription ||
    benefit?.description ||
    benefit?.benefitDescription ||
    benefit?.content ||
    "";

  if (baseDesc) parts.push(baseDesc);

  if (
    benefit?.discountPercent !== undefined &&
    benefit?.discountPercent !== null &&
    Number(benefit.discountPercent) > 0
  ) {
    parts.push(`Ưu đãi giảm ${benefit.discountPercent}%`);
  }

  return parts.join(" • ");
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

function normalizeBenefitTone(type) {
  const normalized = String(type || "").toUpperCase();

  if (normalized.includes("PRODUCT") || normalized.includes("FOOD")) {
    return "product";
  }
  if (normalized.includes("SERVICE") || normalized.includes("RESTAURANT")) {
    return "service";
  }
  if (normalized.includes("ACTIVITY") || normalized.includes("CRUISE")) {
    return "activity";
  }
  if (normalized.includes("VISIT") || normalized.includes("SHORE")) {
    return "visit";
  }
  return "default";
}

function getBenefitIcon(tone) {
  switch (tone) {
    case "product":
      return Package;
    case "service":
      return Utensils;
    case "activity":
      return Ship;
    case "visit":
      return MapPin;
    default:
      return Gift;
  }
}

function BenefitCard({ benefit }) {
  const name = getBenefitName(benefit);
  const description = getBenefitDescription(benefit);
  const type = getBenefitType(benefit);
  const quantity = getBenefitQuantity(benefit);
  const tone = normalizeBenefitTone(type);
  const Icon = getBenefitIcon(tone);

  const status = benefit?.status || benefit?.benefitStatus;

  return (
    <Card className="trip-benefit-card h-100" data-benefit-tone={tone}>
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
  packageId = null,
  packageName = "",
  packageDescription = "",
  loading = false,
  error = "",
  onReload,
}) {
  const resolvedPackageId =
    packageId ??
    (!Array.isArray(benefits) && benefits && typeof benefits === "object"
      ? (benefits.id ?? benefits.packageId ?? null)
      : null);

  const resolvedPackageName =
    packageName ||
    (!Array.isArray(benefits) && benefits && typeof benefits === "object"
      ? benefits.name || benefits.packageName || ""
      : "");

  const resolvedPackageDescription =
    packageDescription ||
    (!Array.isArray(benefits) && benefits && typeof benefits === "object"
      ? benefits.description || ""
      : "");

  const items = unwrapBenefits(benefits);

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

      {(resolvedPackageName || resolvedPackageId) && (
        <Card className="trip-benefit-package-banner mb-3">
          <Card.Body className="d-flex flex-wrap justify-content-between align-items-start gap-2">
            <div>
              <span className="trip-benefit-banner-label">Gói tour đã đặt</span>
              <strong className="trip-benefit-banner-name">
                {resolvedPackageName || "Chưa cập nhật tên gói"}
              </strong>
              {resolvedPackageDescription && (
                <p className="trip-benefit-banner-desc">
                  {resolvedPackageDescription}
                </p>
              )}
            </div>

            {resolvedPackageId && (
              <span className="trip-benefit-banner-id">
                ID gói: <code>{resolvedPackageId}</code>
              </span>
            )}
          </Card.Body>
        </Card>
      )}

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
          <p>Chưa tìm thấy quyền lợi nào được ghi nhận cho gói này.</p>
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
              <span>Được bao gồm trong gói đặt chỗ</span>
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
