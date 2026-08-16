import { Outlet } from "react-router-dom";
import ConvenienceHeader from "../components/convenience/ConvenienceHeader";
import ConvenienceSidebar from "../components/convenience/ConvenienceSidebar";
import ConvenienceFooter from "../components/convenience/ConvenienceFooter";
import "./ConvenienceLayout.css";

function ConvenienceLayout() {
  return (
    <div className="convenience-layout">
      <ConvenienceSidebar />
      <div className="convenience-layout-main">
        <ConvenienceHeader />
        <main className="convenience-layout-content">
          <Outlet />
        </main>
        <ConvenienceFooter />
      </div>
    </div>
  );
}

export default ConvenienceLayout;
