// src/modules/passenger/pages/PassengerTripDetail.jsx
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import { Container, Spinner, Alert } from "react-bootstrap";

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

export default function PassengerTripDetail({
  tourId: propTourId,
  bookingId: propBookingId,
}) {
  const params = useParams();

  const tourId = propTourId || params.tourId;
  const bookingId = propBookingId || params.bookingId;

  const { tour, booking, vessel, weather, loadTourData, loadBookingData } =
    usePassengerTripDetail({
      tourId,
      bookingId,
    });

  useEffect(() => {
    if (tourId) {
      loadTourData();
    }

    if (bookingId) {
      loadBookingData();
    }
  }, [tourId, bookingId, loadTourData, loadBookingData]);

  const reloadTour = () => loadTourData();
  const reloadBooking = () => loadBookingData();

  if (!tourId || !bookingId) {
    return (
      <Container className="passenger-trip-detail-page py-5">
        <Alert variant="warning">
          Không tìm thấy thông tin chuyến đi hoặc đơn đặt chỗ.
        </Alert>
      </Container>
    );
  }

  const initialLoading =
    tour.route.overviewLoading ||
    tour.itinerary.loading ||
    booking.detail.loading;

  return (
    <div className="passenger-trip-detail-page">
      <TripDetailHero
        booking={booking.detail.booking}
        cruise={tour.cruise.cruise}
        onReload={() => {
          reloadTour();
          reloadBooking();
        }}
      />

      <Container className="passenger-trip-detail-content">
        {initialLoading && (
          <div className="passenger-trip-loading">
            <Spinner animation="border" variant="primary" />
            <p>Đang tải thông tin chuyến đi...</p>
          </div>
        )}

        <TripRouteSection
          overview={tour.route.overview}
          legs={tour.route.legs}
          vesselLocation={vessel.vesselLocation}
          loading={tour.route.overviewLoading || tour.route.legsLoading}
          error={tour.route.overviewError || tour.route.legsError}
          onReload={tour.route.loadRoute}
        />

        <TripItinerarySection
          itinerary={tour.itinerary.data}
          loading={tour.itinerary.loading}
          error={tour.itinerary.error}
          onReload={tour.itinerary.load}
        />

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
          onReload={tour.packages.loadPackages}
        />

        <TripBookingSection
          booking={booking.detail.booking}
          loading={booking.detail.loading}
          error={booking.detail.error}
          onReload={booking.detail.loadBooking}
        />

        <TripPassengersSection
          passengers={booking.passengers.passengers}
          loading={booking.passengers.loading}
          error={booking.passengers.error}
          onReload={booking.passengers.loadPassengers}
        />

        <TripBenefitsSection
          benefits={booking.benefits.benefits}
          loading={booking.benefits.loading}
          error={booking.benefits.error}
          onReload={booking.benefits.loadBenefits}
        />

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
        />

        <TripBillsSection
          bills={booking.bills.bills}
          loading={booking.bills.loading}
          error={booking.bills.error}
          onReload={booking.bills.loadBills}
        />
      </Container>
    </div>
  );
}
