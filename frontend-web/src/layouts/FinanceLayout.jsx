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
    <div className="finance-layout">
      {mobileOpen && (
        <div
          className="finance-sidebar-overlay"
          onClick={() => setMobileOpen(false)}
        />
      )}

      <FinanceSidebar
        mobileOpen={mobileOpen}
        onCloseMobile={() => setMobileOpen(false)}
      />

      <div className="finance-layout-main">
        <FinanceHeader onMenuClick={() => setMobileOpen((prev) => !prev)} />

        <main className="finance-layout-content">
          <Outlet />
        </main>

        <FinanceFooter />
      </div>
    </div>
  );
}

export default FinanceLayout;
