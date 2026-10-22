// ManagementLayout.jsx
import { useState } from "react";
import { Outlet, useLocation } from "react-router-dom";
import ManagementSidebar from "./ManagementSidebar";
import ManagementHeader from "./ManagementHeader";
import ManagementFooter from "./ManagementFooter";
import { isPathActive } from "./utils";
import "../../styles/management/themes.css";
import "../../styles/management/layout.css";

function ManagementLayout({ config, notificationProps, children }) {
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const { pathname } = useLocation();

  const pageTitle = config.header.titleFromRoute
    ? config.menu
        .flatMap((section) => section.items)
        .find((item) => isPathActive(pathname, item))?.label
    : undefined;

  return (
    <div className="mgmt-layout" data-theme={config.theme}>
      {mobileOpen && (
        <div className="mgmt-backdrop" onClick={() => setMobileOpen(false)} />
      )}

      <ManagementSidebar
        config={config}
        collapsed={collapsed && !mobileOpen}
        onToggle={() => setCollapsed((v) => !v)}
        mobileOpen={mobileOpen}
        onClose={() => setMobileOpen(false)}
      />

      <div className="mgmt-layout-main">
        <ManagementHeader
          config={config}
          pageTitle={pageTitle}
          onMenuClick={() => setMobileOpen((v) => !v)}
          notificationProps={notificationProps}
        />
        <main className="mgmt-layout-content">{children ?? <Outlet />}</main>
        <ManagementFooter
          rightText={config.footerRight}
          links={config.footerLinks}
        />
      </div>
    </div>
  );
}

export default ManagementLayout;
