import "./BookingSection.css";

export default function BookingSection({
  step,
  title,
  hint,
  action,
  children,
}) {
  return (
    <section className="booking-section">
      <header className="booking-section__head">
        <span className="booking-section__step" aria-hidden="true">
          {step}
        </span>
        <div className="booking-section__titles">
          <h2 className="booking-section__title">{title}</h2>
          {hint && <p className="booking-section__hint">{hint}</p>}
        </div>
        {action && <div className="booking-section__action">{action}</div>}
      </header>
      {children}
    </section>
  );
}
