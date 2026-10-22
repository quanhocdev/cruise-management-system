import "./FilterChips.css";

export default function FilterChips({ options, value, onChange, label }) {
  return (
    <div className="filter-chips" role="group" aria-label={label}>
      {options.map((opt) => {
        const active = opt.value === value;
        return (
          <button
            key={opt.value}
            type="button"
            className={`filter-chip ${active ? "is-active" : ""}`}
            aria-pressed={active}
            onClick={() => onChange(opt.value)}
          >
            {opt.label}
            {opt.count != null && (
              <span className="filter-chip__count">{opt.count}</span>
            )}
          </button>
        );
      })}
    </div>
  );
}
