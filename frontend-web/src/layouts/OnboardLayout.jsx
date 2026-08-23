// frontend-web/src/layouts/OnboardLayout.jsx
import { useState } from "react";
import { Outlet } from "react-router-dom";

import OnboardHeader from "../components/onboard/OnboardHeader";
import OnboardSidebar from "../components/onboard/OnboardSidebar";
import OnboardFooter from "../components/onboard/OnboardFooter";

import "./OnboardLayout.css";

function OnboardLayout() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="onboard-layout">
      {/* Overlay lớp phủ khi mở menu mobile */}
      {mobileOpen && (
        <div
          className="onboard-sidebar-overlay"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* SIDEBAR */}
      <OnboardSidebar
        mobileOpen={mobileOpen}
        onCloseMobile={() => setMobileOpen(false)}
      />

      {/* MAIN AREA */}
      <div className="onboard-layout-main">
        {/* HEADER */}
        <OnboardHeader onMenuClick={() => setMobileOpen(!mobileOpen)} />

        {/* CONTENT */}
        <main className="onboard-layout-content">
          <Outlet />
        </main>

        {/* FOOTER */}
        <OnboardFooter />
      </div>
    </div>
  );
}

export default OnboardLayout;
