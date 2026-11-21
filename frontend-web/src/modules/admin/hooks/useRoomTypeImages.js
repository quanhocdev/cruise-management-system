import { useCallback, useEffect, useState } from "react";
import roomTypeImageService from "../services/roomTypeImageService";

export default function useRoomTypeImages(roomTypeId, enabled = true) {
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD
  // =====================================================
  const loadImages = useCallback(async () => {
    if (!roomTypeId) {
      setImages([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await roomTypeImageService.getImages(roomTypeId);
      setImages(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("🔥 LOAD ROOM TYPE IMAGES ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể tải danh sách ảnh chi tiết.",
      );
    } finally {
      setLoading(false);
    }
  }, [roomTypeId]);

  // =====================================================
  // ADD
  // =====================================================
  const addImage = async ({ image, label, sortOrder } = {}) => {
    if (!roomTypeId) return null;

    setSubmitting(true);
    setError("");
    setSuccess("");

    try {
      const created = await roomTypeImageService.addImage(roomTypeId, {
        image,
        label,
        sortOrder,
      });

      setImages((previous) =>
        [...previous, created].sort(
          (a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0),
        ),
      );
      setSuccess("Đã thêm ảnh chi tiết.");
      return created;
    } catch (err) {
      console.error("🔥 ADD ROOM TYPE IMAGE ERROR:", err);
      setError(err.response?.data?.message || "Không thể thêm ảnh chi tiết.");
      return null;
    } finally {
      setSubmitting(false);
    }
  };

  // =====================================================
  // DELETE
  // =====================================================
  const deleteImage = async (imageId) => {
    if (!roomTypeId || !imageId) return false;

    setDeletingId(imageId);
    setError("");
    setSuccess("");

    try {
      await roomTypeImageService.deleteImage(roomTypeId, imageId);
      setImages((previous) => previous.filter((item) => item.id !== imageId));
      setSuccess("Đã xóa ảnh chi tiết.");
      return true;
    } catch (err) {
      console.error("🔥 DELETE ROOM TYPE IMAGE ERROR:", err);
      setError(err.response?.data?.message || "Không thể xóa ảnh chi tiết.");
      return false;
    } finally {
      setDeletingId(null);
    }
  };

  // =====================================================
  // AUTO LOAD WHEN OPENED
  // =====================================================
  useEffect(() => {
    if (enabled && roomTypeId) {
      setError("");
      setSuccess("");
      loadImages();
    }
  }, [enabled, roomTypeId, loadImages]);

  return {
    images,
    loading,
    submitting,
    deletingId,
    error,
    success,
    setError,
    setSuccess,
    loadImages,
    addImage,
    deleteImage,
  };
}
