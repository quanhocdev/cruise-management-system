import { formatCurrency } from "./formatters";
import "./PackageSelector.css";

const MAX_BENEFITS_PREVIEW = 3;

export default function PackageSelector({ packages, selectedId, onSelect }) {
  if (!packages?.length) {
    return (
      <p className="package-picker__empty">Tour này chưa có gói nào để đặt.</p>
    );
  }

  return (
    <div
      className="package-picker"
      role="radiogroup"
      aria-label="Chọn gói tour"
    >
      {packages.map((pkg) => {
        const selected = String(pkg.id) === String(selectedId);
        const benefits = pkg.benefits || [];
        const extra = benefits.length - MAX_BENEFITS_PREVIEW;

        return (
          <label
            key={pkg.id}
            className={`package-option ${selected ? "is-selected" : ""}`}
          >
            <input
              type="radio"
              name="tourPackageId"
              className="package-option__input"
              value={pkg.id}
              checked={selected}
              onChange={() => onSelect(pkg.id)}
            />

            <span className="package-option__check" aria-hidden="true">
              ✓
            </span>

            <span className="package-option__name">{pkg.name}</span>

            <span className="package-option__price">
              {formatCurrency(pkg.price)}
              <small> / phòng</small>
            </span>

            {pkg.description && (
              <span className="package-option__desc">{pkg.description}</span>
            )}

            {benefits.length > 0 && (
              <ul className="package-option__benefits">
                {benefits.slice(0, MAX_BENEFITS_PREVIEW).map((b, idx) => (
                  <li key={idx}>
                    {b.type} (x{b.quantity}
                    {b.discountPercent ? `, giảm ${b.discountPercent}%` : ""})
                  </li>
                ))}
                {extra > 0 && (
                  <li className="package-option__more">
                    +{extra} quyền lợi khác
                  </li>
                )}
              </ul>
            )}

            <span className="package-option__capacity">
              Tối đa {pkg.maxPassengers || 2} khách / phòng
            </span>
          </label>
        );
      })}
    </div>
  );
}
