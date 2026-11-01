// src/modules/auth/components/AuthLayout.jsx

import { Anchor, CreditCard, MapPin, Ship } from "lucide-react";

import "../styles/auth.css";

const FEATURES = [
  { icon: Ship, text: "Đặt tour du thuyền nhanh chóng, thanh toán trực tuyến" },
  {
    icon: MapPin,
    text: "Theo dõi lịch trình và các điểm dừng trên hành trình",
  },
  { icon: CreditCard, text: "Check-in và thanh toán trên tàu bằng thẻ NFC" },
];

function CruiseIllustration() {
  return (
    <svg
      className="auth-hero-art"
      viewBox="0 0 480 230"
      role="img"
      aria-label="Du thuyền trên biển"
    >
      <circle cx="396" cy="52" r="26" fill="#fff" fillOpacity="0.16" />

      <g className="auth-ship">
        <rect x="222" y="34" width="28" height="40" rx="4" fill="#38bdf8" />
        <rect x="222" y="34" width="28" height="9" rx="3" fill="#0b1f3a" />
        <rect x="182" y="72" width="108" height="24" rx="5" fill="#fff" />
        <rect x="140" y="96" width="192" height="26" rx="5" fill="#fff" />
        <rect x="108" y="122" width="256" height="28" rx="5" fill="#fff" />
        <path d="M58 150h364l-30 40H90z" fill="#fff" />
        <path d="M70 160h340l-7 11H78z" fill="#2563eb" />

        {[148, 172, 196, 220, 244, 268, 292].map((x) => (
          <circle
            key={x}
            cx={x}
            cy="109"
            r="4"
            fill="#1e3a8a"
            fillOpacity="0.45"
          />
        ))}
        {[130, 158, 186, 214, 242, 270, 298, 326].map((x) => (
          <circle
            key={x}
            cx={x}
            cy="136"
            r="4"
            fill="#1e3a8a"
            fillOpacity="0.45"
          />
        ))}
      </g>

      <path
        d="M0 196q30-14 60 0t60 0t60 0t60 0t60 0t60 0t60 0t60 0V230H0z"
        fill="#fff"
        fillOpacity="0.14"
      />
      <path
        d="M0 210q30-12 60 0t60 0t60 0t60 0t60 0t60 0t60 0t60 0V230H0z"
        fill="#fff"
        fillOpacity="0.1"
      />
    </svg>
  );
}

function AuthLayout({ icon, title, subtitle, children, footer }) {
  return (
    <div className="auth-page">
      <aside className="auth-hero">
        <div className="auth-brand">
          <span className="auth-brand-mark">
            <Anchor size={22} />
          </span>
          <span>CRUISE SYSTEM</span>
        </div>

        <div className="auth-hero-copy">
          <h2>Mỗi hành trình trên biển bắt đầu từ đây</h2>
          <p>
            Quản lý tour du thuyền, lịch trình và trải nghiệm của bạn trong một
            hệ thống duy nhất.
          </p>

          <ul className="auth-features">
            {FEATURES.map(({ icon: Icon, text }) => (
              <li key={text}>
                <span className="auth-features-icon">
                  <Icon size={18} />
                </span>
                {text}
              </li>
            ))}
          </ul>
        </div>

        <CruiseIllustration />
      </aside>

      <main className="auth-panel">
        <div className="auth-card">
          <header className="auth-card-head">
            {icon && <div className="auth-card-icon">{icon}</div>}
            <h1>{title}</h1>
            {subtitle && <p>{subtitle}</p>}
          </header>

          {children}

          {footer && <div className="auth-footer">{footer}</div>}
        </div>
      </main>
    </div>
  );
}

export default AuthLayout;
