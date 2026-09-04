// src/modules/operation/pages/ManagerTourPackages.jsx

import React, { useState } from "react";
import { ArrowLeft, Plus, RefreshCw, AlertCircle, Package } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";
import TourPackageTable from "../components/packages/TourPackageTable";
import TourPackageModal from "../components/packages/TourPackageModal";
import { useTourPackages } from "../hooks/useTourPackages";
import "../styles/packages/ManagerTourPackages.css";

const ManagerTourPackages = () => {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");
  const navigate = useNavigate();

  const {
    packages,
    roomTypes,
    loading,
    error,
    refreshPackages,
    createPackage,
    patchPackage,
    deletePackage,
  } = useTourPackages(tourId);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPackage, setEditingPackage] = useState(null);

  const handleBack = () => {
    navigate(`/operation/tour-configuration?tourId=${tourId}`);
  };

  const handleOpenCreate = () => {
    setEditingPackage(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (pkg) => {
    setEditingPackage(pkg);
    setIsModalOpen(true);
  };

  const handleSavePackage = async (formData) => {
    if (editingPackage) {
      await patchPackage(editingPackage.id, formData);
    } else {
      await createPackage(formData);
    }
    setIsModalOpen(false);
  };

  const handleDelete = async (packageId) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa gói tour này không?")) {
      await deletePackage(packageId);
    }
  };

  if (!tourId) {
    return (
      <div className="manager-tour-packages-page">
        <div className="manager-tour-packages-error">
          <AlertCircle size={24} />
          <div>
            <strong>Thiếu Tour ID</strong>
            <p>Không xác định được Tour để quản lý gói.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="manager-tour-packages-page">
      <div className="manager-tour-packages-header">
        <div className="manager-tour-packages-header-left">
          <button
            type="button"
            className="manager-tour-packages-back"
            onClick={handleBack}
          >
            <ArrowLeft size={18} />
            Quay lại Cấu hình
          </button>
          <div className="manager-tour-packages-title">
            <Package size={24} />
            <div>
              <h1>Quản lý Gói Tour</h1>
              <p>
                Cấu hình các gói dịch vụ, sản phẩm và hoạt động cho tour này.
              </p>
            </div>
          </div>
        </div>

        <div className="manager-tour-packages-actions">
          <button
            type="button"
            className="manager-tour-packages-refresh"
            onClick={refreshPackages}
            disabled={loading}
          >
            <RefreshCw size={16} className={loading ? "spin" : ""} />
            Làm mới
          </button>
          <button
            type="button"
            className="manager-tour-packages-add"
            onClick={handleOpenCreate}
          >
            <Plus size={18} />
            Thêm gói mới
          </button>
        </div>
      </div>

      {error && (
        <div className="manager-tour-packages-alert-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <div className="manager-tour-packages-content">
        <TourPackageTable
          packages={packages}
          loading={loading}
          onEdit={handleOpenEdit}
          onDelete={handleDelete}
        />
      </div>

      {isModalOpen && (
        <TourPackageModal
          tourId={tourId}
          roomTypes={roomTypes}
          initialData={editingPackage}
          onClose={() => setIsModalOpen(false)}
          onSave={handleSavePackage}
        />
      )}
    </div>
  );
};

export default ManagerTourPackages;
