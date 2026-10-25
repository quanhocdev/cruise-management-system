import api from "../../../api/axios";

const STAFF_URL = "/convenience/staff";
const ASSIGNMENT_URL = "/convenience/assignments";

const convenienceStaffAssignmentService = {
  async getAllStaff() {
    const response = await api.get(STAFF_URL);
    return response.data;
  },

  async getAssignments({ tourId, assignmentType, targetId }) {
    const response = await api.get(ASSIGNMENT_URL, {
      params: {
        tourId,
        assignmentType,
        targetId,
      },
    });

    return response.data;
  },

  async assignStaff(payload) {
    const response = await api.post(ASSIGNMENT_URL, payload);
    return response.data;
  },
};

export default convenienceStaffAssignmentService;
