// src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Navbar from "./components/Navbar";
import LoginPage from "./pages/LoginPage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />

          {/* 1. Module ADMIN */}
          <Route
            path="/admin/*"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <div>
                  <h1>Trang Quản Trị Hệ Thống (ADMIN)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 2. Module SCHEDULER (Lập lịch chuyến đi) */}
          <Route
            path="/scheduler/*"
            element={
              <ProtectedRoute allowedRoles={["SCHEDULER", "ADMIN"]}>
                <div>
                  <h1>Trang Quản Lý Lịch Trình (SCHEDULER)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 3. Module SHORE (Dịch vụ bờ) */}
          <Route
            path="/shore/*"
            element={
              <ProtectedRoute allowedRoles={["SHORE", "ADMIN"]}>
                <div>
                  <h1>Trang Quản Lý Dịch Vụ Bờ (SHORE)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 4. Module ONBOARD (Dịch vụ trên tàu) */}
          <Route
            path="/onboard/*"
            element={
              <ProtectedRoute allowedRoles={["ONBOARD", "ADMIN"]}>
                <div>
                  <h1>Trang Quản Lý Dịch Vụ Trên Tàu (ONBOARD)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 5. Module CONVENIENCE (Tiện ích / Cửa hàng) */}
          <Route
            path="/convenience/*"
            element={
              <ProtectedRoute allowedRoles={["CONVENIENCE", "ADMIN"]}>
                <div>
                  <h1>Trang Quản Lý Tiện Ích (CONVENIENCE)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 6. Module FINANCE (Tài chính / Doanh thu) */}
          <Route
            path="/finance/*"
            element={
              <ProtectedRoute allowedRoles={["FINANCE", "ADMIN"]}>
                <div>
                  <h1>Trang Báo Cáo Tài Chính (FINANCE)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 7. Module OPERATION (Vận hành kỹ thuật) */}
          <Route
            path="/operation/*"
            element={
              <ProtectedRoute allowedRoles={["OPERATION", "ADMIN"]}>
                <div>
                  <h1>Trang Vận Hành & Kỹ Thuật (OPERATION)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 8. Module PASSENGER (Hành khách) */}
          <Route
            path="/passenger/*"
            element={
              <ProtectedRoute allowedRoles={["PASSENGER", "ADMIN"]}>
                <div>
                  <h1>Trang Dành Cho Hành Khách (PASSENGER)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* 9. Module GUEST (Khách vãng lai / Đặt chỗ) */}
          <Route
            path="/guest/*"
            element={
              <ProtectedRoute allowedRoles={["GUEST", "PASSENGER", "ADMIN"]}>
                <div>
                  <h1>Trang Khám Phá & Đặt Tour (GUEST)</h1>
                </div>
              </ProtectedRoute>
            }
          />

          {/* Redirect mặc định */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
