// src/routes/schedulerRoutes.jsx

import { Navigate, Route, Routes } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import SchedulerLayout from "../layouts/SchedulerLayout";

import SchedulerDashboard from "../modules/scheduler/pages/Dashboard";
import ManagerTour from "../modules/scheduler/pages/ManagerTour";
import TourRoutePlanner from "../modules/scheduler/pages/TourRoutePlanner";
import ManagerSchedule from "../modules/scheduler/pages/ManagerSchedule";
import ManagerScheduleStops from "../modules/scheduler/pages/ManagerScheduleStops";
import VesselMonitoringPage from "../modules/scheduler/pages/VesselMonitoringPage";

const schedulerRoutes = (
  <Route
    path="/scheduler/*"
    element={
      <ProtectedRoute allowedRoles={["SCHEDULER"]}>
        <SchedulerLayout>
          <Routes>
            <Route path="dashboard" element={<SchedulerDashboard />} />
            <Route path="tours" element={<ManagerTour />} />
            <Route path="tours/new" element={<TourRoutePlanner />} />
            <Route
              path="tours/:tourId/schedules"
              element={<ManagerSchedule />}
            />
            <Route
              path="vessels/monitoring"
              element={<VesselMonitoringPage />}
            />
            <Route
              path="tours/:tourId/schedules/:scheduleId/stops"
              element={<ManagerScheduleStops />}
            />
            <Route path="" element={<Navigate to="dashboard" replace />} />
          </Routes>
        </SchedulerLayout>
      </ProtectedRoute>
    }
  />
);

export default schedulerRoutes;
