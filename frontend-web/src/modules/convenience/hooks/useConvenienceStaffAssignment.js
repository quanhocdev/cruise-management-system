import { useCallback, useEffect, useMemo, useState } from "react";
import convenienceStaffAssignmentService from "../services/convenienceStaffAssignmentService";

export function getUserId(staff) {
  return staff?.userId || staff?.id || null;
}

function getErrorMessage(error, fallback) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    error?.message ||
    fallback
  );
}

function normalizeStaff(staffData) {
  const list = Array.isArray(staffData) ? staffData : [];

  return list.filter((staff) => {
    const role = staff?.role;

    return (
      !role ||
      role === "CONVENIENCE_STAFF" ||
      role?.name === "CONVENIENCE_STAFF"
    );
  });
}

export default function useConvenienceStaffAssignment({
  assignment,
  assignmentType,
}) {
  const tourId = assignment?.tourId;
  const targetId = assignment?.id;

  const [staffList, setStaffList] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [cancelingId, setCancelingId] = useState(null);

  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [cancelError, setCancelError] = useState("");
  const [cancelSuccess, setCancelSuccess] = useState("");

  const loadData = useCallback(async () => {
    if (!tourId || !targetId || !assignmentType) {
      setError("Thiếu thông tin Tour hoặc đối tượng cần phân công.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const [staffData, assignmentData] = await Promise.all([
        convenienceStaffAssignmentService.getAllStaff(),
        convenienceStaffAssignmentService.getAssignments({
          tourId,
          assignmentType,
          targetId,
        }),
      ]);

      setStaffList(normalizeStaff(staffData));
      setAssignments(Array.isArray(assignmentData) ? assignmentData : []);
      setSelectedUserIds([]);
    } catch (err) {
      console.error("LOAD CONVENIENCE STAFF ASSIGNMENT ERROR:", err);

      setError(
        getErrorMessage(
          err,
          "Không thể tải danh sách nhân viên hoặc danh sách phân công.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }, [tourId, targetId, assignmentType]);

  useEffect(() => {
    setStaffList([]);
    setAssignments([]);
    setSelectedUserIds([]);
    setSearch("");
    setResult(null);
    setCancelError("");
    setCancelSuccess("");

    loadData();
  }, [loadData]);

  const assignedUserIds = useMemo(
    () =>
      new Set(
        assignments
          .map((item) => item.userId)
          .filter(Boolean)
          .map(String),
      ),
    [assignments],
  );

  const availableStaff = useMemo(() => {
    const keyword = search.trim().toLowerCase();

    return staffList.filter((staff) => {
      const userId = getUserId(staff);

      if (!userId || assignedUserIds.has(String(userId))) {
        return false;
      }

      if (!keyword) return true;

      const searchableText = [
        staff.fullName,
        staff.username,
        staff.email,
        staff.phone,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      return searchableText.includes(keyword);
    });
  }, [staffList, assignedUserIds, search]);

  const toggleStaff = useCallback((userId) => {
    if (!userId) return;

    setSelectedUserIds((previous) =>
      previous.includes(userId)
        ? previous.filter((id) => id !== userId)
        : [...previous, userId],
    );
  }, []);

  const assignSelectedStaff = async () => {
    if (!tourId || !targetId || !assignmentType) {
      setError("Thiếu thông tin Tour hoặc đối tượng cần phân công.");
      return false;
    }

    if (selectedUserIds.length === 0) {
      setError("Vui lòng chọn ít nhất một nhân viên.");
      return false;
    }

    setSubmitting(true);
    setError("");
    setResult(null);

    try {
      const response = await convenienceStaffAssignmentService.assignStaff({
        tourId,
        assignmentType,
        targetId,
        userIds: selectedUserIds,
      });

      setResult({
        assigned: response?.assigned || [],
        alreadyAssigned: response?.alreadyAssigned || [],
        invalidUserIds: response?.invalidUserIds || [],
      });

      setSelectedUserIds([]);

      await loadData();

      return true;
    } catch (err) {
      console.error("ASSIGN CONVENIENCE STAFF ERROR:", err);

      setError(getErrorMessage(err, "Không thể phân công nhân viên."));

      return false;
    } finally {
      setSubmitting(false);
    }
  };

  const cancelAssignment = useCallback(
    async (assignmentId) => {
      if (!tourId || !targetId || !assignmentType || !assignmentId) {
        setCancelError("Thiếu thông tin phân công cần hủy.");
        return false;
      }

      if (cancelingId) return false;

      const targetAssignment = assignments.find(
        (item) => String(item.id) === String(assignmentId),
      );

      if (!targetAssignment) {
        setCancelError("Không tìm thấy phân công cần hủy.");
        return false;
      }

      if (targetAssignment.status !== "PENDING") {
        setCancelError("Chỉ có thể hủy phân công đang chờ xác nhận.");
        return false;
      }

      setCancelingId(assignmentId);
      setCancelError("");
      setCancelSuccess("");

      try {
        await convenienceStaffAssignmentService.cancelAssignment({
          assignmentId,
          tourId,
          assignmentType,
          targetId,
        });

        // Xóa khỏi danh sách phân công ngay sau khi API thành công.
        // Nhân viên sẽ tự xuất hiện lại trong availableStaff.
        setAssignments((previous) =>
          previous.filter((item) => String(item.id) !== String(assignmentId)),
        );

        setSelectedUserIds((previous) =>
          previous.filter(
            (userId) => String(userId) !== String(targetAssignment.userId),
          ),
        );

        setCancelSuccess("Đã hủy phân công nhân viên thành công.");
        return true;
      } catch (err) {
        console.error("CANCEL CONVENIENCE STAFF ERROR:", err);

        setCancelError(
          getErrorMessage(err, "Không thể hủy phân công nhân viên."),
        );

        return false;
      } finally {
        setCancelingId(null);
      }
    },
    [tourId, targetId, assignmentType, assignments, cancelingId],
  );

  return {
    staffList,
    assignments,
    availableStaff,
    selectedUserIds,
    search,
    setSearch,
    loading,
    submitting,
    error,
    result,
    toggleStaff,
    assignSelectedStaff,
    reload: loadData,

    // Hủy phân công
    cancelAssignment,
    cancelingId,
    cancelError,
    cancelSuccess,
  };
}
