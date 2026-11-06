// src/modules/scheduler/components/tour/RoutePlannerSection.jsx

import { RefreshCw, Route, Trash2, Navigation, Plus } from "lucide-react";

import SchedulerRouteMap from "./SchedulerRouteMap";

const getPortName = (port) => {
  if (!port) return "Chưa chọn";
  return port.name || port.portName || port.code || "Cảng";
};

const formatDistance = (distance) => {
  if (distance == null || Number.isNaN(Number(distance))) return "--";

  return `${Number(distance).toLocaleString("vi-VN", {
    maximumFractionDigits: 2,
  })} km`;
};

function PortCard({ label, dotClass, port }) {
  return (
    <div className="scheduler-route-port-card">
      <div className="scheduler-route-port-label">
        <span className={`scheduler-route-dot ${dotClass}`} />
        {label}
      </div>

      <strong>{getPortName(port)}</strong>

      {port?.code && <small>{port.code}</small>}
    </div>
  );
}

function RoutePlannerSection({
  ports,
  departurePort,
  destinationPort,
  departurePortId,
  destinationPortId,
  routePreview,
  loadingPorts,
  routeLoading,
  routeError,
  onSelectPort,
  onPreview,
  onClear,
  onCreate,
}) {
  const hint = !departurePortId
    ? "Bước 1: nhấn vào một cảng trên bản đồ để chọn cảng khởi hành."
    : !destinationPortId
      ? "Bước 2: nhấn vào cảng thứ hai để chọn cảng đến."
      : !routePreview
        ? "Bước 3: nhấn “Tính tuyến đường” để xem trước tuyến."
        : "Tuyến đường đã sẵn sàng. Nhấn “Tạo Tour từ tuyến đường” để tiếp tục.";

  return (
    <section className="scheduler-route-section">
      <div className="scheduler-route-layout">
        <div className="scheduler-route-map-container">
          {loadingPorts ? (
            <div className="scheduler-route-map-loading">
              <RefreshCw className="scheduler-route-spin" size={22} />
              <span>Đang tải danh sách cảng...</span>
            </div>
          ) : (
            <SchedulerRouteMap
              ports={ports}
              departurePortId={departurePortId}
              destinationPortId={destinationPortId}
              routeGeoJson={routePreview?.geoJson || null}
              onSelectPort={onSelectPort}
            />
          )}
        </div>

        <div className="scheduler-route-sidebar">
          <p className="scheduler-route-hint">{hint}</p>

          <PortCard
            label="Cảng khởi hành"
            dotClass="departure-dot"
            port={departurePort}
          />
          <PortCard
            label="Cảng đến"
            dotClass="destination-dot"
            port={destinationPort}
          />

          <div className="scheduler-route-actions">
            <button
              type="button"
              className="scheduler-route-preview-btn"
              onClick={onPreview}
              disabled={
                !departurePortId ||
                !destinationPortId ||
                routeLoading ||
                departurePortId === destinationPortId
              }
            >
              {routeLoading ? (
                <>
                  <RefreshCw size={16} className="scheduler-route-spin" />
                  Đang tính tuyến...
                </>
              ) : (
                <>
                  <Navigation size={16} />
                  Tính tuyến đường
                </>
              )}
            </button>

            <button
              type="button"
              className="scheduler-route-clear-btn"
              onClick={onClear}
              disabled={!departurePortId && !destinationPortId}
            >
              <Trash2 size={15} />
              Xóa lựa chọn
            </button>
          </div>

          {routeError && (
            <div className="scheduler-route-error">{routeError}</div>
          )}

          {routePreview && (
            <div className="scheduler-route-result">
              <div className="scheduler-route-result-heading">
                <Route size={17} />
                Kết quả tuyến đường
              </div>

              <div className="scheduler-route-distance">
                <span>Khoảng cách tuyến đường</span>
                <strong>{formatDistance(routePreview.distanceKm)}</strong>
              </div>

              <div className="scheduler-route-distance-detail">
                <div>
                  <span>Từ cảng khởi hành</span>
                  <strong>
                    {formatDistance(routePreview.distanceFromDepartureKm)}
                  </strong>
                </div>

                <div>
                  <span>Đến cảng đích</span>
                  <strong>
                    {formatDistance(routePreview.distanceToDestinationKm)}
                  </strong>
                </div>
              </div>

              <p>
                Tuyến đường đang ở chế độ xem trước. Hệ thống chỉ lưu tuyến
                đường khi bạn xác nhận tạo Tour.
              </p>
            </div>
          )}
        </div>
      </div>

      <div className="scheduler-route-create-footer">
        <div>
          <strong>
            {routePreview
              ? "Tuyến đường đã sẵn sàng"
              : "Chưa có tuyến đường được xác nhận"}
          </strong>

          <p>
            Chỉ có thể tạo Tour sau khi chọn đủ hai cảng và tính tuyến đường
            thành công.
          </p>
        </div>

        <button
          type="button"
          className="scheduler-route-create-btn"
          onClick={onCreate}
          disabled={!routePreview || !departurePortId || !destinationPortId}
        >
          <Plus size={17} />
          Tạo Tour từ tuyến đường
        </button>
      </div>
    </section>
  );
}

export default RoutePlannerSection;
