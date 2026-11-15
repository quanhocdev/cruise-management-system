import api from "../../../api/axios";

const adminProfileService = {
  async getByUserId(userId) {
    return api.get(`/admin/profiles/${userId}`);
  },
};

export default adminProfileService;
