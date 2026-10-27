import React from "react";
import { EmptyState, LoadingState } from "./OnboardAssignmentShared";

function ActivityCruiseAssignmentTable({
  tour,
  activities,
  loading,
  onAssign,
}) {
  if (loading) {
    return <LoadingState>Đang tải danh sách hoạt động...</LoadingState>;
  }

  if (!activities.length) {
    return (
      <EmptyState>
        Tour này chưa có hoạt động trên tàu được cấu hình.
      </EmptyState>
    );
  }

  return (
    <div className="onboard-target-list">
      {activities.map((activity) => (
        <div className="onboard-target-card" key={activity.id}>
          <div className="onboard-target-main">
            <div className="onboard-target-info">
              <div className="onboard-target-icon">🎯</div>

              <div>
                <h3>
                  {activity.activityName ||
                    activity.name ||
                    activity.title ||
                    "Hoạt động trên tàu"}
                </h3>

                <p>
                  Tour: {tour?.code || tour?.name}
                  {" · "}
                  Trạng thái: {activity.status || "Chưa xác định"}
                </p>
              </div>
            </div>

            <div className="onboard-target-actions">
              <button
                type="button"
                className="onboard-button onboard-button-primary"
                onClick={() =>
                  onAssign({
                    tourId: activity.tourId,
                    activityCruiseTourId: activity.id,
                    title:
                      activity.activityName ||
                      activity.name ||
                      activity.title ||
                      "Hoạt động trên tàu",
                    subtitle: tour?.code || tour?.name,
                  })
                }
              >
                + Phân công nhân viên
              </button>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default ActivityCruiseAssignmentTable;
