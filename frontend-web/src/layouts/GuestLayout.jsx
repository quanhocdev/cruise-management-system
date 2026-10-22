// src/layouts/GuestLayout.jsx
import { Outlet } from "react-router-dom";
import GuestHeader from "../components/guest/GuestHeader";
import GuestFooter from "../components/guest/GuestFooter";
import PublicTourChatFloatingWidget from "../modules/chat/pages/PublicTourChatFloatingWidget";
import "./GuestLayout.css";

export default function GuestLayout() {
  return (
    <div className="guest-layout-wrapper">
      <GuestHeader />

      <main className="guest-main-content">
        <Outlet />
      </main>

      <GuestFooter />

      <PublicTourChatFloatingWidget />
    </div>
  );
}
