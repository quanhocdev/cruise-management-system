// src/modules/shore/components/shore-tour-manager/TourCompletionList.jsx
// (FILE MỚI) Danh sách card từng Tour + nút "Hoàn thành".
// CSS riêng: styles/TourCompletionList.css

import {
  RefreshCw,
  CheckCircle2,
  Clock3,
  Calendar,
  Compass,
} from "lucide-react";

import "../../styles/TourCompletionList.css";

function TourCompletionCard({
  tourId,
  stopCount,
  masterTour,
  isCompleted,
  canComplete,
  isProcessing,
  onComplete,
}) {
  return (
    <div
      className={`shore-manager-tour-completion-card ${
        isCompleted ? "ready" : ""
      }`}
    >
      <div className="shore-manager-tour-completion-info">
        <div className="shore-manager-tour-completion-icon">
          <Compass size={18} />
        </div>

        <div>
          <div className="shore-manager-tour-completion-title">
            <strong>{masterTour?.name || `Tour ID: ${tourId}`}</strong>
            {masterTour?.code && (
              <span className="shore-manager-tour-code-badge">
                {masterTour.code}
              </span>
            )}
          </div>

          <small className="shore-manager-tour-completion-date">
            <Calendar size={13} />
            {masterTour?.startDate && masterTour?.endDate
              ? `${masterTour.startDate} → ${masterTour.endDate}`
              : `Đồng bộ: ${stopCount} điểm dừng`}
          </small>
        </div>
      </div>

      <div className="shore-manager-tour-completion-status">
        {isCompleted ? (
          <>
            <CheckCircle2 size={17} />
            <span>Đã hoàn thành cấu hình</span>
          </>
        ) : canComplete ? (
          <>
            <CheckCircle2 size={17} />
            <span>Đã đủ điều kiện bấm hoàn thành</span>
          </>
        ) : (
          <>
            <Clock3 size={17} />
            <span>Chưa cấu hình xong các điểm dừng</span>
          </>
        )}
      </div>

      {isCompleted ? (
        <div className="shore-manager-tour-completed-badge">
          <CheckCircle2 size={16} />
          <span>Đã hoàn thành</span>
        </div>
      ) : (
        <button
          type="button"
          className="shore-manager-tour-complete-button"
          disabled={isProcessing || !canComplete}
          onClick={() => onComplete(tourId)}
        >
          {isProcessing ? (
            <>
              <RefreshCw size={16} className="shore-manager-tour-spinner" />
              <span>Đang xử lý...</span>
            </>
          ) : (
            <>
              <CheckCircle2 size={16} />
              <span>Hoàn thành</span>
            </>
          )}
        </button>
      )}
    </div>
  );
}

function TourCompletionList({
  tourGroups,
  masterToursMap,
  completedTourIds,
  completeLoading,
  canCompleteTour,
  onComplete,
}) {
  return (
    <div className="shore-manager-tour-completion-list">
      {[...tourGroups.entries()].map(([tourId, tours]) => (
        <TourCompletionCard
          key={tourId}
          tourId={tourId}
          stopCount={tours.length}
          masterTour={masterToursMap.get(tourId)}
          isCompleted={completedTourIds.has(tourId)}
          canComplete={canCompleteTour(tourId)}
          isProcessing={completeLoading === tourId}
          onComplete={onComplete}
        />
      ))}
    </div>
  );
}

export default TourCompletionList;
