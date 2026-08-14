import { useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

import useCruiseAreas from "../hooks/useCruiseAreas";
import CruiseAreaTable from "../components/cruise/CruiseAreaTable";
import CruiseAreaFormModal from "../components/cruise/CruiseAreaFormModal";

import "../styles/cruise/CruiseArea.css";

export default function CruiseArea() {
  const { deckId } = useParams();
  const navigate = useNavigate();

  const {
    areas,
    loading,
    error,
    success,
    setError,
    setSuccess,
    createArea,
    updateArea,
    deleteArea,
  } = useCruiseAreas(deckId);

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingArea, setEditingArea] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: "",
    status: "ACTIVE",
    image: null,
  });

  const handleOpenCreate = () => {
    setEditingArea(null);

    setForm({
      name: "",
      description: "",
      status: "ACTIVE",
      image: null,
    });

    setError("");
    setSuccess("");
    setShowModal(true);
  };

  const handleOpenEdit = (area) => {
    setEditingArea(area);

    setForm({
      name: area.name || "",
      description: area.description || "",
      status: area.status || "ACTIVE",
      image: null,
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
      setError("Vui lòng nhập tên khu vực.");
      return;
    }

    const formData = new FormData();

    formData.append("name", form.name.trim());
    formData.append("description", form.description.trim());

    if (editingArea) {
      formData.append("status", form.status);
    }

    if (form.image) {
      formData.append("image", form.image);
    }

    setSaving(true);

    try {
      let result;

      if (!editingArea) {
        result = await createArea(formData);
      } else {
        result = await updateArea(editingArea.id, formData);
      }

      if (result) {
        setShowModal(false);
      }
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (area) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa khu vực "${area.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteArea(area.id);
  };

  return (
    <div className="cruise-area-page container-fluid py-4">
      <div className="cruise-area-header mb-4">
        <div>
          <Button
            variant="link"
            className="px-0 mb-2"
            onClick={() => navigate(-1)}
          >
            ← Quay lại tầng
          </Button>

          <h2 className="cruise-area-title">Quản lý khu vực</h2>

          <p className="cruise-area-description">
            Quản lý các không gian và vị trí trên tầng của du thuyền.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Thêm khu vực
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

      <CruiseAreaTable
        areas={areas}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

      <CruiseAreaFormModal
        show={showModal}
        saving={saving}
        editingArea={editingArea}
        form={form}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        onChange={handleChange}
      />
    </div>
  );
}
