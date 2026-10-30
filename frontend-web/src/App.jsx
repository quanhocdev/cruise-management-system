// src/App.jsx
// Chỉ ghép các nhóm route. Route của từng role nằm trong src/routes/.

import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";

import publicRoutes from "./routes/publicRoutes";
import adminRoutes from "./routes/adminRoutes";
import passengerRoutes from "./routes/passengerRoutes";
import schedulerRoutes from "./routes/schedulerRoutes";
import operationRoutes from "./routes/operationRoutes";
import onboardRoutes from "./routes/onboardRoutes";
import shoreRoutes from "./routes/shoreRoutes";
import convenienceRoutes from "./routes/convenienceRoutes";
import financeRoutes from "./routes/financeRoutes";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {publicRoutes}
          {adminRoutes}
          {passengerRoutes}
          {schedulerRoutes}
          {operationRoutes}
          {onboardRoutes}
          {shoreRoutes}
          {convenienceRoutes}
          {financeRoutes}

          {/* Đường dẫn không tồn tại -> về trang chủ */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
