// src/modules/operation/hooks/useTourPackages.js
import { useState, useEffect, useCallback } from "react";
import tourPackageService from "../services/tourPackageService";

export const useTourPackages = (tourId) => {
  const [packages, setPackages] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Lấy danh sách gói tour
  const fetchPackages = useCallback(async () => {
    if (!tourId) return;
    try {
      setLoading(true);
      setError(null);
      const data = await tourPackageService.getPackagesByTourId(tourId);
      setPackages(data);
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải danh sách gói tour.",
      );
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  // Lấy danh sách hạng phòng theo tour
  const fetchRoomTypes = useCallback(async () => {
    if (!tourId) return;
    try {
      const data = await tourPackageService.getRoomTypesByTourId(tourId);
      setRoomTypes(data);
    } catch (err) {
      console.error("Không thể tải danh sách hạng phòng:", err);
    }
  }, [tourId]);

  useEffect(() => {
    fetchPackages();
    fetchRoomTypes();
  }, [fetchPackages, fetchRoomTypes]);

  // Tạo mới gói tour
  const createPackage = async (packageData) => {
    try {
      const newPkg = await tourPackageService.createPackage({
        ...packageData,
        tourId,
      });
      setPackages((prev) => [...prev, newPkg]);
      return newPkg;
    } catch (err) {
      throw new Error(err.response?.data?.message || "Lỗi khi tạo gói tour.");
    }
  };

  // Cập nhật gói tour
  const patchPackage = async (packageId, packageData) => {
    try {
      const updatedPkg = await tourPackageService.patchPackage(
        packageId,
        packageData,
      );
      setPackages((prev) =>
        prev.map((pkg) => (pkg.id === packageId ? updatedPkg : pkg)),
      );
      return updatedPkg;
    } catch (err) {
      throw new Error(
        err.response?.data?.message || "Lỗi khi cập nhật gói tour.",
      );
    }
  };

  // Xóa gói tour
  const deletePackage = async (packageId) => {
    try {
      await tourPackageService.deletePackage(packageId);
      setPackages((prev) => prev.filter((pkg) => pkg.id !== packageId));
    } catch (err) {
      throw new Error(err.response?.data?.message || "Lỗi khi xóa gói tour.");
    }
  };

  return {
    packages,
    roomTypes,
    loading,
    error,
    refreshPackages: fetchPackages,
    refreshRoomTypes: fetchRoomTypes,
    createPackage,
    patchPackage,
    deletePackage,
  };
};
