// src/modules/shore/hooks/useShoreStaffAssignment.js

import { useCallback, useEffect, useRef, useState } from "react";

import visitTourService from "../services/visitTourService";

const useShoreStaffAssignment = (visitTourId) => {
  const [staffList, setStaffList] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [cancelingId, setCancelingId] = useState(null);
  const [actionError, setActionError] = useState("");
  const [actionSuccess, setActionSuccess] = useState("");

  const requestRef = useRef(0); // bỏ qua kết quả của request cũ

  const load = useCallback(async () => {
    const requestId = ++requestRef.current;

    setLoading(true);
    setError("");
    setStaffList([]);
    setAssignments([]);

    const [staffResult, assignmentResult] = await Promise.allSettled([
      visitTourService.getShoreStaff(),
      visitTourId
        ? visitTourService.getAssignments(visitTourId)
        : Promise.resolve([]),
    ]);

    if (requestId !== requestRef.current) return;

    const messages = [];

    if (staffResult.status === "fulfilled") {
      const data = staffResult.value;
      setStaffList(
        Array.isArray(data)
          ? data.filter((s) => s?.userId && s.role === "SHORE_STAFF")
          : [],
      );
    } else {
      console.error("LOAD SHORE STAFF ERROR:", staffResult.reason);
      messages.push(
        staffResult.reason?.response?.data?.message ||
          "Không thể tải danh sách nhân viên Shore.",
      );
    }

    if (assignmentResult.status === "fulfilled") {
      const data = assignmentResult.value;
      setAssignments(Array.isArray(data) ? data : []);
    } else {
      console.error("LOAD ASSIGNMENTS ERROR:", assignmentResult.reason);
      messages.push("Không thể tải danh sách nhân viên đã phân công.");
    }

    setError(messages.join(" "));
    setLoading(false);
  }, [visitTourId]);

  useEffect(() => {
    load();
    return () => {
      requestRef.current++;
    };
  }, [load]);

  const toggleStaff = useCallback((userId) => {
    setSelectedUserIds((prev) =>
      prev.includes(userId)
        ? prev.filter((id) => id !== userId)
        : [...prev, userId],
    );
  }, []);

  const cancelAssignment = useCallback(
    async (assignmentId) => {
      if (!visitTourId || !assignmentId) return;

      setCancelingId(assignmentId);
      setActionError("");
      setActionSuccess("");

      try {
        await visitTourService.cancelStaffAssignment(visitTourId, assignmentId);

        setAssignments((previous) =>
          previous.filter((item) => item.id !== assignmentId),
        );

        setActionSuccess("Đã hủy phân công nhân viên thành công.");

        return true;
      } catch (err) {
        console.error("CANCEL SHORE ASSIGNMENT ERROR:", err);

        setActionError(
          err?.response?.data?.message || "Không thể hủy phân công nhân viên.",
        );

        return false;
      } finally {
        setCancelingId(null);
      }
    },
    [visitTourId],
  );
  // Không cho chọn lại người đã được phân công
  const assignedIds = new Set(assignments.map((a) => a.userId).filter(Boolean));
  const availableStaff = staffList.filter((s) => !assignedIds.has(s.userId));

  return {
    staffList,
    availableStaff,
    assignments,
    selectedUserIds,
    toggleStaff,
    loading,
    error,
    reload: load,

    cancelAssignment,
    cancelingId,
    actionError,
    actionSuccess,
  };
};

export default useShoreStaffAssignment;
