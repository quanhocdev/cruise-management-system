import React, { useState } from "react";
import useOnboardTourConfig from "../../hooks/useOnboardTourConfig";

import PatrolAssignmentTable from "./PatrolAssignmentTable";
import ActivityCruiseAssignmentTable from "./ActivityCruiseAssignmentTable";
import OnboardStaffAssignmentModal from "./OnboardStaffAssignmentModal";

function OnboardTourConfig() {
  const {
    tours,
    selectedTour,
    selectedTourId,
    setSelectedTourId,
    decks,
    activities,
    loadingTours,
    loadingTargets,
    error,
    loadTours,
    loadTourTargets,
  } = useOnboardTourConfig();

  const [activeTab, setActiveTab] = useState("PATROL");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedAssignment, setSelectedAssignment] = useState(null);
  const [assignmentType, setAssignmentType] = useState("PATROL");

  const openAssignmentModal = (assignment, type) => {
    setSelectedAssignment(assignment);
    setAssignmentType(type);
    setModalOpen(true);
  };

  const closeAssignmentModal = () => {
    setModalOpen(false);
    setSelectedAssignment(null);
  };

  const handleRefresh = async () => {
    await loadTours();
    await loadTourTargets();
  };

  return (
    <div className="onboard-config-page">
      <div className="onboard-page-header">
        <div>
          <span className="onboard-page-eyebrow">ONBOARD MANAGEMENT</span>

          <h1>Cấu hình nhân sự ONBOARD</h1>

          <p>
            Phân công nhân viên tuần tra và phụ trách các hoạt động trên du
            thuyền.
          </p>
        </div>

        <button
          type="button"
          className="onboard-button onboard-button-outline"
          onClick={handleRefresh}
          disabled={loadingTours || loadingTargets}
        >
          ↻ Làm mới
        </button>
      </div>

      {error && (
        <div className="onboard-alert onboard-alert-error">{error}</div>
      )}

      <div className="onboard-tour-selector">
        <div>
          <label htmlFor="onboard-tour-select">Chọn Tour</label>

          <select
            id="onboard-tour-select"
            value={selectedTourId}
            onChange={(event) => setSelectedTourId(event.target.value)}
            disabled={loadingTours || tours.length === 0}
          >
            {tours.length === 0 && (
              <option value="">Không có Tour phù hợp</option>
            )}

            {tours.map((tour) => (
              <option value={tour.id} key={tour.id}>
                {tour.code} - {tour.name}
              </option>
            ))}
          </select>
        </div>

        {selectedTour && (
          <div className="onboard-tour-meta">
            <div>
              <span>Du thuyền</span>
              <strong>{selectedTour.cruiseName || "Chưa xác định"}</strong>
            </div>

            <div>
              <span>Trạng thái</span>
              <strong>{selectedTour.statusTrip}</strong>
            </div>

            <div>
              <span>Thời gian</span>
              <strong>
                {selectedTour.startDate || "--"} →{" "}
                {selectedTour.endDate || "--"}
              </strong>
            </div>
          </div>
        )}
      </div>

      {loadingTours ? (
        <div className="onboard-page-loading">Đang tải danh sách Tour...</div>
      ) : !selectedTour ? (
        <div className="onboard-page-empty">
          Không có Tour APPROVED hoặc READY đã được gán du thuyền.
        </div>
      ) : (
        <>
          <div className="onboard-tabs">
            <button
              type="button"
              className={activeTab === "PATROL" ? "active" : ""}
              onClick={() => setActiveTab("PATROL")}
            >
              <span>🛳️</span>
              Tuần tra
              <span className="onboard-tab-count">{decks.length}</span>
            </button>

            <button
              type="button"
              className={activeTab === "ACTIVITY_CRUISE" ? "active" : ""}
              onClick={() => setActiveTab("ACTIVITY_CRUISE")}
            >
              <span>🎯</span>
              Hoạt động trên tàu
              <span className="onboard-tab-count">{activities.length}</span>
            </button>
          </div>

          <div className="onboard-tab-content">
            {activeTab === "PATROL" ? (
              <>
                <div className="onboard-content-heading">
                  <div>
                    <h2>Phân công tuần tra</h2>
                    <p>
                      Chọn tầng boong và phân công nhân viên phụ trách tuần tra.
                    </p>
                  </div>
                </div>

                <PatrolAssignmentTable
                  tour={selectedTour}
                  decks={decks}
                  loading={loadingTargets}
                  onAssign={(assignment) =>
                    openAssignmentModal(assignment, "PATROL")
                  }
                />
              </>
            ) : (
              <>
                <div className="onboard-content-heading">
                  <div>
                    <h2>Phân công hoạt động trên tàu</h2>
                    <p>
                      Chọn hoạt động đã được cấu hình và phân công nhân viên phụ
                      trách.
                    </p>
                  </div>
                </div>

                <ActivityCruiseAssignmentTable
                  tour={selectedTour}
                  activities={activities}
                  loading={loadingTargets}
                  onAssign={(assignment) =>
                    openAssignmentModal(assignment, "ACTIVITY_CRUISE")
                  }
                />
              </>
            )}
          </div>
        </>
      )}

      <OnboardStaffAssignmentModal
        open={modalOpen}
        assignment={selectedAssignment}
        assignmentType={assignmentType}
        onClose={closeAssignmentModal}
      />
    </div>
  );
}

export default OnboardTourConfig;
