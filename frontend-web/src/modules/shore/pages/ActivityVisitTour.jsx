// src/modules/shore/pages/ActivityVisitTour.jsx
// (SỬA file có sẵn) Chỉ ghép UI, mọi logic nằm trong useShoreTourManager.

import { RefreshCw } from "lucide-react";
import { useNavigate } from "react-router-dom";

import "../styles/ActivityVisitTour.css";

import useShoreTourManager from "../hooks/useShoreTourManager";

import ShoreTourTable from "../components/ShoreTourTable";
import VisitTourFormModal from "../components/visit-tour/VisitTourFormModal";
import ShoreTourHeader from "../components/shore-tour-manager/ShoreTourHeader";
import ShoreTourFilterBar from "../components/shore-tour-manager/ShoreTourFilterBar";
import TourCompletionList from "../components/shore-tour-manager/TourCompletionList";

function ShoreManagerTour() {
  const navigate = useNavigate();
  const m = useShoreTourManager();

  if (m.loading) {
    return (
      <div className="shore-manager-tour">
        <div className="shore-manager-tour-loading">
          <RefreshCw size={22} className="shore-manager-tour-spinner" />
          <span>Đang tải danh sách Tour bờ...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="shore-manager-tour">
      <ShoreTourHeader
        loading={m.loading}
        onRefresh={m.reload}
        onOpenHistory={() => navigate("/shore/activity-visit-history")}
        error={m.error}
        completeError={m.completeError}
        onCloseCompleteError={m.clearCompleteError}
        successMessage={m.successMessage}
        onCloseSuccess={m.clearSuccessMessage}
      />

      {!m.error && (
        <>
          <ShoreTourFilterBar
            summary={m.summary}
            statusFilter={m.statusFilter}
            onStatusChange={m.setStatusFilter}
            shown={m.filteredVisitTours.length}
            total={m.visitTours.length}
          />

          <TourCompletionList
            tourGroups={m.tourGroups}
            masterToursMap={m.masterToursMap}
            completedTourIds={m.completedTourIds}
            completeLoading={m.completeLoading}
            canCompleteTour={m.canCompleteTour}
            onComplete={m.handleCompleteConfiguration}
          />

          <ShoreTourTable
            visitTours={m.filteredVisitTours}
            masterToursMap={m.masterToursMap}
            onConfigure={m.openConfiguration}
          />
        </>
      )}

      {m.isModalOpen && (
        <VisitTourFormModal
          visitTour={m.editingVisitTour}
          scheduleStop={m.selectedScheduleStop}
          loading={m.visitTourSaving}
          onClose={m.closeModal}
          onSubmit={m.submitVisitTour}
        />
      )}
    </div>
  );
}

export default ShoreManagerTour;
