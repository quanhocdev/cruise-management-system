// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Navbar from "./components/Navbar";

import HomePage from "./modules/guest/pages/HomePage";
import RegisterPage from "./modules/auth/pages/RegisterPage";
import LoginPage from "./modules/auth/pages/LoginPage";
import VerifyOtpPage from "./modules/auth/pages/VerifyOtpPage";

// 1. Import trang Dashboard của Passenger (chỉnh đường dẫn file cho đúng dự án của bạn)
import PassengerDashboard from "./modules/passenger/pages/Dashboard";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />

        <Routes>
          {/* Guest Public */}
          <Route path="/" element={<HomePage />} />

          {/* Auth */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/verify-email" element={<VerifyOtpPage />} />

          {/* Admin */}
          <Route
            path="/admin/*"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <h1>Trang Admin</h1>
              </ProtectedRoute>
            }
          />

          {/* Passenger */}
          <Route
            path="/passenger/*"
            element={
              <ProtectedRoute allowedRoles={["PASSENGER", "ADMIN"]}>
                <Routes>
                  {/* Khớp với URL /passenger/my-cruise */}
                  <Route path="dashboard" element={<PassengerDashboard />} />

                  {/* Nếu chỉ vào /passenger thì tự động chuyển hướng qua /passenger/my-cruise */}
                  <Route
                    path=""
                    element={<Navigate to="my-cruise" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* Không tìm thấy */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
