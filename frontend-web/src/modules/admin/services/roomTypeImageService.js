import api from "../../../api/axios";

const getBaseUrl = (roomTypeId) => `/admin/room-types/${roomTypeId}/images`;

const roomTypeImageService = {
  async getImages(roomTypeId) {
    const response = await api.get(getBaseUrl(roomTypeId));
    return response.data;
  },

  async addImage(roomTypeId, { image, label, sortOrder } = {}) {
    const formData = new FormData();

    if (image instanceof File) {
      formData.append("image", image);
    }

    if (label !== undefined && label !== null && String(label).trim() !== "") {
      formData.append("label", String(label).trim());
    }

    if (sortOrder !== undefined && sortOrder !== null && sortOrder !== "") {
      formData.append("sortOrder", sortOrder);
    }

    const response = await api.post(getBaseUrl(roomTypeId), formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  async deleteImage(roomTypeId, imageId) {
    const response = await api.delete(`${getBaseUrl(roomTypeId)}/${imageId}`);
    return response.data;
  },
};

export default roomTypeImageService;
