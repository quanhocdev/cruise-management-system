import api from "../../../api/axios";

const BASE_URL = "/passenger/notifications";

const notificationService = {
  async getNotifications() {
    const response = await api.get(BASE_URL);
    return response.data;
  },

  async getUnreadCount() {
    const response = await api.get(`${BASE_URL}/unread/count`);
    return response.data?.unreadCount ?? 0;
  },

  async markAsRead(notificationId) {
    const response = await api.patch(`${BASE_URL}/${notificationId}/read`);
    return response.data;
  },

  async markAllAsRead() {
    const response = await api.patch(`${BASE_URL}/read-all`);
    return response.data?.updatedCount ?? 0;
  },
};

export default notificationService;
