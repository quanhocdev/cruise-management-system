import { useCallback, useEffect, useMemo, useState } from "react";
import onboardAssignmentService from "../services/onboardAssignmentService";

const getUserId = (staff) => staff?.userId || staff?.id;

export default function useOnboardAssignment({
  assignment,
  assignmentType,
  open,
}) {
  const [staffList, setStaffList] = useState([]);
  const [assignments, setAssignments] = useState([]);

  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [error, setError] = useState("");
  const [result, setResult] = useState(null);

  const tourId = assignment?.tourId;

  const cruiseDeckId =
    assignmentType === "PATROL" ? assignment?.cruiseDeckId : null;

  const activityCruiseTourId =
    assignmentType === "ACTIVITY_CRUISE"
      ? assignment?.activityCruiseTourId
      : null;

  const targetId = cruiseDeckId || activityCruiseTourId;

  // =====================================================
  // LOAD STAFF + ASSIGNMENTS
  // =====================================================

  const reload = useCallback(async () => {
    if (!tourId || !targetId || !assignmentType) {
      setStaffList([]);
      setAssignments([]);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const [staffResponse, assignmentResponse] = await Promise.all([
        onboardAssignmentService.getAllStaff(),
        onboardAssignmentService.getAssignments({
          tourId,
          assignmentType,
          cruiseDeckId,
          activityCruiseTourId,
        }),
      ]);

      setStaffList(Array.isArray(staffResponse) ? staffResponse : []);
      setAssignments(
        Array.isArray(assignmentResponse) ? assignmentResponse : [],
      );
    } catch (err) {
      console.error("LOAD ONBOARD ASSIGNMENT ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách nhân viên hoặc phân công.",
      );
    } finally {
      setLoading(false);
    }
  }, [tourId, targetId, assignmentType, cruiseDeckId, activityCruiseTourId]);

  useEffect(() => {
    if (open && targetId) {
      reload();
    }
  }, [open, targetId, reload]);

  // =====================================================
  // ALREADY ASSIGNED
  // =====================================================

  const assignedUserIds = useMemo(
    () => new Set(assignments.map((item) => item.userId)),
    [assignments],
  );

  const availableStaff = useMemo(() => {
    const keyword = search.trim().toLowerCase();

    return staffList.filter((staff) => {
      const userId = getUserId(staff);

      if (!userId || assignedUserIds.has(userId)) {
        return false;
      }

      if (!keyword) {
        return true;
      }

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

  // =====================================================
  // TOGGLE STAFF
  // =====================================================

  const toggleStaff = useCallback((userId) => {
    setSelectedUserIds((previous) =>
      previous.includes(userId)
        ? previous.filter((id) => id !== userId)
        : [...previous, userId],
    );
  }, []);

  // =====================================================
  // ASSIGN
  // =====================================================

  const assignSelectedStaff = useCallback(async () => {
    if (!tourId || !targetId || selectedUserIds.length === 0) {
      return;
    }

    try {
      setSubmitting(true);
      setError("");
      setResult(null);

      const payload = {
        tourId,
        assignmentType,
        cruiseDeckId,
        activityCruiseTourId,
        userIds: selectedUserIds,
      };

      const response = await onboardAssignmentService.assignStaff(payload);

      setResult(response);
      setSelectedUserIds([]);

      await reload();

      return response;
    } catch (err) {
      console.error("ASSIGN ONBOARD STAFF ERROR:", err);

      setError(err.response?.data?.message || "Không thể phân công nhân viên.");

      throw err;
    } finally {
      setSubmitting(false);
    }
  }, [
    tourId,
    targetId,
    assignmentType,
    cruiseDeckId,
    activityCruiseTourId,
    selectedUserIds,
    reload,
  ]);

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
    reload,
  };
}
