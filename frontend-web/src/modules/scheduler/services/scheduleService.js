import api from "../../../api/axios";

const scheduleService = {
  /**
   * Lấy tất cả Schedule của Tour
   */
  getAllSchedules: async (tourId) => {
    const response = await api.get(`/scheduler/tours/${tourId}/schedules`);

    return response.data;
  },

  /**
   * Lấy các Schedule đang ACTIVE
   */
  getActiveSchedules: async (tourId) => {
    const response = await api.get(`/scheduler/tours/${tourId}/schedules`, {
      params: {
        activeOnly: true,
      },
    });

    return response.data;
  },

  /**
   * Lấy Schedule theo ID
   */
  getScheduleById: async (tourId, scheduleId) => {
    const response = await api.get(
      `/scheduler/tours/${tourId}/schedules/${scheduleId}`,
    );

    return response.data;
  },

  /**
   * Tạo Schedule
   *
   * Request:
   * {
   *   name,
   *   description,
   *   dayNumber,
   *   realDay
   * }
   */
  createSchedule: async (tourId, data) => {
    const response = await api.post(
      `/scheduler/tours/${tourId}/schedules`,
      data,
    );

    return response.data;
  },

  /**
   * Cập nhật Schedule
   */
  updateSchedule: async (tourId, scheduleId, data) => {
    const response = await api.patch(
      `/scheduler/tours/${tourId}/schedules/${scheduleId}`,
      data,
    );

    return response.data;
  },

  /**
   * Xóa Schedule
   */
  deleteSchedule: async (tourId, scheduleId) => {
    await api.delete(`/scheduler/tours/${tourId}/schedules/${scheduleId}`);

    return true;
  },
};

export default scheduleService;
