import api from "../../../api/axios";

const scheduleStopService = {
  /**
   * Lấy tất cả điểm dừng của Schedule
   */
  getAllScheduleStops: async (scheduleId) => {
    const response = await api.get(`/scheduler/schedules/${scheduleId}/stops`);

    return response.data;
  },

  /**
   * Lấy Schedule Stop theo ID
   */
  getScheduleStopById: async (scheduleId, stopId) => {
    const response = await api.get(
      `/scheduler/schedules/${scheduleId}/stops/${stopId}`,
    );

    return response.data;
  },

  /**
   * Tạo Schedule Stop
   *
   * Request:
   * {
   *   portId,
   *   stopOrder,
   *   arriveAt,
   *   leaveAt
   * }
   */
  createScheduleStop: async (scheduleId, data) => {
    const response = await api.post(
      `/scheduler/schedules/${scheduleId}/stops`,
      data,
    );

    return response.data;
  },

  /**
   * Cập nhật Schedule Stop
   */
  updateScheduleStop: async (scheduleId, stopId, data) => {
    const response = await api.patch(
      `/scheduler/schedules/${scheduleId}/stops/${stopId}`,
      data,
    );

    return response.data;
  },

  /**
   * Xóa Schedule Stop
   */
  deleteScheduleStop: async (scheduleId, stopId) => {
    await api.delete(`/scheduler/schedules/${scheduleId}/stops/${stopId}`);

    return true;
  },
};

export default scheduleStopService;
