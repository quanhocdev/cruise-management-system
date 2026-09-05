// src/layouts/GuestLayout.jsx
import { Outlet } from "react-router-dom";
import GuestHeader from "../components/guest/GuestHeader";
import GuestFooter from "../components/guest/GuestFooter";
import "./GuestLayout.css";

export default function GuestLayout() {
  return (
    <div className="guest-layout-wrapper">
      <GuestHeader />
      <main className="guest-main-content">
        <Outlet />
      </main>
      <GuestFooter />
    </div>
  );
}
