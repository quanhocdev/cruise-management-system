// src/modules/passenger/pages/PassengerTripDetail.jsx
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Container, Alert, Nav } from "react-bootstrap";

import usePassengerTripDetail from "../hooks/trip-detail/usePassengerTripDetail";

import TripDetailHero from "../components/trip-detail/TripDetailHero";
import TripRouteSection from "../components/trip-detail/TripRouteSection";
import TripItinerarySection from "../components/trip-detail/TripItinerarySection";
import TripCruiseSection from "../components/trip-detail/TripCruiseSection";
import TripFacilitiesSection from "../components/trip-detail/TripFacilitiesSection";
import TripPackagesSection from "../components/trip-detail/TripPackagesSection";
import TripBookingSection from "../components/trip-detail/TripBookingSection";
import TripPassengersSection from "../components/trip-detail/TripPassengersSection";
import TripBenefitsSection from "../components/trip-detail/TripBenefitsSection";
import TripUsagesSection from "../components/trip-detail/TripUsagesSection";
import TripBillsSection from "../components/trip-detail/TripBillsSection";

import "../styles/PassengerTripDetail.css";

const TRIP_SECTIONS = [
  { key: "tracking", label: "Theo dõi hành trình" },
  { key: "itinerary", label: "Lịch trình" },
  { key: "cruise", label: "Du thuyền" },
  { key: "facilities", label: "Dịch vụ & tiện ích" },
  { key: "booking", label: "Đặt chỗ" },
  { key: "passengers", label: "Hành khách" },
  { key: "benefits", label: "Quyền lợi" },
  { key: "usages", label: "Lượt sử dụng" },
  { key: "bills", label: "Hóa đơn" },
];

export default function PassengerTripDetail({
  tourId: propTourId,
  bookingId: propBookingId,
}) {
  const params = useParams();

  const tourId = propTourId || params.tourId;
  const bookingId = propBookingId || params.bookingId;

  const [activeSection, setActiveSection] = useState("tracking");

  const { tour, booking, vessel, loadSection, reloadSection } =
    usePassengerTripDetail({
      tourId,
      bookingId,
      activeSection,
    });

  useEffect(() => {
    if (!tourId || !bookingId) return;

    loadSection(activeSection).catch(() => {});
  }, [tourId, bookingId, activeSection, loadSection]);

  const handleReload = () => {
    reloadSection(activeSection).catch(() => {});
  };

  if (!tourId || !bookingId) {
    return (
      <Container className="passenger-trip-detail-page py-5">
        <Alert variant="warning">
          Không tìm thấy thông tin chuyến đi hoặc đơn đặt chỗ.
        </Alert>
      </Container>
    );
  }

  return (
    <div className="passenger-trip-detail-page">
      {/* HERO - Giữ nguyên phần tổng quan chuyến đi */}
      <TripDetailHero
        booking={booking.detail.booking}
        tourOverview={tour.route.overview}
        itinerary={tour.itinerary.data}
        cruise={tour.cruise.cruise}
        onReload={handleReload}
      />

      <Container className="passenger-trip-detail-content py-4">
        {/* SECTION NAVIGATION */}
        <div className="passenger-trip-section-nav-wrapper mb-4">
          <Nav
            variant="pills"
            activeKey={activeSection}
            onSelect={(selectedKey) => {
              if (selectedKey) setActiveSection(selectedKey);
            }}
            className="passenger-trip-section-nav flex-nowrap"
          >
            {TRIP_SECTIONS.map((section) => (
              <Nav.Item key={section.key}>
                <Nav.Link eventKey={section.key}>{section.label}</Nav.Link>
              </Nav.Item>
            ))}
          </Nav>
        </div>

        {/* THEO DÕI HÀNH TRÌNH */}
        {activeSection === "tracking" && (
          <TripRouteSection
            overview={tour.route.overview}
            legs={tour.route.legs}
            vesselLocation={vessel.vesselLocation}
            loading={
              tour.route.overviewLoading ||
              tour.route.legsLoading ||
              vessel.loading
            }
            error={
              tour.route.overviewError || tour.route.legsError || vessel.error
            }
            onReload={handleReload}
          />
        )}

        {/* LỊCH TRÌNH */}
        {activeSection === "itinerary" && (
          <TripItinerarySection
            itinerary={tour.itinerary.data}
            loading={tour.itinerary.loading}
            error={tour.itinerary.error}
            onReload={handleReload}
          />
        )}

        {/* DU THUYỀN */}
        {activeSection === "cruise" && (
          <TripCruiseSection
            cruise={tour.cruise.cruise}
            decks={tour.cruise.decks}
            deckAreas={tour.cruise.deckAreas}
            loading={tour.cruise.cruiseLoading || tour.cruise.decksLoading}
            error={tour.cruise.cruiseError || tour.cruise.decksError}
            areasLoading={tour.cruise.areasLoading}
            areasError={tour.cruise.areasError}
            onLoadDeckAreas={tour.cruise.loadDeckAreas}
          />
        )}

        {/* DỊCH VỤ VÀ TIỆN ÍCH */}
        {activeSection === "facilities" && (
          <>
            <TripFacilitiesSection
              products={tour.facilities.products}
              services={tour.facilities.services}
              cruiseActivities={tour.facilities.cruiseActivities}
              visits={tour.facilities.visits}
            />

            <TripPackagesSection
              packages={tour.packages.packages}
              loading={tour.packages.loading}
              error={tour.packages.error}
              onReload={handleReload}
            />
          </>
        )}

        {/* ĐẶT CHỖ */}
        {activeSection === "booking" && (
          <TripBookingSection
            booking={booking.detail.booking}
            loading={booking.detail.loading}
            error={booking.detail.error}
            onReload={handleReload}
          />
        )}

        {/* HÀNH KHÁCH */}
        {activeSection === "passengers" && (
          <TripPassengersSection
            passengers={booking.passengers.passengers}
            loading={booking.passengers.loading}
            error={booking.passengers.error}
            onReload={handleReload}
          />
        )}

        {/* QUYỀN LỢI */}
        {activeSection === "benefits" && (
          <TripBenefitsSection
            benefits={booking.benefits.benefits}
            loading={booking.benefits.loading}
            error={booking.benefits.error}
            onReload={handleReload}
          />
        )}

        {/* LƯỢT SỬ DỤNG */}
        {activeSection === "usages" && (
          <TripUsagesSection
            productUsages={booking.usages.productUsages}
            serviceUsages={booking.usages.serviceUsages}
            activityCruiseUsages={booking.usages.activityCruiseUsages}
            visitUsages={booking.usages.visitUsages}
            loading={{
              products: booking.usages.productUsagesLoading,
              services: booking.usages.serviceUsagesLoading,
              activities: booking.usages.activityCruiseUsagesLoading,
              visits: booking.usages.visitUsagesLoading,
            }}
            errors={{
              products: booking.usages.productUsagesError,
              services: booking.usages.serviceUsagesError,
              activities: booking.usages.activityCruiseUsagesError,
              visits: booking.usages.visitUsagesError,
            }}
            onReload={{
              products: handleReload,
              services: handleReload,
              activities: handleReload,
              visits: handleReload,
            }}
          />
        )}

        {/* HÓA ĐƠN */}
        {activeSection === "bills" && (
          <TripBillsSection
            bills={booking.bills.bills}
            loading={booking.bills.loading}
            error={booking.bills.error}
            onReload={handleReload}
          />
        )}
      </Container>
    </div>
  );
}
