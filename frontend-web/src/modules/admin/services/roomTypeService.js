import api from "../../../api/axios";

const ROOM_TYPE_BASE_URL = "/admin/room-types";

const buildRoomTypeFormData = (data) => {
  const formData = new FormData();

  if (data.name !== undefined && data.name !== null) {
    formData.append("name", data.name.trim());
  }

  if (
    data.description !== undefined &&
    data.description !== null &&
    data.description !== ""
  ) {
    formData.append("description", data.description);
  }

  if (data.price !== undefined && data.price !== null && data.price !== "") {
    formData.append("price", data.price);
  }

  if (
    data.capacity !== undefined &&
    data.capacity !== null &&
    data.capacity !== ""
  ) {
    formData.append("capacity", data.capacity);
  }

  if (data.image instanceof File) {
    formData.append("image", data.image);
  }

  return formData;
};

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
    const formData = buildRoomTypeFormData(data);
    const response = await api.post(ROOM_TYPE_BASE_URL, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  // UPDATE
  async updateRoomType(id, data) {
    const formData = buildRoomTypeFormData(data);
    const response = await api.patch(`${ROOM_TYPE_BASE_URL}/${id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  // DELETE
  async deleteRoomType(id) {
    const response = await api.delete(`${ROOM_TYPE_BASE_URL}/${id}`);
    return response.data;
  },
};

export default roomTypeService;
