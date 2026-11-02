// src/routes/shoreRoutes.jsx

import { Navigate, Route } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import ShoreLayout from "../layouts/ShoreLayout";

import ShoreDashboard from "../modules/shore/pages/Dashboard";
import ActivityVisitTour from "../modules/shore/pages/ActivityVisitTour";
import ActivityVisitUsage from "../modules/shore/pages/ActivityVisitUsage";

const shoreRoutes = (
  <Route
    path="/shore"
    element={
      <ProtectedRoute allowedRoles={["SHORE"]}>
        <ShoreLayout />
      </ProtectedRoute>
    }
  >
    <Route path="dashboard" element={<ShoreDashboard />} />
    <Route path="tours" element={<ActivityVisitTour />} />
    <Route path="activity-visit-usages" element={<ActivityVisitUsage />} />
    <Route index element={<Navigate to="dashboard" replace />} />
  </Route>
);

export default shoreRoutes;
