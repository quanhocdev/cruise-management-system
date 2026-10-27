// src/modules/operation/pages/ManagerTour.jsx
// (SỬA file có sẵn) Chỉ ghép UI, logic nằm trong hooks/useManagerTour.js

import { useNavigate } from "react-router-dom";
import { CheckCircle, AlertCircle } from "lucide-react";

import useManagerTour from "../hooks/useManagerTour";

import OperationTourHeaderToolbar from "../components/OperationTourHeaderToolbar";
import OperationTourTable from "../components/OperationTourTable";
import CruiseSelectModal from "../components/CruiseSelectModal";
import CruiseAreaAssignmentModal from "../components/CruiseAreaAssignmentModal";

import "../styles/ManagerTour.css";

function ManagerTour() {
  const navigate = useNavigate();
  const m = useManagerTour();

  const handleViewTour = (tour) => {
    const targetId = tour.id || tour.tourId || tour.tripId;
    navigate(`/operation/tour-configuration?tourId=${targetId}`);
  };

  return (
    <div className="operation-tour-page">
      <OperationTourHeaderToolbar
        tourMode={m.tourMode}
        statusCounts={m.statusCounts}
        onChangeMode={m.setTourMode}
        keyword={m.filter.keyword}
        startDate={m.filter.startDate}
        endDate={m.filter.endDate}
        onKeywordChange={m.filter.setKeyword}
        onStartDateChange={m.filter.setStartDate}
        onEndDateChange={m.filter.setEndDate}
        onClearFilter={m.filter.clear}
      />

      {m.success && (
        <div className="operation-tour-success">
          <CheckCircle size={18} />
          <span>{m.success}</span>
        </div>
      )}

      {m.error && (
        <div className="operation-tour-error">
          <AlertCircle size={18} />
          <span>{m.error}</span>
        </div>
      )}

      <div className="operation-tour-list-header">
        <div>
          <h2>Danh sách Tour ({m.tourMode})</h2>
          <span>
            Hiển thị {m.filteredTours.length} / {m.currentTours.length} Tour
          </span>
        </div>
      </div>

      <div className="operation-tour-content">
        <OperationTourTable
          tours={m.filteredTours}
          loading={m.toursLoading}
          mode={m.tourMode}
          onView={handleViewTour}
          onSelectCruise={m.handleSelectCruise}
          onAssignArea={m.handleAssignArea}
          onApprove={m.handleApproveTour}
          onReject={m.handleRejectTour}
        />
      </div>

      <CruiseSelectModal {...m.cruiseModalProps} />

      <CruiseAreaAssignmentModal {...m.areaModalProps} />
    </div>
  );
}

export default ManagerTour;
