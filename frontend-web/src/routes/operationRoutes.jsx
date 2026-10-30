// src/routes/operationRoutes.jsx

import { Navigate, Route } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import OperationLayout from "../layouts/OperationLayout";

import OperationDashboard from "../modules/operation/pages/Dashboard";
import OperationManagerTour from "../modules/operation/pages/ManagerTour";
import OperationTourConfiguration from "../modules/operation/pages/OperationTourConfiguration";
import ManagerTourPackages from "../modules/operation/pages/ManagerTourPackages";
import OperationTourOpenBooking from "../modules/operation/pages/OperationTourOpenBooking";

const operationRoutes = (
  <Route
    path="/operation"
    element={
      <ProtectedRoute allowedRoles={["OPERATION"]}>
        <OperationLayout />
      </ProtectedRoute>
    }
  >
    <Route path="dashboard" element={<OperationDashboard />} />
    <Route path="tours" element={<OperationManagerTour />} />
    <Route path="tour-configuration" element={<OperationTourConfiguration />} />
    <Route path="tour-packages" element={<ManagerTourPackages />} />
    <Route path="tour-booking-open" element={<OperationTourOpenBooking />} />
    <Route index element={<Navigate to="dashboard" replace />} />
  </Route>
);

export default operationRoutes;
