// src/modules/shore/hooks/useShoreStaffAssignment.js
// (FILE MỚI) Tải nhân viên SHORE_STAFF + danh sách đã phân công của 1 Visit Tour,
// và giữ state các nhân viên được chọn thêm.

import { useCallback, useEffect, useRef, useState } from "react";

import visitTourService from "../services/visitTourService";

const useShoreStaffAssignment = (visitTourId) => {
  const [staffList, setStaffList] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

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
  };
};

export default useShoreStaffAssignment;
