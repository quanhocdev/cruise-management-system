// src/routes/financeRoutes.jsx

import { Navigate, Route } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import FinanceLayout from "../layouts/FinanceLayout";

import FinanceDashboard from "../modules/finance/pages/Dashboard";
import FinanceTourSchedule from "../modules/finance/pages/FinanceTourSchedule";
import FinanceRooms from "../modules/finance/pages/FinanceRooms";
import FinanceNfcCards from "../modules/finance/pages/FinanceNfcCards";
import FinanceBookings from "../modules/finance/pages/FinanceBookings";
import FinanceBookingPassengers from "../modules/finance/pages/FinanceBookingPassengers";
import FinanceCheckIn from "../modules/finance/pages/FinanceCheckIn";
import FinanceCheckout from "../modules/finance/pages/FinanceCheckout";

const financeRoutes = (
  <Route
    path="/finance"
    element={
      <ProtectedRoute allowedRoles={["FINANCE"]}>
        <FinanceLayout />
      </ProtectedRoute>
    }
  >
    <Route path="dashboard" element={<FinanceDashboard />} />
    <Route path="check-in" element={<FinanceCheckIn />} />
    <Route path="checkout" element={<FinanceCheckout />} />
    <Route path="tours-schedule" element={<FinanceTourSchedule />} />

    <Route path="bookings" element={<FinanceBookings />} />
    <Route
      path="bookings/:bookingId/passengers"
      element={<FinanceBookingPassengers />}
    />

    <Route path="rooms" element={<FinanceRooms />} />
    <Route path="nfc-cards" element={<FinanceNfcCards />} />

    <Route index element={<Navigate to="dashboard" replace />} />
  </Route>
);

export default financeRoutes;
