import React, { useMemo, useState } from "react";
import { Card, Spinner, Alert, Button, Row, Col, Badge } from "react-bootstrap";
import {
  Package,
  Utensils,
  Ship,
  MapPin,
  Clock,
  CalendarDays,
  CheckCircle,
  XCircle,
  AlertCircle,
  RefreshCw,
  History,
  ChevronDown,
  ChevronUp,
} from "lucide-react";

import "./TripUsagesSection.css";

const USAGE_GROUPS = [
  {
    key: "products",
    title: "Sản phẩm",
    description: "Lịch sử sử dụng sản phẩm",
    icon: Package,
    dataKey: "productUsages",
  },
  {
    key: "services",
    title: "Dịch vụ",
    description: "Lịch sử sử dụng dịch vụ",
    icon: Utensils,
    dataKey: "serviceUsages",
  },
  {
    key: "activities",
    title: "Hoạt động trên tàu",
    description: "Các hoạt động hành khách đã tham gia",
    icon: Ship,
    dataKey: "activityCruiseUsages",
  },
  {
    key: "visits",
    title: "Tham quan",
    description: "Các địa điểm và hoạt động tham quan",
    icon: MapPin,
    dataKey: "visitUsages",
  },
];

function unwrapList(value) {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return [];

  const possibleKeys = [
    "usages",
    "items",
    "content",
    "data",
    "records",
    "results",
  ];

  for (const key of possibleKeys) {
    if (Array.isArray(value[key])) {
      return value[key];
    }
  }

  return [];
}

function formatDate(value) {
  if (!value) return null;

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function getUsageName(usage, groupKey) {
  const commonName =
    usage?.name ||
    usage?.itemName ||
    usage?.targetName ||
    usage?.usageName ||
    usage?.title;

  if (commonName) return commonName;

  const specificNames = {
    products: usage?.productName,
    services: usage?.serviceName,
    activities: usage?.activityName,
    visits: usage?.visitName || usage?.destinationName || usage?.portName,
  };

  return specificNames[groupKey] || "Trải nghiệm";
}

function getUsageDate(usage) {
  return (
    usage?.usedAt ||
    usage?.usageDate ||
    usage?.completedAt ||
    usage?.checkInAt ||
    usage?.createdAt ||
    usage?.startTime
  );
}

function getUsageStatus(usage) {
  return usage?.status || usage?.usageStatus || usage?.completionStatus || "";
}

function getStatusInfo(status) {
  const normalized = String(status || "").toUpperCase();

  if (
    ["COMPLETED", "USED", "SUCCESS", "CHECKED_IN", "FINISHED"].includes(
      normalized,
    )
  ) {
    return {
      label: "Đã sử dụng",
      className: "success",
      icon: CheckCircle,
    };
  }

  if (["CANCELLED", "CANCELED", "FAILED", "REJECTED"].includes(normalized)) {
    return {
      label: "Đã hủy",
      className: "danger",
      icon: XCircle,
    };
  }

  if (
    ["PENDING", "WAITING", "PROCESSING", "IN_PROGRESS"].includes(normalized)
  ) {
    return {
      label: "Đang xử lý",
      className: "pending",
      icon: Clock,
    };
  }

  return {
    label: status || "Chưa cập nhật",
    className: "secondary",
    icon: AlertCircle,
  };
}

function getUsageId(usage, index) {
  return (
    usage?.id ??
    usage?.usageId ??
    usage?.bookingUsageId ??
    usage?.bookingPassengerId ??
    index
  );
}

function UsageCard({ usage, groupKey }) {
  const name = getUsageName(usage, groupKey);

  const description = usage?.description || usage?.note || usage?.remark || "";

  const usedAt = getUsageDate(usage);
  const status = getUsageStatus(usage);
  const statusInfo = getStatusInfo(status);
  const StatusIcon = statusInfo.icon;

  const quantity =
    usage?.quantity ?? usage?.usedQuantity ?? usage?.amount ?? null;

  const location =
    usage?.locationName ||
    usage?.areaName ||
    usage?.destinationName ||
    usage?.portName ||
    "";

  const staffName = usage?.staffName || usage?.handledByName || "";

  return (
    <Card className="trip-usage-card h-100">
      <Card.Body>
        <div className="trip-usage-card-top">
          <h5>{name}</h5>

          <Badge
            bg="light"
            text="dark"
            className={`trip-usage-status trip-usage-status-${statusInfo.className}`}
          >
            <StatusIcon size={13} />
            {statusInfo.label}
          </Badge>
        </div>

        {description && <p className="trip-usage-description">{description}</p>}

        <div className="trip-usage-details">
          {usedAt && (
            <div className="trip-usage-detail-item">
              <CalendarDays size={15} />
              <span>{formatDate(usedAt)}</span>
            </div>
          )}

          {location && (
            <div className="trip-usage-detail-item">
              <MapPin size={15} />
              <span>{location}</span>
            </div>
          )}

          {quantity !== null && (
            <div className="trip-usage-detail-item">
              <Package size={15} />
              <span>Số lượng: {quantity}</span>
            </div>
          )}

          {staffName && (
            <div className="trip-usage-detail-item">
              <History size={15} />
              <span>Nhân viên: {staffName}</span>
            </div>
          )}
        </div>
      </Card.Body>
    </Card>
  );
}

function UsageGroup({ group, usages, loading, error, onReload }) {
  const [expanded, setExpanded] = useState(true);

  const Icon = group.icon;
  const items = useMemo(() => unwrapList(usages), [usages]);

  const successfulCount = items.filter((item) => {
    const status = getUsageStatus(item).toUpperCase();

    return ["COMPLETED", "USED", "SUCCESS", "CHECKED_IN", "FINISHED"].includes(
      status,
    );
  }).length;

  return (
    <div className="trip-usage-group">
      <div className="trip-usage-group-header">
        <button
          type="button"
          className="trip-usage-group-toggle"
          onClick={() => setExpanded((previous) => !previous)}
          aria-expanded={expanded}
        >
          <div className="trip-usage-group-icon">
            <Icon size={20} />
          </div>

          <div className="trip-usage-group-heading">
            <h5>{group.title}</h5>
            <p>{group.description}</p>
          </div>

          <div className="trip-usage-group-count">
            {loading ? (
              <Spinner animation="border" size="sm" />
            ) : (
              <span>{items.length}</span>
            )}
          </div>

          {expanded ? <ChevronUp size={19} /> : <ChevronDown size={19} />}
        </button>

        {onReload && (
          <Button
            variant="light"
            size="sm"
            className="trip-usage-reload"
            onClick={onReload}
            disabled={loading}
            aria-label={`Tải lại ${group.title}`}
          >
            <RefreshCw size={15} />
          </Button>
        )}
      </div>

      {expanded && (
        <div className="trip-usage-group-content">
          {loading && (
            <div className="trip-usage-state">
              <Spinner animation="border" size="sm" />
              <span>Đang tải dữ liệu...</span>
            </div>
          )}

          {!loading && error && (
            <Alert variant="danger" className="trip-usage-alert">
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
            <div className="trip-usage-empty">
              Chưa có lịch sử sử dụng trong nhóm này.
            </div>
          )}

          {!loading && !error && items.length > 0 && (
            <>
              <div className="trip-usage-group-summary">
                <span>
                  Tổng cộng <strong>{items.length}</strong> lượt ghi nhận
                </span>

                <span>
                  Đã sử dụng <strong>{successfulCount}</strong>
                </span>
              </div>

              <Row className="g-3">
                {items.map((usage, index) => (
                  <Col key={getUsageId(usage, index)} xs={12} md={6}>
                    <UsageCard usage={usage} groupKey={group.key} />
                  </Col>
                ))}
              </Row>
            </>
          )}
        </div>
      )}
    </div>
  );
}

export default function TripUsagesSection({
  productUsages,
  serviceUsages,
  activityCruiseUsages,
  visitUsages,
  loading = {},
  errors = {},
  onReload = {},
}) {
  const groups = [
    {
      ...USAGE_GROUPS[0],
      usages: productUsages,
    },
    {
      ...USAGE_GROUPS[1],
      usages: serviceUsages,
    },
    {
      ...USAGE_GROUPS[2],
      usages: activityCruiseUsages,
    },
    {
      ...USAGE_GROUPS[3],
      usages: visitUsages,
    },
  ];

  const totalCount = groups.reduce(
    (total, group) => total + unwrapList(group.usages).length,
    0,
  );

  return (
    <section className="trip-usages-section">
      <div className="trip-usages-heading">
        <div>
          <span className="trip-usages-eyebrow">LỊCH SỬ TRẢI NGHIỆM</span>

          <h3>Lịch sử sử dụng quyền lợi</h3>

          <p>
            Theo dõi các sản phẩm, dịch vụ và hoạt động đã sử dụng trong chuyến
            đi.
          </p>
        </div>
      </div>

      <div className="trip-usages-summary">
        <div className="trip-usages-summary-icon">
          <History size={21} />
        </div>

        <div>
          <strong>{totalCount} lượt ghi nhận</strong>
          <span>Tổng số lượt sử dụng từ các nhóm dữ liệu</span>
        </div>
      </div>

      <div className="trip-usages-groups">
        {groups.map((group) => (
          <UsageGroup
            key={group.key}
            group={group}
            usages={group.usages}
            loading={Boolean(loading[group.key])}
            error={errors[group.key] || ""}
            onReload={onReload[group.key]}
          />
        ))}
      </div>
    </section>
  );
}
