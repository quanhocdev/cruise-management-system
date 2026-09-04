import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/auth/ProtectedRoute";

import HomePage from "./modules/guest/pages/HomePage";

import RegisterPage from "./modules/auth/pages/RegisterPage";
import LoginPage from "./modules/auth/pages/LoginPage";
import VerifyOtpPage from "./modules/auth/pages/VerifyOtpPage";
import ActivatePage from "./modules/auth/pages/ActivatePage";
import PaymentResultPage from "./modules/payment/pages/PaymentResultPage";

// Admin imports
import AdminLayout from "./layouts/AdminLayout";
import AdminDashboard from "./modules/admin/pages/Dashboard";
import ManagerAccount from "./modules/admin/pages/ManagerAccount";
import ManagerPort from "./modules/admin/pages/ManagerPort";
import ManagerCruise from "./modules/admin/pages/ManagerCruise";
import CruiseDeck from "./modules/admin/pages/CruiseDeck";
import CruiseDeckDetail from "./modules/admin/pages/CruiseDeckDetail";
import CruiseArea from "./modules/admin/pages/CruiseArea";
import CruiseRoom from "./modules/admin/pages/CruiseRoom";
import ManagerRoomType from "./modules/admin/pages/ManagerRoomType";
import ManagerProduct from "./modules/admin/pages/ManagerProduct";
import ManagerService from "./modules/admin/pages/ManagerService";
import ManagerPolicy from "./modules/admin/pages/ManagerPolicy";
import ManagerPos from "./modules/admin/pages/ManagerPos";

// Passenger imports
import PassengerDashboard from "./modules/passenger/pages/Dashboard";
import PassengerTourDetail from "./modules/passenger/pages/TourDetail";
import CreatePassengerBooking from "./modules/passenger/pages/CreateBooking";
import PassengerBookings from "./modules/passenger/pages/MyBookings";
import PassengerBookingDetail from "./modules/passenger/pages/BookingDetail";

// Scheduler imports
import SchedulerLayout from "./layouts/SchedulerLayout";
import SchedulerDashboard from "./modules/scheduler/pages/Dashboard";
import ManagerTour from "./modules/scheduler/pages/ManagerTour";
import ManagerSchedule from "./modules/scheduler/pages/ManagerSchedule";
import ManagerScheduleStops from "./modules/scheduler/pages/ManagerScheduleStops";

// Operation imports
import OperationLayout from "./layouts/OperationLayout";
import OperationDashboard from "./modules/operation/pages/Dashboard";
import OperationManagerTour from "./modules/operation/pages/ManagerTour";
import OperationTourConfiguration from "./modules/operation/pages/OperationTourConfiguration";

// Convenience imports
import ConvenienceLayout from "./layouts/ConvenienceLayout";
import ConvenienceDashboard from "./modules/convenience/pages/Dashboard";
import ConvenienceProducts from "./modules/convenience/pages/ConvenienceProducts";
import ConvenienceServices from "./modules/convenience/pages/ConvenienceServices";
import ConvenienceTourConfigPage from "./modules/convenience/pages/ConvenienceTourConfigPage";
import ConvenienceTourHistory from "./modules/convenience/pages/ConvenienceTourHistory";

// Onboard imports
import OnboardLayout from "./layouts/OnboardLayout";
import OnboardDashboard from "./modules/onboard/pages/Dashboard";
import OnboardActivityCruiseTour from "./modules/onboard/pages/ActivityCruiseTour";
import ActivityCruise from "./modules/onboard/pages/ActivityCruise";
import ActivityCruiseTourHistory from "./modules/onboard/pages/ActivityCruiseTourHistory";

// Shore imports
import ShoreLayout from "./layouts/ShoreLayout";
import ShoreDashboard from "./modules/shore/pages/Dashboard";
import ActivityVisitTour from "./modules/shore/pages/ActivityVisitTour";
import ActivityVisitTourHistory from "./modules/shore/pages/ActivityVisitTourHistory";
import VisitTourConfiguration from "./modules/shore/pages/VisitTourConfiguration";

// Finance imports
import FinanceDashboard from "./modules/finance/pages/Dashboard";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* =====================================================
              PUBLIC ROUTES
              ===================================================== */}

          <Route path="/" element={<HomePage />} />
          <Route path="/activate" element={<ActivatePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/verify-email" element={<VerifyOtpPage />} />
          <Route path="/payment/result" element={<PaymentResultPage />} />

          {/* =====================================================
              ADMIN ROUTES
              ===================================================== */}

          <Route
            path="/admin/*"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <AdminLayout>
                  <Routes>
                    <Route path="dashboard" element={<AdminDashboard />} />
                    <Route path="accounts" element={<ManagerAccount />} />
                    <Route path="ports" element={<ManagerPort />} />
                    <Route path="cruises" element={<ManagerCruise />} />

                    <Route
                      path="cruises/:cruiseId/decks"
                      element={<CruiseDeck />}
                    />

                    <Route
                      path="decks/:deckId"
                      element={<CruiseDeckDetail />}
                    />

                    <Route
                      path="decks/:deckId/areas"
                      element={<CruiseArea />}
                    />

                    <Route
                      path="decks/:deckId/rooms"
                      element={<CruiseRoom />}
                    />

                    <Route path="room-types" element={<ManagerRoomType />} />

                    <Route path="products" element={<ManagerProduct />} />

                    <Route path="services" element={<ManagerService />} />

                    <Route path="policies" element={<ManagerPolicy />} />
                    <Route path="pos-terminals" element={<ManagerPos />} />

                    <Route
                      path=""
                      element={<Navigate to="dashboard" replace />}
                    />
                  </Routes>
                </AdminLayout>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              PASSENGER ROUTES
              ===================================================== */}

          <Route
            path="/passenger/*"
            element={
              <ProtectedRoute allowedRoles={["PASSENGER"]}>
                <Routes>
                  <Route path="dashboard" element={<PassengerDashboard />} />
                  <Route path="tours/:tourId" element={<PassengerTourDetail />} />
                  <Route path="bookings/new" element={<CreatePassengerBooking />} />
                  <Route path="bookings" element={<PassengerBookings />} />
                  <Route path="bookings/:bookingId" element={<PassengerBookingDetail />} />
                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              SCHEDULER ROUTES
              ===================================================== */}

          <Route
            path="/scheduler/*"
            element={
              <ProtectedRoute allowedRoles={["SCHEDULER"]}>
                <SchedulerLayout>
                  <Routes>
                    <Route path="dashboard" element={<SchedulerDashboard />} />

                    <Route path="tours" element={<ManagerTour />} />

                    <Route
                      path="tours/:tourId/schedules"
                      element={<ManagerSchedule />}
                    />

                    <Route
                      path="tours/:tourId/schedules/:scheduleId/stops"
                      element={<ManagerScheduleStops />}
                    />

                    <Route
                      path=""
                      element={<Navigate to="dashboard" replace />}
                    />
                  </Routes>
                </SchedulerLayout>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              OPERATION ROUTES
              ===================================================== */}

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

            <Route
              path="tour-configuration"
              element={<OperationTourConfiguration />}
            />

            <Route index element={<Navigate to="dashboard" replace />} />
          </Route>

          {/* =====================================================
              ONBOARD ROUTES
              ===================================================== */}

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

            <Route
              path="activity-cruise"
              element={<OnboardActivityCruiseTour />}
            />

            <Route
              path="activity-cruise-history"
              element={<ActivityCruiseTourHistory />}
            />

            <Route
              path="schedules"
              element={<div>Trang Lịch trình (Đang phát triển)</div>}
            />

            <Route
              path="cruises"
              element={<div>Trang Du thuyền (Đang phát triển)</div>}
            />

            <Route
              path="bookings"
              element={<div>Trang Booking (Đang phát triển)</div>}
            />

            <Route
              path="settings"
              element={<div>Trang Cài đặt (Đang phát triển)</div>}
            />

            <Route index element={<Navigate to="dashboard" replace />} />
          </Route>

          {/* =====================================================
              SHORE ROUTES
              ===================================================== */}

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
            <Route
              path="visit-tour-configuration"
              element={<VisitTourConfiguration />}
            />
            <Route path="history" element={<ActivityVisitTourHistory />} />

            <Route index element={<Navigate to="dashboard" replace />} />
          </Route>

          {/* =====================================================
              CONVENIENCE ROUTES
              ===================================================== */}

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

            <Route path="tour-history" element={<ConvenienceTourHistory />} />

            <Route index element={<Navigate to="dashboard" replace />} />
          </Route>

          {/* =====================================================
              FINANCE ROUTES
              ===================================================== */}

          <Route
            path="/finance/*"
            element={
              <ProtectedRoute allowedRoles={["FINANCE"]}>
                <Routes>
                  <Route path="dashboard" element={<FinanceDashboard />} />

                  <Route
                    path=""
                    element={<Navigate to="dashboard" replace />}
                  />
                </Routes>
              </ProtectedRoute>
            }
          />

          {/* =====================================================
              FALLBACK 404
              ===================================================== */}

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
