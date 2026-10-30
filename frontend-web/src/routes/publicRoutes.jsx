// src/routes/publicRoutes.jsx

import { Route } from "react-router-dom";

import GuestLayout from "../layouts/GuestLayout";
import TourPublic from "../modules/guest/pages/TourPublic";
import TourPublicDetail from "../modules/guest/pages/TourPublicDetail";

import RegisterPage from "../modules/auth/pages/RegisterPage";
import LoginPage from "../modules/auth/pages/LoginPage";
import VerifyOtpPage from "../modules/auth/pages/VerifyOtpPage";
import ActivatePage from "../modules/auth/pages/ActivatePage";
import GoogleVerifyOtpPage from "../modules/auth/pages/GoogleVerifyOtpPage";
import PaymentResultPage from "../modules/passenger/pages/PaymentResultPage";

const publicRoutes = (
  <>
    <Route element={<GuestLayout />}>
      <Route path="/" element={<TourPublic />} />
      <Route path="/tours" element={<TourPublic />} />
      <Route path="/tours/:id" element={<TourPublicDetail />} />
    </Route>

    <Route path="/activate" element={<ActivatePage />} />
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    <Route path="/verify-email" element={<VerifyOtpPage />} />
    <Route path="/google-verify-otp" element={<GoogleVerifyOtpPage />} />
    <Route path="/payment/result" element={<PaymentResultPage />} />
  </>
);

export default publicRoutes;
