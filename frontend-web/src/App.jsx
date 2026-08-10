// src/App.jsx

import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";

import HomePage from "./modules/guest/pages/HomePage";

import RegisterPage from "./modules/auth/pages/RegisterPage";
import LoginPage from "./modules/auth/pages/LoginPage";
import VerifyOtpPage from "./modules/auth/pages/VerifyOtpPage";

import AdminDashboard from "./modules/admin/pages/Dashboard";
import PassengerDashboard from "./modules/passenger/pages/Dashboard";
import SchedulerDashboard from "./modules/scheduler/pages/Dashboard";
import OperationDashboard from "./modules/operation/pages/Dashboard";
import OnboardDashboard from "./modules/onboard/pages/Dashboard";
import ShoreDashboard from "./modules/shore/pages/Dashboard";
import ConvenienceDashboard from "./modules/convenience/pages/Dashboard";
import FinanceDashboard from "./modules/finance/pages/Dashboard";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* GUEST */}
          <Route path="/" element={<HomePage />} />
          {/* AUTH */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/verify-email" element={<VerifyOtpPage />} />
          {/* ADMIN */}
          <Route
            path="/admin/*"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          {/* PASSENGER */}
          <Route
            path="/passenger/*"
            element={
              <ProtectedRoute allowedRoles={["PASSENGER"]}>
                <PassengerDashboard />
              </ProtectedRoute>
            }
          />
          {/* SCHEDULER */}
          <Route
            path="/scheduler/*"
            element={
              <ProtectedRoute allowedRoles={["SCHEDULE"]}>
                <SchedulerDashboard />
              </ProtectedRoute>
            }
          />
          {/* OPERATION */}
          <Route
            path="/operation/*"
            element={
              <ProtectedRoute allowedRoles={["OPERATION"]}>
                <OperationDashboard />
              </ProtectedRoute>
            }
          />
          {/* ONBOARD */}
          <Route
            path="/onboard/*"
            element={
              <ProtectedRoute allowedRoles={["ONBOARD"]}>
                <OnboardDashboard />
              </ProtectedRoute>
            }
          />
          {/* SHORE */}
          <Route
            path="/shore/*"
            element={
              <ProtectedRoute allowedRoles={["SHORE"]}>
                <ShoreDashboard />
              </ProtectedRoute>
            }
          />
          {/* CONVENIENCE */}
          <Route
            path="/convenience/*"
            element={
              <ProtectedRoute allowedRoles={["CONVENIENCE"]}>
                <ConvenienceDashboard />
              </ProtectedRoute>
            }
          />
          {/* FINANCE */}
          <Route
            path="/finance/*"
            element={
              <ProtectedRoute allowedRoles={["FINANCE"]}>
                <FinanceDashboard />
              </ProtectedRoute>
            }
          />
          {/* 404 Not Found */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
