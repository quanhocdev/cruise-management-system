// ManagementLayout.jsx
import { useState } from "react";
import { Outlet } from "react-router-dom";
import ManagementSidebar from "./ManagementSidebar";
import ManagementHeader from "./ManagementHeader";
import ManagementFooter from "./ManagementFooter";
import "../../styles/management/layout.css";
import "../../styles/management/themes.css";

function ManagementLayout({ config, notificationProps }) {
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="mgmt-layout" data-theme={config.theme}>
      <ManagementSidebar
        config={config}
        collapsed={collapsed}
        onToggle={() => setCollapsed((v) => !v)}
        mobileOpen={mobileOpen}
        onClose={() => setMobileOpen(false)}
      />

      <div className="mgmt-layout-main">
        <ManagementHeader
          config={config}
          onMenuClick={() => setMobileOpen((v) => !v)}
          notificationProps={notificationProps}
        />
        <main className="mgmt-layout-content">
          <Outlet />
        </main>
        <ManagementFooter rightText={config.footerRight} />
      </div>
    </div>
  );
}

export default ManagementLayout;
