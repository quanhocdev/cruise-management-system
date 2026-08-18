import api from "../../../api/axios";

const OPERATION_TOUR_BASE_URL = "/operation/tours";
const OPERATION_ASSIGNMENT_BASE_URL =
  "/operation/activity-cruise-tour-assignment";

const operationTourService = {
  /**
   * GET /api/operation/tours/pending
   */
  getPendingTours: async () => {
    const response = await api.get(`${OPERATION_TOUR_BASE_URL}/pending`);
    return response.data;
  },

  /**
   * GET /api/operation/tours/approved
   */
  getApprovedTours: async () => {
    const response = await api.get(`${OPERATION_TOUR_BASE_URL}/approved`);
    return response.data;
  },

  /**
   * GET /api/operation/tours/{id}/available-cruises
   */
  getAvailableCruises: async (tourId) => {
    const response = await api.get(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/available-cruises`,
    );
    return response.data;
  },

  /**
   * GET /api/operation/tours/{id}/cruise-layout
   * Lấy toàn bộ Deck + Area của Cruise đang được gán cho Tour.
   */
  getCruiseLayout: async (tourId) => {
    const response = await api.get(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/cruise-layout`,
    );
    return response.data;
  },

  /**
   * POST /api/operation/tours/{id}/assign-cruise?cruiseId=...
   * Gán du thuyền cho Tour (Tour vẫn ở trạng thái APPROVAL_PENDING)
   */
  assignCruise: async (tourId, cruiseId) => {
    const response = await api.post(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/assign-cruise`,
      null,
      {
        params: {
          cruiseId,
        },
      },
    );
    return response.data;
  },

  /**
   * POST /api/operation/tours/{id}/approve
   * Duyệt Tour - Đổi trạng thái sang APPROVED sau khi đã phân công xong khu vực
   */
  approveTour: async (tourId, payload = null) => {
    const response = await api.post(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/approve`,
      payload,
    );
    return response.data;
  },

  /**
   * GET /api/operation/activity-cruise-tour-assignment/tour/{tourId}
   * Lấy danh sách các khu vực đã phân công cho Tour (Bảng ActivityCruiseTour)
   */
  getActivityCruiseAssignments: async (tourId) => {
    const response = await api.get(
      `${OPERATION_ASSIGNMENT_BASE_URL}/tour/${tourId}`,
    );
    return response.data;
  },

  /**
   * POST /api/operation/activity-cruise-tour-assignment
   * Phân công 1 khu vực Hoạt động cho Tour: payload = { tourId, cruiseAreaId }
   */
  assignActivityCruiseArea: async (payload) => {
    const response = await api.post(OPERATION_ASSIGNMENT_BASE_URL, payload, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response.data;
  },

  /**
   * DELETE /api/operation/activity-cruise-tour-assignment/{id}
   * Xóa phân công khu vực theo Primary Key Assignment ID
   */
  deleteActivityCruiseAssignment: async (assignmentId) => {
    const response = await api.delete(
      `${OPERATION_ASSIGNMENT_BASE_URL}/${assignmentId}`,
    );
    return response.data;
  },
};

export default operationTourService;
