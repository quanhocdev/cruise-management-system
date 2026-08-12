// src/App.jsx

import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";

import HomePage from "./modules/guest/pages/HomePage";

import RegisterPage from "./modules/auth/pages/RegisterPage";
import LoginPage from "./modules/auth/pages/LoginPage";
import VerifyOtpPage from "./modules/auth/pages/VerifyOtpPage";
import ActivatePage from "./modules/auth/pages/ActivatePage";

// admin
import AdminDashboard from "./modules/admin/pages/Dashboard";
import ManagerAccount from "./modules/admin/pages/ManagerAccount";
import ManagerPort from "./modules/admin/pages/ManagerPort";

import PassengerDashboard from "./modules/passenger/pages/Dashboard";
import SchedulerDashboard from "./modules/scheduler/pages/Dashboard";
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
              Staff chưa đăng nhập vẫn được truy cập
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
                <Routes>
                  <Route path="dashboard" element={<AdminDashboard />} />

                  <Route path="accounts" element={<ManagerAccount />} />
                  <Route path="ports" element={<ManagerPort />} />
                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
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
                <Routes>
                  <Route path="dashboard" element={<SchedulerDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
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
