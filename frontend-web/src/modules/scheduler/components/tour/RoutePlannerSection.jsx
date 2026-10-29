// src/modules/scheduler/components/tour/RoutePlannerSection.jsx
// (FILE MỚI) Khối "Lập tuyến đường cho Tour" của trang ManagerTour:
// bản đồ chọn cảng, kết quả tuyến đường và nút tạo Tour.
// CSS: dùng chung styles/ManagerSchedule.css (các class scheduler-route-*)

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
  return (
    <section className="scheduler-route-section">
      <div className="scheduler-route-heading">
        <div className="scheduler-route-heading-icon">
          <Route size={21} />
        </div>

        <div>
          <h3>Lập tuyến đường cho Tour</h3>
          <p>
            Chọn cảng khởi hành và cảng đến trên bản đồ để xem trước tuyến
            đường biển.
          </p>
        </div>
      </div>

      <div className="scheduler-route-layout">
        <div className="scheduler-route-map-container">
          {loadingPorts ? (
            <div className="scheduler-route-map-loading">
              <RefreshCw className="spin" size={22} />
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
                  <RefreshCw size={16} className="spin" />
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

          <div className="scheduler-route-legend">
            <div>
              <span className="scheduler-route-dot departure-dot" />
              Cảng khởi hành
            </div>
            <div>
              <span className="scheduler-route-dot destination-dot" />
              Cảng đến
            </div>
            <div>
              <span className="scheduler-route-dot normal-port-dot" />
              Cảng khác
            </div>
          </div>
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