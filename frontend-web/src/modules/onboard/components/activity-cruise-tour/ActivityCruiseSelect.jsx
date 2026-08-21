// src/modules/onboard/components/activity-cruise-tour/ActivityCruiseSelect.jsx
import React, { useEffect, useState } from "react";
import { ChevronDown, Loader2 } from "lucide-react";

import { activityCruiseService } from "../../services/activityCruiseService";

import "../../styles/activity-cruise-tour/ActivityCruiseSelect.css";

const ActivityCruiseSelect = ({ value = "", onChange, disabled = false }) => {
  const [activities, setActivities] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(null);

  // =====================================================
  // LOAD ACTIVE ACTIVITIES
  // =====================================================

  useEffect(() => {
    let mounted = true;

    const loadActivities = async () => {
      try {
        setLoading(true);
        setError(null);

        const data = await activityCruiseService.getActive();

        if (mounted) {
          setActivities(data || []);
        }
      } catch (err) {
        console.error("LOAD ACTIVE ACTIVITY CRUISES ERROR:", err);

        if (mounted) {
          setError(
            err.response?.data?.message || "Không thể tải danh sách hoạt động",
          );
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    loadActivities();

    return () => {
      mounted = false;
    };
  }, []);

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    onChange?.(event.target.value);
  };

  return (
    <div className="activity-cruise-select">
      <div className="activity-cruise-select-wrapper">
        <select
          value={value}
          onChange={handleChange}
          disabled={disabled || loading}
          className={
            error
              ? "activity-cruise-select-input has-error"
              : "activity-cruise-select-input"
          }
        >
          <option value="">
            {loading ? "Đang tải hoạt động..." : "Chọn hoạt động"}
          </option>

          {activities.map((activity) => (
            <option key={activity.id} value={activity.id}>
              {activity.name}
            </option>
          ))}
        </select>

        {loading ? (
          <Loader2 size={17} className="activity-cruise-select-loading" />
        ) : (
          <ChevronDown size={17} className="activity-cruise-select-chevron" />
        )}
      </div>

      {error && <span className="activity-cruise-select-error">{error}</span>}

      {!loading && !error && activities.length === 0 && (
        <span className="activity-cruise-select-hint">
          Không có hoạt động nào đang ACTIVE.
        </span>
      )}
    </div>
  );
};

export default ActivityCruiseSelect;
