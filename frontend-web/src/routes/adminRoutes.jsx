// src/routes/adminRoutes.jsx

import { Navigate, Route, Routes } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import AdminLayout from "../layouts/AdminLayout";

import AdminDashboard from "../modules/admin/pages/Dashboard";
import ManagerAccount from "../modules/admin/pages/ManagerAccount";
import ManagerPort from "../modules/admin/pages/ManagerPort";
import ManagerCruise from "../modules/admin/pages/ManagerCruise";
import CruiseDeck from "../modules/admin/pages/CruiseDeck";
import CruiseDeckDetail from "../modules/admin/pages/CruiseDeckDetail";
import CruiseArea from "../modules/admin/pages/CruiseArea";
import CruiseRoom from "../modules/admin/pages/CruiseRoom";
import ManagerRoomType from "../modules/admin/pages/ManagerRoomType";
import ManagerProduct from "../modules/admin/pages/ManagerProduct";
import ManagerService from "../modules/admin/pages/ManagerService";
import ManagerPolicy from "../modules/admin/pages/ManagerPolicy";
import ManagerNfcCard from "../modules/admin/pages/ManagerNfcCard";

const adminRoutes = (
  <Route
    path="/admin/*"
    element={
      <ProtectedRoute allowedRoles={["ADMIN"]}>
        <AdminLayout>
          <Routes>
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="accounts" element={<ManagerAccount />} />
            <Route path="ports" element={<ManagerPort />} />
            <Route path="nfc-cards" element={<ManagerNfcCard />} />

            <Route path="cruises" element={<ManagerCruise />} />
            <Route path="cruises/:cruiseId/decks" element={<CruiseDeck />} />
            <Route path="decks/:deckId" element={<CruiseDeckDetail />} />
            <Route path="decks/:deckId/areas" element={<CruiseArea />} />
            <Route path="decks/:deckId/rooms" element={<CruiseRoom />} />

            <Route path="room-types" element={<ManagerRoomType />} />
            <Route path="products" element={<ManagerProduct />} />
            <Route path="services" element={<ManagerService />} />
            <Route path="policies" element={<ManagerPolicy />} />

            <Route path="" element={<Navigate to="dashboard" replace />} />
          </Routes>
        </AdminLayout>
      </ProtectedRoute>
    }
  />
);

export default adminRoutes;
