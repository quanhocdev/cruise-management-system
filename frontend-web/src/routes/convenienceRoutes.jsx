// src/routes/convenienceRoutes.jsx

import { Navigate, Route } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import ConvenienceLayout from "../layouts/ConvenienceLayout";

import ConvenienceDashboard from "../modules/convenience/pages/Dashboard";
import ConvenienceProducts from "../modules/convenience/pages/ConvenienceProducts";
import ConvenienceServices from "../modules/convenience/pages/ConvenienceServices";
import ConvenienceTourConfigPage from "../modules/convenience/pages/ConvenienceTourConfigPage";
import ProductUsageHistory from "../modules/convenience/pages/ProductUsageHistory";
import ServiceUsageHistory from "../modules/convenience/pages/ServiceUsageHistory";

const convenienceRoutes = (
  <Route
    path="/convenience"
    element={
      <ProtectedRoute allowedRoles={["CONVENIENCE"]}>
        <ConvenienceLayout />
      </ProtectedRoute>
    }
  >
    <Route path="dashboard" element={<ConvenienceDashboard />} />
    <Route path="products" element={<ConvenienceProducts />} />
    <Route path="services" element={<ConvenienceServices />} />
    <Route path="tour-config" element={<ConvenienceTourConfigPage />} />
    <Route path="product-usage-history" element={<ProductUsageHistory />} />
    <Route path="service-usage-history" element={<ServiceUsageHistory />} />
    <Route index element={<Navigate to="dashboard" replace />} />
  </Route>
);

export default convenienceRoutes;
