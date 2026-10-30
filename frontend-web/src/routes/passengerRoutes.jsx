// src/routes/passengerRoutes.jsx

import { Navigate, Route } from "react-router-dom";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import GuestLayout from "../layouts/GuestLayout";

import PassengerBookings from "../modules/passenger/pages/MyBookings";
import PassengerBookingDetail from "../modules/passenger/pages/BookingDetail";
import PassengerBooking from "../modules/passenger/pages/PassengerBooking";
import PaymentResultPage from "../modules/passenger/pages/PaymentResultPage";

const passengerRoutes = (
  <Route
    path="/passenger/*"
    element={
      <ProtectedRoute allowedRoles={["PASSENGER"]}>
        <GuestLayout />
      </ProtectedRoute>
    }
  >
    <Route path="bookings/new" element={<PassengerBooking />} />
    <Route path="bookings" element={<PassengerBookings />} />
    <Route path="bookings/:bookingId" element={<PassengerBookingDetail />} />
    <Route path="payment/result" element={<PaymentResultPage />} />
    <Route path="dashboard" element={<Navigate to="/tours" replace />} />
    {/* Vào /passenger thì về danh sách bookings */}
    <Route path="" element={<Navigate to="bookings" replace />} />
  </Route>
);

export default passengerRoutes;
