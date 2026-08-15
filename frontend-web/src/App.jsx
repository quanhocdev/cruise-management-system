// src/App.jsx

import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/auth/ProtectedRoute";

import HomePage from "./modules/guest/pages/HomePage";

import RegisterPage from "./modules/auth/pages/RegisterPage";
import LoginPage from "./modules/auth/pages/LoginPage";
import VerifyOtpPage from "./modules/auth/pages/VerifyOtpPage";
import ActivatePage from "./modules/auth/pages/ActivatePage";

// admin
import AdminLayout from "./layouts/AdminLayout";
import AdminDashboard from "./modules/admin/pages/Dashboard";
import ManagerAccount from "./modules/admin/pages/ManagerAccount";
import ManagerPort from "./modules/admin/pages/ManagerPort";

import ManagerCruise from "./modules/admin/pages/ManagerCruise";
import CruiseDeck from "./modules/admin/pages/CruiseDeck";
import CruiseDeckDetail from "./modules/admin/pages/CruiseDeckDetail";
import CruiseArea from "./modules/admin/pages/CruiseArea";
import CruiseRoom from "./modules/admin/pages/CruiseRoom";
import ManagerRoomType from "./modules/admin/pages/ManagerRoomType";

import ManagerProduct from "./modules/admin/pages/ManagerProduct";
import ManagerService from "./modules/admin/pages/ManagerService";
import ManagerPolicy from "./modules/admin/pages/ManagerPolicy";

import PassengerDashboard from "./modules/passenger/pages/Dashboard";

import SchedulerLayout from "./layouts/SchedulerLayout";
import SchedulerDashboard from "./modules/scheduler/pages/Dashboard";
import ManagerTour from "./modules/scheduler/pages/ManagerTour";
import ManagerSchedule from "./modules/scheduler/pages/ManagerSchedule";
import ManagerScheduleStops from "./modules/scheduler/pages/ManagerScheduleStops";

import OperationDashboard from "./modules/operation/pages/Dashboard";
import OnboardDashboard from "./modules/onboard/pages/Dashboard";
import ShoreDashboard from "./modules/shore/pages/Dashboard";
import ConvenienceDashboard from "./modules/convenience/pages/Dashboard";
import FinanceDashboard from "./modules/finance/pages/Dashboard";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* =====================================================
              ACTIVATION
             ===================================================== */}
          <Route path="/activate" element={<ActivatePage />} />

          {/* =====================================================
              GUEST
             ===================================================== */}
          <Route path="/" element={<HomePage />} />

          {/* =====================================================
              AUTH
             ===================================================== */}
          <Route path="/login" element={<LoginPage />} />

          <Route path="/register" element={<RegisterPage />} />

          <Route path="/verify-email" element={<VerifyOtpPage />} />

          {/* =====================================================
              ADMIN
             ===================================================== */}
          <Route
            path="/admin/*"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <AdminLayout>
                  <Routes>
                    {/* DASHBOARD */}
                    <Route path="dashboard" element={<AdminDashboard />} />

                    {/* ACCOUNT */}
                    <Route path="accounts" element={<ManagerAccount />} />

                    {/* PORT */}
                    <Route path="ports" element={<ManagerPort />} />

                    {/* =================================================
                      CRUISE
                     ================================================= */}

                    {/* Quản lý du thuyền */}
                    <Route path="cruises" element={<ManagerCruise />} />

                    {/* Quản lý tầng của du thuyền */}
                    <Route
                      path="cruises/:cruiseId/decks"
                      element={<CruiseDeck />}
                    />
                    <Route
                      path="decks/:deckId"
                      element={<CruiseDeckDetail />}
                    />

                    {/* Quản lý khu vực của tầng */}
                    <Route
                      path="decks/:deckId/areas"
                      element={<CruiseArea />}
                    />
                    <Route path="room-types" element={<ManagerRoomType />} />
                    {/* Quản lý phòng của tầng */}
                    <Route
                      path="decks/:deckId/rooms"
                      element={<CruiseRoom />}
                    />

                    <Route path="products" element={<ManagerProduct />} />
                    <Route path="services" element={<ManagerService />} />
                    <Route path="policies" element={<ManagerPolicy />} />
                    {/* DEFAULT */}
                    <Route
                      path=""
                      element={<Navigate to="dashboard" replace />}
                    />
                  </Routes>
                </AdminLayout>
                //{" "}
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              PASSENGER
             ===================================================== */}
          <Route
            path="/passenger/*"
            element={
              <ProtectedRoute allowedRoles={["PASSENGER"]}>
                <Routes>
                  <Route path="dashboard" element={<PassengerDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              SCHEDULER
             ===================================================== */}
          <Route
            path="/scheduler/*"
            element={
              <ProtectedRoute allowedRoles={["SCHEDULER"]}>
                <SchedulerLayout>
                  <Routes>
                    {/* DASHBOARD */}
                    <Route path="dashboard" element={<SchedulerDashboard />} />
                    {/* TOUR */}
                    <Route path="tours" element={<ManagerTour />} />

                    <Route
                      path="tours/:tourId/schedules"
                      element={<ManagerSchedule />}
                    />
                    <Route
                      path="tours/:tourId/schedules/:scheduleId/stops"
                      element={<ManagerScheduleStops />}
                    />
                    {/* TOUR DETAIL */}
                    {/* DEFAULT */}
                    <Route
                      path=""
                      element={<Navigate to="dashboard" replace />}
                    />
                  </Routes>
                </SchedulerLayout>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              OPERATION
             ===================================================== */}
          <Route
            path="/operation/*"
            element={
              <ProtectedRoute allowedRoles={["OPERATION"]}>
                <Routes>
                  <Route path="dashboard" element={<OperationDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              ONBOARD
             ===================================================== */}
          <Route
            path="/onboard/*"
            element={
              <ProtectedRoute allowedRoles={["ONBOARD"]}>
                <Routes>
                  <Route path="dashboard" element={<OnboardDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              SHORE
             ===================================================== */}
          <Route
            path="/shore/*"
            element={
              <ProtectedRoute allowedRoles={["SHORE"]}>
                <Routes>
                  <Route path="dashboard" element={<ShoreDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              CONVENIENCE
             ===================================================== */}
          <Route
            path="/convenience/*"
            element={
              <ProtectedRoute allowedRoles={["CONVENIENCE"]}>
                <Routes>
                  <Route path="dashboard" element={<ConvenienceDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              FINANCE
             ===================================================== */}
          <Route
            path="/finance/*"
            element={
              <ProtectedRoute allowedRoles={["FINANCE"]}>
                <Routes>
                  <Route path="dashboard" element={<FinanceDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              404
             ===================================================== */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
