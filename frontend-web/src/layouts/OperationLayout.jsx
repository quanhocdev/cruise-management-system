import { Outlet } from "react-router-dom";

import OperationHeader from "../components/operation/OperationHeader";
import OperationSidebar from "../components/operation/OperationSidebar";
import OperationFooter from "../components/operation/OperationFooter";

import "./OperationLayout.css";

function OperationLayout() {
  return (
    <div className="operation-layout">
      {/* SIDEBAR */}
      <OperationSidebar />

      {/* MAIN AREA */}
      <div className="operation-layout-main">
        {/* HEADER */}
        <OperationHeader />

        {/* CONTENT */}
        <main className="operation-layout-content">
          <Outlet />
        </main>

        {/* FOOTER */}
        <OperationFooter />
      </div>
    </div>
  );
}

export default OperationLayout;
