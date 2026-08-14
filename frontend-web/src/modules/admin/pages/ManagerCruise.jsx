// src/modules/admin/pages/ManagerCruise.jsx
import { useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

import useCruises from "../hooks/useCruises";
import CruiseTable from "../components/cruise/CruiseTable";
import CruiseFormModal from "../components/cruise/CruiseFormModal";

import "../styles/cruise/ManagerCruise.css";

export default function ManagerCruise() {
  const navigate = useNavigate();

  const {
    cruises,
    loading,
    error,
    success,
    setError,
    setSuccess,
    createCruise,
    updateCruise,
    deleteCruise,
  } = useCruises();

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingCruise, setEditingCruise] = useState(null);

  const [form, setForm] = useState({
    name: "",
    code: "",
    description: "",
    maxPassengers: "",
    image: null,
    status: "ACTIVE",
  });

  const handleOpenCreate = () => {
    setEditingCruise(null);

    setForm({
      name: "",
      code: "",
      description: "",
      maxPassengers: "",
      image: null,
      status: "ACTIVE",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (cruise) => {
    setEditingCruise(cruise);

    setForm({
      name: cruise.name || "",
      code: cruise.code || "",
      description: cruise.description || "",
      maxPassengers: cruise.maxPassengers ?? "",
      image: null,
      status: cruise.status || "ACTIVE",
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
  };

  const handleChange = (event) => {
    const { name, value, files } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: files ? files[0] : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!form.name.trim()) {
      setError("Vui lòng nhập tên du thuyền.");
      return;
    }

    if (!form.code.trim()) {
      setError("Vui lòng nhập mã du thuyền.");
      return;
    }

    if (!form.maxPassengers || Number(form.maxPassengers) <= 0) {
      setError("Số hành khách tối đa phải lớn hơn 0.");
      return;
    }

    const formData = new FormData();

    formData.append("name", form.name.trim());
    formData.append("code", form.code.trim());
    formData.append("description", form.description.trim());
    formData.append("maxPassengers", Number(form.maxPassengers));

    if (form.image) {
      formData.append("image", form.image);
    }

    if (editingCruise) {
      formData.append("status", form.status);
    }

    setSaving(true);

    try {
      let result;

      if (!editingCruise) {
        result = await createCruise(formData);
      } else {
        result = await updateCruise(editingCruise.id, formData);
      }

      if (result) {
        setShowModal(false);
      }
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (cruise) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa du thuyền "${cruise.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteCruise(cruise.id);
  };

  const handleManageDecks = (cruise) => {
    navigate(`/admin/cruises/${cruise.id}/decks`);
  };

  return (
    <div className="manager-cruise-page container-fluid py-4">
      <div className="manager-cruise-header mb-4">
        <div>
          <h2 className="manager-cruise-title">Quản lý du thuyền</h2>

          <p className="manager-cruise-description">
            Quản lý thông tin các du thuyền trong hệ thống.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Thêm du thuyền
        </Button>
      </div>

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <CruiseTable
        cruises={cruises}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
        onManageDecks={handleManageDecks}
      />

      <CruiseFormModal
        show={showModal}
        saving={saving}
        editingCruise={editingCruise}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
