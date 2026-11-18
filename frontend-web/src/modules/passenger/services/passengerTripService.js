import api from "../../../api/axios";

const unwrap = (response) => {
  return response?.data?.data ?? response?.data ?? null;
};

const passengerTripService = {
  // ==========================================
  // TOUR SERVICE - ROUTE
  // ==========================================

  async getRouteOverview(tourId) {
    const response = await api.get(`/tours/routes/tours/${tourId}/overview`);
    return unwrap(response);
  },

  async getRouteLegs(tourId) {
    const response = await api.get(`/tours/routes/tours/${tourId}/legs`);
    return unwrap(response);
  },

  // ==========================================
  // TOUR SERVICE - TRIP INFORMATION
  // ==========================================

  async getItinerary(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/itinerary`);
    return unwrap(response);
  },

  async getCruise(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/cruise`);
    return unwrap(response);
  },

  async getCruiseDecks(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/cruise/decks`);
    return unwrap(response);
  },

  async getCruiseDeckAreas(tourId, deckId) {
    const response = await api.get(
      `/passenger/tours/${tourId}/cruise/decks/${deckId}/areas`,
    );
    return unwrap(response);
  },

  async getProducts(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/products`);
    return unwrap(response);
  },

  async getServices(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/services`);
    return unwrap(response);
  },

  async getCruiseActivities(tourId) {
    const response = await api.get(
      `/passenger/tours/${tourId}/activities-cruise`,
    );
    return unwrap(response);
  },

  async getVisits(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/visits`);
    return unwrap(response);
  },

  async getPackages(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/packages`);
    return unwrap(response);
  },

  // ==========================================
  // BOOKING SERVICE
  // ==========================================

  async getBookingTripDetail(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/trip-detail`,
    );
    return unwrap(response);
  },

  async getBookingPassengers(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/passengers`,
    );
    return unwrap(response);
  },

  async getBookingBenefits(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/benefits`,
    );
    return unwrap(response);
  },

  async getProductUsages(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/usages/products`,
    );
    return unwrap(response);
  },

  async getServiceUsages(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/usages/services`,
    );
    return unwrap(response);
  },

  async getCruiseActivityUsages(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/usages/activities-cruise`,
    );
    return unwrap(response);
  },

  async getVisitUsages(bookingId) {
    const response = await api.get(
      `/passengers/bookings/${bookingId}/usages/visits`,
    );
    return unwrap(response);
  },

  async getBills(bookingId) {
    const response = await api.get(`/passengers/bookings/${bookingId}/bills`);
    return unwrap(response);
  },

  // ==========================================
  // SHARED APIs
  // ==========================================

  async getVesselLocation(tourId) {
    const response = await api.get(`/vessel-tracking/tours/${tourId}/location`);

    // API có thể trả HTTP 204 nếu chưa có GPS.
    return unwrap(response);
  },

  async getWeather(latitude, longitude, startDate, endDate) {
    const response = await api.get("/public/weather", {
      params: {
        latitude,
        longitude,
        ...(startDate ? { startDate } : {}),
        ...(endDate ? { endDate } : {}),
      },
    });

    return unwrap(response);
  },
};

export default passengerTripService;
