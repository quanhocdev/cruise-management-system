import { Outlet } from "react-router-dom";

import ShoreHeader from "../components/shore/ShoreHeader";
import ShoreSidebar from "../components/shore/ShoreSidebar";
import ShoreFooter from "../components/shore/ShoreFooter";

import "./ShoreLayout.css";

function ShoreLayout() {
  return (
    <div className="shore-layout">
      {/* SIDEBAR */}
      <ShoreSidebar />

      {/* MAIN AREA */}
      <div className="shore-layout-main">
        {/* HEADER */}
        <ShoreHeader />

        {/* CONTENT */}
        <main className="shore-layout-content">
          <Outlet />
        </main>

        {/* FOOTER */}
        <ShoreFooter />
      </div>
    </div>
  );
}

export default ShoreLayout;
