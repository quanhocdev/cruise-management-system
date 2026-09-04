// src/modules/operation/services/tourPackageService.js
import api from "../../../api/axios";

const OPERATION_PACKAGE_BASE_URL = "/operation/tour-packages";

const tourPackageService = {
  /**
   * GET /api/operation/tour-packages/tour/{tourId}
   * Lấy danh sách gói tour theo Tour ID
   */
  getPackagesByTourId: async (tourId) => {
    const response = await api.get(
      `${OPERATION_PACKAGE_BASE_URL}/tour/${tourId}`,
    );
    return response.data;
  },

  /**
   * POST /api/operation/tour-packages
   * Tạo mới gói tour kèm quyền lợi
   */
  createPackage: async (packageData) => {
    const response = await api.post(OPERATION_PACKAGE_BASE_URL, packageData);
    return response.data;
  },

  /**
   * PATCH /api/operation/tour-packages/{packageId}
   * Cập nhật một phần thông tin gói tour
   */
  patchPackage: async (packageId, packageData) => {
    const response = await api.patch(
      `${OPERATION_PACKAGE_BASE_URL}/${packageId}`,
      packageData,
    );
    return response.data;
  },

  /**
   * DELETE /api/operation/tour-packages/{packageId}
   * Xóa gói tour
   */
  deletePackage: async (packageId) => {
    const response = await api.delete(
      `${OPERATION_PACKAGE_BASE_URL}/${packageId}`,
    );
    return response.data;
  },
  /**
   * GET /api/operation/tour-packages/tour/{tourId}/room-types
   * Lấy danh sách hạng phòng theo Tour ID để hiển thị dropdown chọn phòng
   */
  getRoomTypesByTourId: async (tourId) => {
    const response = await api.get(
      `${OPERATION_PACKAGE_BASE_URL}/tour/${tourId}/room-types`,
    );
    return response.data;
  },
};

export default tourPackageService;
