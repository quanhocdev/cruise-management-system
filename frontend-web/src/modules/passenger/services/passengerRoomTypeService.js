import api from "../../../api/axios";

const passengerRoomTypeService = {
  async getRoomTypeDetail(roomTypeId) {
    const response = await api.get(`/passenger/room-types/${roomTypeId}`);
    return response.data;
  },
};

export default passengerRoomTypeService;
