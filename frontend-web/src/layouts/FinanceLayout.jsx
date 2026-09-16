// frontend-web/src/layouts/FinanceLayout.jsx
import { useState } from "react";
import { Outlet } from "react-router-dom";

import FinanceHeader from "../components/finance/FinanceHeader";
import FinanceSidebar from "../components/finance/FinanceSidebar";
import FinanceFooter from "../components/finance/FinanceFooter";

import "./FinanceLayout.css";

function FinanceLayout() {
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
      <FinanceSidebar
        mobileOpen={mobileOpen}
        onCloseMobile={() => setMobileOpen(false)}
      />

      {/* MAIN AREA */}
      <div className="finance-layout-main">
        {/* HEADER */}
        <FinanceHeader onMenuClick={() => setMobileOpen(!mobileOpen)} />

        {/* CONTENT */}
        <main className="finance-layout-content">
          <Outlet />
        </main>

        {/* FOOTER */}
        <FinanceFooter />
      </div>
    </div>
  );
}

export default FinanceLayout;
