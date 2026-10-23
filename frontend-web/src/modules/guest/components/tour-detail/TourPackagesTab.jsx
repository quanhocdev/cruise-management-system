import { formatCurrency } from "./formatters";
import "./TourPackagesTab.css";

export default function TourPackagesTab({ packages }) {
  if (!packages?.length) {
    return (
      <p className="tab-empty">Chưa có gói tour nào cho hành trình này.</p>
    );
  }

  return (
    <div className="packages">
      {packages.map((pkg) => (
        <article key={pkg.id} className="package">
          <div className="package__head">
            <h3 className="package__name">{pkg.name}</h3>
            {pkg.description && (
              <p className="package__desc">{pkg.description}</p>
            )}
          </div>

          <div className="package__benefits">
            <h4 className="package__subtitle">Quyền lợi bao gồm</h4>
            {pkg.benefits?.length ? (
              <ul>
                {pkg.benefits.map((b, idx) => (
                  <li key={idx}>
                    <span>{b.type}</span>
                    <span className="package__qty">
                      x{b.quantity}
                      {b.discountPercent ? ` · giảm ${b.discountPercent}%` : ""}
                    </span>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="package__none">Không có quyền lợi đi kèm.</p>
            )}
          </div>

          <div className="package__price">{formatCurrency(pkg.price)}</div>
        </article>
      ))}
    </div>
  );
}
