// src/routes/onboardRoutes.jsx

import { Navigate, Route } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import OnboardLayout from "../layouts/OnboardLayout";

import OnboardDashboard from "../modules/onboard/pages/Dashboard";
import OnboardActivityCruiseTour from "../modules/onboard/pages/ActivityCruiseTour";
import ActivityCruise from "../modules/onboard/pages/ActivityCruise";
import ActivityCruiseUsage from "../modules/onboard/pages/ActivityCruiseUsage";
import OnboardTourConfigPage from "../modules/onboard/pages/OnboardTourConfigPage";

// Các trang chưa làm: dùng chung một placeholder
const comingSoon = (title) => <div>Trang {title} (Đang phát triển)</div>;

const onboardRoutes = (
  <Route
    path="/onboard"
    element={
      <ProtectedRoute allowedRoles={["ONBOARD"]}>
        <OnboardLayout />
      </ProtectedRoute>
    }
  >
    <Route path="dashboard" element={<OnboardDashboard />} />
    <Route path="activities-catalog" element={<ActivityCruise />} />
    <Route path="tour-config" element={<OnboardTourConfigPage />} />
    <Route path="activity-cruise" element={<OnboardActivityCruiseTour />} />
    <Route path="activity-cruise-usages" element={<ActivityCruiseUsage />} />

    <Route path="schedules" element={comingSoon("Lịch trình")} />
    <Route path="cruises" element={comingSoon("Du thuyền")} />
    <Route path="bookings" element={comingSoon("Booking")} />
    <Route path="settings" element={comingSoon("Cài đặt")} />

    <Route index element={<Navigate to="dashboard" replace />} />
  </Route>
);

export default onboardRoutes;
