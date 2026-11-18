import React from "react";
import { Card, Spinner, Alert, Button, Row, Col } from "react-bootstrap";
import {
  Package,
  CheckCircle,
  Clock,
  DollarSign,
  RefreshCw,
} from "lucide-react";

import "./TripPackagesSection.css";

function unwrapList(value) {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return [];

  if (Array.isArray(value.packages)) return value.packages;
  if (Array.isArray(value.items)) return value.items;
  if (Array.isArray(value.content)) return value.content;
  if (Array.isArray(value.data)) return value.data;

  return [];
}

function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "Chưa cập nhật";
  }

  const number = Number(value);

  if (!Number.isFinite(number)) return value;

  return `${number.toLocaleString("vi-VN")} ₫`;
}

function getPackageName(item) {
  return (
    item?.packageName ||
    item?.name ||
    item?.tourPackageName ||
    item?.title ||
    "Gói dịch vụ"
  );
}

function getPackageDescription(item) {
  return (
    item?.description ||
    item?.packageDescription ||
    item?.content ||
    "Chưa có mô tả cho gói dịch vụ này."
  );
}

function getPackagePrice(item) {
  return (
    item?.price ?? item?.packagePrice ?? item?.sellingPrice ?? item?.totalPrice
  );
}

function getPackageId(item, index) {
  return item?.tourPackageId ?? item?.packageId ?? item?.id ?? index;
}

function PackageCard({ item }) {
  const name = getPackageName(item);
  const description = getPackageDescription(item);
  const price = getPackagePrice(item);

  const benefits = Array.isArray(item?.benefits)
    ? item.benefits
    : Array.isArray(item?.features)
      ? item.features
      : [];

  return (
    <Card className="trip-package-card h-100">
      <Card.Body>
        <div className="trip-package-card-header">
          <div className="trip-package-icon">
            <Package size={22} />
          </div>

          <div className="trip-package-title">
            <h5>{name}</h5>
            {item?.status && (
              <span className="trip-package-status">{item.status}</span>
            )}
          </div>
        </div>

        <p className="trip-package-description">{description}</p>

        {benefits.length > 0 && (
          <div className="trip-package-benefits">
            <h6>Thông tin gói</h6>

            <ul>
              {benefits.map((benefit, index) => {
                const label =
                  typeof benefit === "string"
                    ? benefit
                    : benefit?.name || benefit?.description || benefit?.title;

                if (!label) return null;

                return (
                  <li key={benefit?.id ?? index}>
                    <CheckCircle size={15} />
                    <span>{label}</span>
                  </li>
                );
              })}
            </ul>
          </div>
        )}

        <div className="trip-package-meta">
          {item?.duration && (
            <span>
              <Clock size={15} />
              {item.duration}
            </span>
          )}

          <span className="trip-package-price">
            <DollarSign size={15} />
            {formatPrice(price)}
          </span>
        </div>
      </Card.Body>
    </Card>
  );
}

export default function TripPackagesSection({
  packages,
  loading = false,
  error = "",
  onReload,
}) {
  const items = unwrapList(packages);

  return (
    <section className="trip-packages-section">
      <div className="trip-packages-heading">
        <div>
          <span className="trip-packages-eyebrow">DỊCH VỤ CHUYẾN ĐI</span>

          <h3>Gói dịch vụ</h3>

          <p>Các gói dịch vụ được cung cấp trong hành trình.</p>
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
        <div className="trip-packages-state">
          <Spinner animation="border" size="sm" />
          <span>Đang tải danh sách gói dịch vụ...</span>
        </div>
      )}

      {!loading && error && (
        <Alert variant="danger" className="trip-packages-alert">
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
        <div className="trip-packages-empty">
          <Package size={30} />
          <h5>Chưa có gói dịch vụ</h5>
          <p>Hiện chưa có thông tin gói dịch vụ cho chuyến đi này.</p>
        </div>
      )}

      {!loading && !error && items.length > 0 && (
        <Row className="g-3">
          {items.map((item, index) => (
            <Col key={getPackageId(item, index)} xs={12} md={6} xl={4}>
              <PackageCard item={item} />
            </Col>
          ))}
        </Row>
      )}
    </section>
  );
}
