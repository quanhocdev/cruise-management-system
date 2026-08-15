// src/modules/admin/pages/ManagerPort.jsx

import { useCallback, useState } from "react";

import { Alert, Button, Spinner } from "react-bootstrap";

import { List, Map as MapIcon, Plus } from "lucide-react";

import usePorts from "../hooks/usePorts";

import PortMap from "../components/port/PortMap";
import PortSearch from "../components/port/PortSearch";
import PortFormModal from "../components/port/PortFormModal";
import PortTable from "../components/port/PortTable";

import "../styles/ManagerPort.css";

export default function ManagerPort() {
  const {
    ports,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    createPort,
    updatePort,
    deactivatePort,
  } = usePorts();

  // =====================================================
  // VIEW
  // =====================================================

  const [activeView, setActiveView] = useState("map");

  // =====================================================
  // SELECTED LOCATION
  // =====================================================

  const [selectedLocation, setSelectedLocation] = useState(null);

  // =====================================================
  // MODAL
  // =====================================================

  const [showFormModal, setShowFormModal] = useState(false);

  const [editingPort, setEditingPort] = useState(null);

  // =====================================================
  // MAP LOCATION SELECT
  // =====================================================

  const handleMapLocationSelect = useCallback((location) => {
    setSelectedLocation(location);
  }, []);

  // =====================================================
  // SEARCH LOCATION SELECT
  // =====================================================

  const handleSearchLocationSelect = (location) => {
    setSelectedLocation(location);
  };

  // =====================================================
  // CREATE AT SELECTED LOCATION
  // =====================================================

  const handleCreateAtLocation = () => {
    if (!selectedLocation) {
      setError("Vui lòng chọn một vị trí trên bản đồ.");

      return;
    }

    setError("");
    setSuccess("");

    setEditingPort(null);
    setShowFormModal(true);
  };

  // =====================================================
  // CREATE MANUALLY
  // =====================================================

  const handleOpenManualCreate = () => {
    setError("");
    setSuccess("");

    setSelectedLocation(null);
    setEditingPort(null);

    setShowFormModal(true);
  };

  // =====================================================
  // EDIT
  // =====================================================

  const handleEditPort = (port) => {
    setError("");
    setSuccess("");

    setEditingPort(port);

    setSelectedLocation({
      latitude: port.latitude,
      longitude: port.longitude,

      placeName: port.address || `${port.city || ""}, ${port.country || ""}`,
    });

    setShowFormModal(true);
  };

  // =====================================================
  // CLOSE FORM
  // =====================================================

  const handleCloseForm = () => {
    if (saving) {
      return;
    }

    setShowFormModal(false);
    setEditingPort(null);
  };

  // =====================================================
  // SUBMIT FORM
  // =====================================================

  const handleSubmitPort = async (data) => {
    let result = null;

    if (editingPort) {
      result = await updatePort(editingPort.id, data);
    } else {
      result = await createPort(data);
    }

    if (!result) {
      return;
    }

    setShowFormModal(false);
    setEditingPort(null);

    setSelectedLocation({
      latitude: result.latitude,
      longitude: result.longitude,

      placeName:
        result.address || `${result.city || ""}, ${result.country || ""}`,
    });

    // Đưa map về view
    setActiveView("map");
  };

  // =====================================================
  // DELETE
  // =====================================================

  const handleDeletePort = async (port) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn vô hiệu hóa cảng "${port.name}" không?\n\n` +
        "Cảng sẽ được chuyển sang trạng thái ngừng hoạt động.",
    );

    if (!confirmed) {
      return;
    }

    await deactivatePort(port.id);
  };

  // =====================================================
  // CLEAR MESSAGES
  // =====================================================

  const handleCloseSuccess = () => {
    setSuccess("");
  };

  const handleCloseError = () => {
    setError("");
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="manager-port-page">
      {/* =================================================
          HEADER
         ================================================= */}

      <div className="manager-port-header">
        <div>
          <h2 className="manager-port-title">Quản lý cảng</h2>

          <p className="manager-port-description">
            Quản lý vị trí, thông tin và trạng thái các cảng trong hệ thống.
          </p>
        </div>

        <div className="manager-port-view-switch">
          <button
            type="button"
            className={activeView === "map" ? "active" : ""}
            onClick={() => setActiveView("map")}
          >
            <MapIcon size={17} />
            <span>Bản đồ</span>
          </button>

          <button
            type="button"
            className={activeView === "table" ? "active" : ""}
            onClick={() => setActiveView("table")}
          >
            <List size={17} />
            <span>Danh sách</span>
          </button>
        </div>
      </div>

      {/* =================================================
          ALERT
         ================================================= */}

      {success && (
        <Alert variant="success" dismissible onClose={handleCloseSuccess}>
          {success}
        </Alert>
      )}

      {error && (
        <Alert variant="danger" dismissible onClose={handleCloseError}>
          {error}
        </Alert>
      )}

      {/* =================================================
          MAP VIEW
         ================================================= */}

      {activeView === "map" && (
        <div className="manager-port-map-section">
          <div className="manager-port-map-search">
            <PortSearch
              onLocationSelect={handleSearchLocationSelect}
              disabled={saving}
            />
          </div>

          <PortMap
            ports={ports}
            selectedLocation={selectedLocation}
            onMapLocationSelect={handleMapLocationSelect}
            onEditPort={handleEditPort}
          />

          {/* SELECTED LOCATION PANEL */}

          {selectedLocation && (
            <div className="port-location-panel">
              <div className="port-location-panel-content">
                <div className="port-location-panel-icon">📍</div>

                <div className="port-location-panel-info">
                  <div className="port-location-panel-title">
                    Vị trí đang chọn
                  </div>

                  {selectedLocation.placeName && (
                    <div className="port-location-panel-place">
                      {selectedLocation.placeName}
                    </div>
                  )}

                  <div className="port-location-panel-coordinates">
                    <span>
                      <strong>Latitude:</strong>{" "}
                      {Number(selectedLocation.latitude).toFixed(6)}
                    </span>

                    <span>
                      <strong>Longitude:</strong>{" "}
                      {Number(selectedLocation.longitude).toFixed(6)}
                    </span>
                  </div>
                </div>
              </div>

              <Button
                variant="primary"
                onClick={handleCreateAtLocation}
                disabled={saving}
              >
                <Plus size={17} />

                <span>Tạo cảng tại đây</span>
              </Button>
            </div>
          )}

          {/* MAP LOADING */}

          {loading && (
            <div className="manager-port-map-loading">
              <Spinner animation="border" size="sm" />

              <span>Đang tải danh sách cảng...</span>
            </div>
          )}
        </div>
      )}

      {/* =================================================
          TABLE VIEW
         ================================================= */}

      {activeView === "table" && (
        <div className="manager-port-table-section">
          <div className="manager-port-table-header">
            <div>
              <h5>Danh sách cảng</h5>

              <p>Quản lý thông tin và trạng thái các cảng.</p>
            </div>

            <Button variant="primary" onClick={handleOpenManualCreate}>
              <Plus size={17} />

              <span>Tạo cảng</span>
            </Button>
          </div>

          <PortTable
            ports={ports}
            loading={loading}
            onEdit={handleEditPort}
            onDelete={handleDeletePort}
          />
        </div>
      )}

      {/* =================================================
          FORM MODAL
         ================================================= */}

      <PortFormModal
        show={showFormModal}
        saving={saving}
        editingPort={editingPort}
        selectedLocation={selectedLocation}
        error={error}
        onClose={handleCloseForm}
        onSubmit={handleSubmitPort}
      />
    </div>
  );
}
