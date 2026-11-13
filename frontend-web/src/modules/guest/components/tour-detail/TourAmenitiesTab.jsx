import { formatCurrency } from "./formatters";
import "./TourAmenitiesTab.css";

function getImageUrl(imageUrl) {
  return imageUrl || null;
}

function ProductCard({ product }) {
  return (
    <article className="amenity-card">
      {getImageUrl(product.imageUrl) && (
        <div className="amenity-card__image">
          <img
            src={product.imageUrl}
            alt={product.productName || "Sản phẩm"}
            loading="lazy"
          />
        </div>
      )}

      <div className="amenity-card__body">
        <span className="amenity-card__type">Sản phẩm</span>

        <h3 className="amenity-card__name">
          {product.productName || "Sản phẩm chưa có tên"}
        </h3>

        <p className="amenity-card__desc">
          {product.productDescription || "Chưa có mô tả."}
        </p>

        <div className="amenity-card__footer">
          <span className="amenity-card__price">
            {product.price != null ? formatCurrency(product.price) : "Liên hệ"}
          </span>

          <span className="amenity-card__quantity">
            Số lượng: {product.quantity ?? 0}
          </span>
        </div>
      </div>
    </article>
  );
}

function ServiceCard({ service }) {
  return (
    <article className="amenity-card">
      {getImageUrl(service.imageUrl) && (
        <div className="amenity-card__image">
          <img
            src={service.imageUrl}
            alt={service.serviceName || "Dịch vụ"}
            loading="lazy"
          />
        </div>
      )}

      <div className="amenity-card__body">
        <span className="amenity-card__type amenity-card__type--service">
          Dịch vụ
        </span>

        <h3 className="amenity-card__name">
          {service.serviceName || "Dịch vụ chưa có tên"}
        </h3>

        <p className="amenity-card__desc">
          {service.serviceDescription || "Chưa có mô tả."}
        </p>

        <div className="amenity-card__footer">
          <span className="amenity-card__price">
            {service.price != null ? formatCurrency(service.price) : "Liên hệ"}
          </span>

          <div className="amenity-card__metadata">
            {service.maxPassengers != null && (
              <span>Tối đa {service.maxPassengers} khách</span>
            )}

            {service.durationMinutes != null && (
              <span>{service.durationMinutes} phút</span>
            )}
          </div>
        </div>
      </div>
    </article>
  );
}

export default function TourAmenitiesTab({ products = [], services = [] }) {
  const hasProducts = products.length > 0;
  const hasServices = services.length > 0;

  if (!hasProducts && !hasServices) {
    return (
      <p className="tab-empty">
        Tour hiện chưa có sản phẩm hoặc dịch vụ tiện ích.
      </p>
    );
  }

  return (
    <div className="amenities">
      {hasProducts && (
        <section className="amenities__section">
          <div className="amenities__heading">
            <h3>Sản phẩm đi kèm</h3>
            <span>{products.length} sản phẩm</span>
          </div>

          <div className="amenities__grid">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        </section>
      )}

      {hasServices && (
        <section className="amenities__section">
          <div className="amenities__heading">
            <h3>Dịch vụ tiện ích</h3>
            <span>{services.length} dịch vụ</span>
          </div>

          <div className="amenities__grid">
            {services.map((service) => (
              <ServiceCard key={service.id} service={service} />
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
