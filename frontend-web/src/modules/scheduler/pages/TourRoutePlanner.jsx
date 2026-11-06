// src/modules/scheduler/pages/TourRoutePlanner.jsx

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, X } from "lucide-react";

import useSchedulerRoute from "../hooks/useSchedulerRoute";

import RoutePlannerSection from "../components/tour/RoutePlannerSection";
import TourFormModal from "../components/tour/TourFormModal";

import "../styles/TourRoutePlanner.css";

export default function TourRoutePlanner() {
  const navigate = useNavigate();
  const route = useSchedulerRoute();

  const [showForm, setShowForm] = useState(false);
  const [formLoading, setFormLoading] = useState(false);
  const [error, setError] = useState("");

  const goBack = () => navigate("/scheduler/tours");

  const openCreateForm = () => {
    if (!route.departurePortId || !route.destinationPortId) {
      setError("Vui lòng chọn cảng khởi hành và cảng đến.");
      return;
    }

    if (!route.routePreview) {
      setError("Vui lòng tính tuyến đường trước khi tạo Tour.");
      return;
    }

    setError("");
    setShowForm(true);
  };

  const handleCloseForm = () => {
    if (formLoading) return;
    setShowForm(false);
  };

  const handleSubmitForm = async (formData) => {
    setFormLoading(true);
    setError("");

    try {
      // Tạo Tour và lưu tuyến đường đã tính (hook tự xóa lựa chọn sau khi xong)
      await route.handleCreateTourWithRoute(formData);

      navigate("/scheduler/tours", {
        state: { successMessage: "Đã tạo Tour và lưu tuyến đường." },
      });
    } catch (err) {
      console.error("Không thể tạo Tour:", err);
      setShowForm(false);
      setError(err.message || "Không thể tạo Tour.");
    } finally {
      setFormLoading(false);
    }
  };

  return (
    <div className="tour-planner-page">
      <div className="tour-planner-header">
        <button
          type="button"
          className="tour-planner-back-btn"
          onClick={goBack}
          disabled={formLoading}
        >
          <ArrowLeft size={16} />
          Danh sách Tour
        </button>

        <div>
          <h2>Tạo Tour mới</h2>
          <p>
            Chọn cảng khởi hành và cảng đến trên bản đồ, tính tuyến đường rồi
            nhập thông tin Tour.
          </p>
        </div>
      </div>

      {error && (
        <div className="tour-planner-alert">
          <span>{error}</span>
          <button
            type="button"
            onClick={() => setError("")}
            aria-label="Đóng thông báo lỗi"
          >
            <X size={16} />
          </button>
        </div>
      )}

      <RoutePlannerSection
        ports={route.ports}
        departurePort={route.departurePort}
        destinationPort={route.destinationPort}
        departurePortId={route.departurePortId}
        destinationPortId={route.destinationPortId}
        routePreview={route.routePreview}
        loadingPorts={route.loadingPorts}
        routeLoading={route.routeLoading}
        routeError={route.routeError}
        onSelectPort={route.handleSelectPort}
        onPreview={route.handlePreviewRoute}
        onClear={route.clearRouteSelection}
        onCreate={openCreateForm}
      />

      <TourFormModal
        open={showForm}
        tour={null}
        loading={formLoading}
        onClose={handleCloseForm}
        onSubmit={handleSubmitForm}
      />
    </div>
  );
}
