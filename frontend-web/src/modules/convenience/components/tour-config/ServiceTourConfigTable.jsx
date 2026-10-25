// src/modules/convenience/components/tour-config/ServiceTourConfigTable.jsx

import React, { useMemo, useState } from "react";
import { AlertCircle, RefreshCw, Wrench } from "lucide-react";

import useServiceTour from "../../hooks/useServiceTour";
import "../../styles/tour-config/TourConfigTable.css";

import ServiceTourConfigModal from "./ServiceTourConfigModal";
import ConvenienceStaffAssignmentModal from "./ConvenienceStaffAssignmentModal";

import {
  AreaCell,
  AssignStaffAction,
  ConfigAction,
  StatusTabs,
  TourCell,
  TourCompleteBox,
  getStatusLabel,
} from "./TourConfigShared";

const PREFIX = "tour-config";

const STATUSES = [
  "ALL",
  "WAITING_CONFIG",
  "CONFIGURED",
  "NOT_STARTED",
  "IN_PROGRESS",
  "COMPLETED",
];

const ServiceTourConfigTable = () => {
  const {
    serviceTours,
    tourSummaries,
    loading,
    error,
    completing,
    completeError,
    loadServiceTours,
    configureService,
    updateService,
    completeTourConfiguration,
  } = useServiceTour();

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [selectedAssignment, setSelectedAssignment] = useState(null);
  const [assignmentTarget, setAssignmentTarget] = useState(null);

  const filteredTours = useMemo(() => {
    const list = serviceTours || [];

    if (statusFilter === "ALL") {
      return list;
    }

    return list.filter((item) => item.status === statusFilter);
  }, [serviceTours, statusFilter]);

  const handleCloseModal = () => {
    if (loading) return;
    setSelectedAssignment(null);
  };

  const handleSubmit = async (assignmentId, payload) => {
    const assignment = serviceTours.find((item) => item.id === assignmentId);

    if (!assignment) return;

    if (assignment.status === "WAITING_CONFIG") {
      await configureService(assignmentId, payload);
    } else if (assignment.status === "CONFIGURED") {
      await updateService(assignmentId, payload);
    }

    setSelectedAssignment(null);
  };

  if (loading && filteredTours.length === 0) {
    return (
      <div className={`${PREFIX}-loading`}>
        <RefreshCw size={20} className="spin" />
        <span>Đang tải danh sách dịch vụ...</span>
      </div>
    );
  }

  return (
    <>
      <div className={`${PREFIX}-toolbar`}>
        <div>
          <h2>
            <Wrench size={20} />
            Dịch vụ của Tour
          </h2>
          <p>Cấu hình các dịch vụ được Operation phân công cho từng Tour.</p>
        </div>

        <button
          type="button"
          className={`${PREFIX}-refresh`}
          onClick={loadServiceTours}
          disabled={loading}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />
          <span>Làm mới</span>
        </button>
      </div>

      {error && (
        <div className={`${PREFIX}-error`}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <TourCompleteBox
        prefix={PREFIX}
        noun="dịch vụ"
        tourSummaries={tourSummaries}
        completing={completing}
        completeError={completeError}
        onComplete={completeTourConfiguration}
      />

      <StatusTabs
        prefix={PREFIX}
        statuses={STATUSES}
        value={statusFilter}
        onChange={setStatusFilter}
      />

      {filteredTours.length === 0 && !error ? (
        <div className={`${PREFIX}-empty`}>
          <Wrench size={32} />
          <h3>Không có dịch vụ nào</h3>
          <p>Không có dịch vụ nào khớp với bộ lọc hiện tại.</p>
        </div>
      ) : (
        <div className={`${PREFIX}-table-wrapper`}>
          <table className={`${PREFIX}-table`}>
            <thead>
              <tr>
                <th>STT</th>
                <th>Tour</th>
                <th>Khu vực</th>
                <th>Dịch vụ</th>
                <th>Khách tối đa</th>
                <th>Thời lượng</th>
                <th>Trạng thái</th>
                <th style={{ textAlign: "center" }}>Thao tác</th>
              </tr>
            </thead>

            <tbody>
              {filteredTours.map((assignment, index) => (
                <tr key={assignment.id}>
                  <td>{index + 1}</td>

                  <td>
                    <TourCell prefix={PREFIX} assignment={assignment} />
                  </td>

                  <td>
                    <AreaCell prefix={PREFIX} assignment={assignment} />
                  </td>

                  <td>
                    {assignment.serviceName ? (
                      <div className={`${PREFIX}-item`}>
                        <strong>{assignment.serviceName}</strong>

                        {assignment.serviceDescription && (
                          <span>{assignment.serviceDescription}</span>
                        )}
                      </div>
                    ) : (
                      <span className={`${PREFIX}-muted`}>
                        Chưa chọn dịch vụ
                      </span>
                    )}
                  </td>

                  <td>
                    {assignment.maxPassengers != null ? (
                      <span>{assignment.maxPassengers}</span>
                    ) : (
                      <span className={`${PREFIX}-muted`}>—</span>
                    )}
                  </td>

                  <td>
                    {assignment.durationMinutes != null ? (
                      <span>{assignment.durationMinutes} phút</span>
                    ) : (
                      <span className={`${PREFIX}-unlimited`}>
                        Không giới hạn
                      </span>
                    )}
                  </td>

                  <td>
                    <span
                      className={`${PREFIX}-status ${String(
                        assignment.status || "",
                      ).toLowerCase()}`}
                    >
                      {getStatusLabel(assignment.status)}
                    </span>
                  </td>

                  <td>
                    <div
                      style={{
                        display: "flex",
                        gap: "8px",
                        justifyContent: "center",
                      }}
                    >
                      <ConfigAction
                        prefix={PREFIX}
                        assignment={assignment}
                        onOpen={setSelectedAssignment}
                      />

                      <AssignStaffAction
                        prefix={PREFIX}
                        onOpen={() => setAssignmentTarget(assignment)}
                      />
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selectedAssignment && (
        <ServiceTourConfigModal
          assignment={selectedAssignment}
          onClose={handleCloseModal}
          onSubmit={handleSubmit}
          submitting={loading}
        />
      )}

      {assignmentTarget && (
        <ConvenienceStaffAssignmentModal
          assignment={assignmentTarget}
          assignmentType="SERVICE"
          onClose={() => setAssignmentTarget(null)}
        />
      )}
    </>
  );
};

export default ServiceTourConfigTable;
