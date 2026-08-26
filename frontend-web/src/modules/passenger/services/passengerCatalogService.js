import api from "../../../api/axios";

const passengerCatalogService = {
  async getOpenTours() {
    const response = await api.get("/passenger/tours");
    return response.data;
  },

  async getTourDetail(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}`);
    return response.data;
  },

  async getDepartures(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/departures`);
    return response.data;
  },

  async getAvailableRooms(voyageId) {
    const response = await api.get(
      `/v1/bookings/voyages/${voyageId}/available-rooms`,
    );
    return response.data;
  },
};

export default passengerCatalogService;
