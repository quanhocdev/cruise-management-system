import api from "../../../api/axios";

const STAFF_URL = "/onboard/staff";
const ASSIGNMENT_URL = "/onboard/assignments";

const onboardAssignmentService = {
  async getAllStaff() {
    const response = await api.get(STAFF_URL);
    return response.data;
  },

  async getAssignments({
    tourId,
    assignmentType,
    cruiseDeckId,
    activityCruiseTourId,
  }) {
    const response = await api.get(ASSIGNMENT_URL, {
      params: {
        tourId,
        assignmentType,
        ...(cruiseDeckId ? { cruiseDeckId } : {}),
        ...(activityCruiseTourId ? { activityCruiseTourId } : {}),
      },
    });

    return response.data;
  },

  async assignStaff(payload) {
    const response = await api.post(ASSIGNMENT_URL, payload);
    return response.data;
  },
};

export default onboardAssignmentService;
