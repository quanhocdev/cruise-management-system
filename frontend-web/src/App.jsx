// src/App.jsx

import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Navbar from "./components/Navbar";

import HomePage from "./modules/guest/pages/HomePage";
import RegisterPage from "./modules/auth/pages/RegisterPage";
import LoginPage from "./modules/auth/pages/LoginPage";

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
                <h1>Trang Passenger</h1>
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
