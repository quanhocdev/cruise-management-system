import api from "../../../api/axios";

const ROOM_TYPE_BASE_URL = "/admin/room-types";

const roomTypeService = {
  // GET ALL
  async getAllRoomTypes() {
    const response = await api.get(ROOM_TYPE_BASE_URL);
    return response.data;
  },

  // GET BY ID
  async getRoomTypeById(id) {
    const response = await api.get(`${ROOM_TYPE_BASE_URL}/${id}`);
    return response.data;
  },

  // CREATE
  async createRoomType(data) {
    const response = await api.post(ROOM_TYPE_BASE_URL, data);
    return response.data;
  },

  // UPDATE
  async updateRoomType(id, data) {
    const response = await api.patch(`${ROOM_TYPE_BASE_URL}/${id}`, data);
    return response.data;
  },

  // DELETE
  async deleteRoomType(id) {
    const response = await api.delete(`${ROOM_TYPE_BASE_URL}/${id}`);
    return response.data;
  },
};

export default roomTypeService;
