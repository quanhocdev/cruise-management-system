import React, { useMemo } from "react";
import {
  ShoppingBag,
  ConciergeBell,
  Ticket,
  MapPinned,
  MapPin,
  Clock3,
  Tag,
  Info,
  RefreshCw,
} from "lucide-react";
import { Alert, Badge, Spinner } from "react-bootstrap";

import "./TripFacilitiesSection.css";

function unwrapList(value) {
  if (Array.isArray(value)) return value;
  if (Array.isArray(value?.content)) return value.content;
  if (Array.isArray(value?.items)) return value.items;
  if (Array.isArray(value?.data)) return value.data;
  if (Array.isArray(value?.products)) return value.products;
  if (Array.isArray(value?.services)) return value.services;
  if (Array.isArray(value?.activities)) return value.activities;
  if (Array.isArray(value?.visits)) return value.visits;
  return [];
}

function getResource(resource) {
  if (
    resource &&
    typeof resource === "object" &&
    ("data" in resource ||
      "loading" in resource ||
      "error" in resource ||
      "load" in resource)
  ) {
    return {
      items: unwrapList(resource.data),
      loading: Boolean(resource.loading),
      error: resource.error || "",
      reload: resource.load,
    };
  }

  return {
    items: unwrapList(resource),
    loading: false,
    error: "",
    reload: null,
  };
}

function getItemId(item, index) {
  return (
    item?.id ||
    item?.productId ||
    item?.serviceId ||
    item?.activityId ||
    item?.visitId ||
    item?.tourVisitId ||
    index
  );
}

function getItemName(item) {
  return (
    item?.productName ||
    item?.serviceName ||
    item?.activityName ||
    item?.visitName ||
    item?.destinationName ||
    item?.portName ||
    item?.name ||
    item?.title ||
    "Chưa có tên"
  );
}

function getItemDescription(item) {
  return (
    item?.description ||
    item?.shortDescription ||
    item?.content ||
    item?.note ||
    item?.detail ||
    ""
  );
}

function getItemImage(item) {
  return (
    item?.imageUrl ||
    item?.thumbnailUrl ||
    item?.image ||
    item?.thumbnail ||
    null
  );
}

function getItemLocation(item) {
  return (
    item?.locationName ||
    item?.portName ||
    item?.address ||
    item?.location ||
    null
  );
}

function getItemTime(item) {
  const value =
    item?.startTime ||
    item?.arrivalTime ||
    item?.departureTime ||
    item?.time ||
    item?.duration ||
    null;

  if (!value) return null;

  if (typeof value === "string" && /^\d{2}:\d{2}/.test(value)) {
    return value.slice(0, 5);
  }

  return value;
}

function getItemPrice(item) {
  const value =
    item?.price ?? item?.unitPrice ?? item?.sellingPrice ?? item?.amount;

  if (value == null || value === "") return null;

  const numericValue = Number(value);

  if (!Number.isFinite(numericValue)) return String(value);

  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: item?.currency || "VND",
  }).format(numericValue);
}

function getItemStatus(item) {
  return item?.statusLabel || item?.status || null;
}

function FacilityCard({ item, type, index }) {
  const name = getItemName(item);
  const description = getItemDescription(item);
  const image = getItemImage(item);
  const location = getItemLocation(item);
  const time = getItemTime(item);
  const price =
    type === "product" || type === "service" ? getItemPrice(item) : null;
  const status = getItemStatus(item);

  return (
    <article className="trip-facility-card">
      {image ? (
        <div className="trip-facility-card__image">
          <img src={image} alt={name} loading="lazy" />
        </div>
      ) : (
        <div className="trip-facility-card__image trip-facility-card__image--placeholder">
          {type === "product" && <ShoppingBag size={25} />}
          {type === "service" && <ConciergeBell size={25} />}
          {type === "activity" && <Ticket size={25} />}
          {type === "visit" && <MapPinned size={25} />}
        </div>
      )}

      <div className="trip-facility-card__body">
        <div className="trip-facility-card__heading">
          <h4>{name}</h4>
          {status && (
            <Badge
              bg="light"
              text="dark"
              className="trip-facility-card__status"
            >
              {status}
            </Badge>
          )}
        </div>

        {description && (
          <p className="trip-facility-card__description">{description}</p>
        )}

        <div className="trip-facility-card__meta">
          {location && (
            <span>
              <MapPin size={13} />
              {location}
            </span>
          )}

          {time && (
            <span>
              <Clock3 size={13} />
              {time}
            </span>
          )}

          {price && (
            <span className="trip-facility-card__price">
              <Tag size={13} />
              {price}
            </span>
          )}
        </div>
      </div>
    </article>
  );
}

function FacilityGroup({ title, description, icon: Icon, type, resource }) {
  const { items, loading, error, reload } = resource;

  return (
    <section className="trip-facility-group">
      <div className="trip-facility-group__header">
        <div className="trip-facility-group__icon">
          <Icon size={19} />
        </div>

        <div className="trip-facility-group__heading">
          <h3>{title}</h3>
          <p>{description}</p>
        </div>

        <span className="trip-facility-group__count">{items.length}</span>
      </div>

      {loading ? (
        <div className="trip-facility-group__state">
          <Spinner animation="border" size="sm" variant="primary" />
          <span>Đang tải {title.toLowerCase()}...</span>
        </div>
      ) : error ? (
        <Alert variant="danger" className="trip-facility-group__alert">
          <span>{error}</span>
          {reload && (
            <button type="button" onClick={reload}>
              <RefreshCw size={13} />
              Thử lại
            </button>
          )}
        </Alert>
      ) : items.length === 0 ? (
        <div className="trip-facility-group__empty">
          Chưa có dữ liệu {title.toLowerCase()} cho chuyến đi này.
        </div>
      ) : (
        <div className="trip-facility-group__list">
          {items.map((item, index) => (
            <FacilityCard
              key={getItemId(item, index)}
              item={item}
              type={type}
              index={index}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default function TripFacilitiesSection({
  products,
  services,
  cruiseActivities,
  visits,
}) {
  const resources = useMemo(
    () => ({
      products: getResource(products),
      services: getResource(services),
      activities: getResource(cruiseActivities),
      visits: getResource(visits),
    }),
    [products, services, cruiseActivities, visits],
  );

  return (
    <section className="trip-facilities-section">
      <div className="trip-facilities-section__header">
        <div>
          <div className="trip-facilities-section__eyebrow">
            <Info size={14} />
            KHÁM PHÁ TRÊN HÀNH TRÌNH
          </div>

          <h2 className="trip-facilities-section__title">
            Sản phẩm, dịch vụ và trải nghiệm
          </h2>

          <p className="trip-facilities-section__subtitle">
            Khám phá các tiện ích trên tàu và những điểm tham quan trong chuyến
            đi.
          </p>
        </div>
      </div>

      <div className="trip-facilities-section__groups">
        <FacilityGroup
          title="Sản phẩm trên tàu"
          description="Các sản phẩm hành khách có thể tìm hiểu và sử dụng."
          icon={ShoppingBag}
          type="product"
          resource={resources.products}
        />

        <FacilityGroup
          title="Dịch vụ trên tàu"
          description="Các dịch vụ được cung cấp trong chuyến đi."
          icon={ConciergeBell}
          type="service"
          resource={resources.services}
        />

        <FacilityGroup
          title="Hoạt động trên tàu"
          description="Các hoạt động và trải nghiệm giải trí."
          icon={Ticket}
          type="activity"
          resource={resources.activities}
        />

        <FacilityGroup
          title="Điểm tham quan"
          description="Các địa điểm và điểm dừng tham quan trong hành trình."
          icon={MapPinned}
          type="visit"
          resource={resources.visits}
        />
      </div>
    </section>
  );
}
