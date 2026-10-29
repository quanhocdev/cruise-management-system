// src/modules/operation/hooks/useTourConfigurationPage.js

import { useCallback, useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

import useActivityCruiseTourAssignments from "./useActivityCruiseTourAssignments";
import useActivityVisitTourAssignments from "./useActivityVisitTourAssignments";
import useProductTourAssignments from "./useProductTourAssignments";
import useServiceTourAssignments from "./useServiceTourAssignments";
import useTourBooking from "./useTourBooking";

import patrolRequirementService from "../services/patrolRequirementService";
import { isTourItemConfigured } from "../utils/tourConfigurationUtils";

const getErrorMessage = (error, fallback) =>
  error?.response?.data?.message ||
  error?.response?.data?.error ||
  error?.message ||
  fallback;

const useTourConfigurationPage = () => {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");

  const { getBookingConfig } = useTourBooking();

  const [bookingConfig, setBookingConfig] = useState(null);
  const [activeFilter, setActiveFilter] = useState("ALL");

  // Patrol requirements
  const [patrolRequirements, setPatrolRequirements] = useState([]);
  const [patrolLoading, setPatrolLoading] = useState(false);
  const [patrolSaving, setPatrolSaving] = useState(false);
  const [patrolError, setPatrolError] = useState("");
  const [patrolSuccess, setPatrolSuccess] = useState("");

  // ---------- 4 HOOK DỮ LIỆU ----------
  const {
    configuredActivities,
    configuredLoading: activityCruiseLoading,
    activityError,
    loadConfiguredActivitiesByTour,
  } = useActivityCruiseTourAssignments();

  const {
    configuredActivityVisits,
    configuredActivityVisitLoading: activityVisitLoading,
    activityVisitError,
    loadConfiguredActivityVisitsByTour,
  } = useActivityVisitTourAssignments();

  const {
    configuredProducts,
    configuredLoading: productLoading,
    productError,
    loadConfiguredProductsByTour,
  } = useProductTourAssignments();

  const {
    configuredServices,
    configuredLoading: serviceLoading,
    serviceError,
    loadConfiguredServicesByTour,
  } = useServiceTourAssignments();

  const loading =
    activityCruiseLoading ||
    activityVisitLoading ||
    productLoading ||
    serviceLoading;

  const error =
    activityError || activityVisitError || productError || serviceError;

  // ---------- PATROL REQUIREMENTS ----------

  const loadPatrolRequirements = useCallback(async () => {
    if (!tourId) return;

    setPatrolLoading(true);
    setPatrolError("");

    try {
      const response = await patrolRequirementService.getByTour(tourId);

      const requirements = Array.isArray(response)
        ? response
        : (response?.data ?? []);

      setPatrolRequirements(requirements);
    } catch (err) {
      setPatrolError(
        getErrorMessage(err, "Không thể tải cấu hình nhân sự tuần tra."),
      );
    } finally {
      setPatrolLoading(false);
    }
  }, [tourId]);

  const updatePatrolRequirement = useCallback((cruiseDeckId, value) => {
    const parsedValue = value === "" ? "" : Math.max(0, Number(value));

    setPatrolRequirements((previous) =>
      previous.map((item) =>
        item.cruiseDeckId === cruiseDeckId
          ? { ...item, requiredCount: parsedValue }
          : item,
      ),
    );

    setPatrolError("");
    setPatrolSuccess("");
  }, []);

  const savePatrolRequirements = useCallback(async () => {
    if (!tourId || patrolSaving) return;

    const invalidItem = patrolRequirements.find(
      (item) =>
        item.requiredCount === "" ||
        !Number.isInteger(Number(item.requiredCount)) ||
        Number(item.requiredCount) < 0,
    );

    if (invalidItem) {
      setPatrolError(
        `Số nhân sự tại tầng ${invalidItem.deckNumber} không hợp lệ.`,
      );
      return;
    }

    setPatrolSaving(true);
    setPatrolError("");
    setPatrolSuccess("");

    try {
      const payload = {
        requirements: patrolRequirements.map((item) => ({
          cruiseDeckId: item.cruiseDeckId,
          requiredCount: Number(item.requiredCount),
        })),
      };

      const response = await patrolRequirementService.save(tourId, payload);

      const requirements = Array.isArray(response)
        ? response
        : (response?.data ?? []);

      setPatrolRequirements(requirements);
      setPatrolSuccess("Đã lưu cấu hình nhân sự tuần tra thành công.");
    } catch (err) {
      setPatrolError(
        getErrorMessage(err, "Không thể lưu cấu hình nhân sự tuần tra."),
      );
    } finally {
      setPatrolSaving(false);
    }
  }, [tourId, patrolRequirements, patrolSaving]);

  // ---------- LOAD CONFIGURATIONS ----------

  const loadConfigurations = useCallback(async () => {
    if (!tourId) return;

    await Promise.all([
      loadConfiguredActivitiesByTour(tourId),
      loadConfiguredActivityVisitsByTour(tourId),
      loadConfiguredProductsByTour(tourId),
      loadConfiguredServicesByTour(tourId),
    ]);
  }, [
    tourId,
    loadConfiguredActivitiesByTour,
    loadConfiguredActivityVisitsByTour,
    loadConfiguredProductsByTour,
    loadConfiguredServicesByTour,
  ]);

  const loadBookingConfig = useCallback(async () => {
    if (!tourId) return;

    try {
      setBookingConfig(await getBookingConfig(tourId));
    } catch {
      setBookingConfig(null);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tourId]);

  // ---------- INITIAL LOAD ----------

  useEffect(() => {
    if (!tourId) return;

    loadConfigurations();
    loadBookingConfig();
    loadPatrolRequirements();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tourId]);

  const refresh = () => {
    loadConfigurations();
    loadBookingConfig();
    loadPatrolRequirements();
  };

  // ---------- FILTER ----------

  const handleFilterChange = (filterKey) => {
    const next = activeFilter === filterKey ? "ALL" : filterKey;
    setActiveFilter(next);

    if (next !== "ALL") {
      setTimeout(() => {
        document
          .getElementById(`${next}-section`)
          ?.scrollIntoView({ behavior: "smooth", block: "start" });
      }, 100);
    }
  };

  // ---------- SUMMARY ----------

  const allAssignments = [
    ...configuredActivities,
    ...configuredActivityVisits,
    ...configuredProducts,
    ...configuredServices,
  ];

  const hasAnyData = allAssignments.length > 0;

  const configurationComplete =
    hasAnyData &&
    allAssignments.every((item) => isTourItemConfigured(item.status));

  const hasBookingConfig =
    !!bookingConfig?.statusBooking &&
    bookingConfig.statusBooking !== "NOT_OPEN";

  return {
    tourId,
    loading,
    error,
    refresh,

    configuredActivities,
    configuredActivityVisits,
    configuredProducts,
    configuredServices,

    hasAnyData,
    configurationComplete,

    activeFilter,
    handleFilterChange,

    hasBookingConfig,

    // Patrol
    patrolRequirements,
    patrolLoading,
    patrolSaving,
    patrolError,
    patrolSuccess,
    updatePatrolRequirement,
    savePatrolRequirements,
  };
};

export default useTourConfigurationPage;
