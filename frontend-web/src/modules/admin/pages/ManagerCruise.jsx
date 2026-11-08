// src/modules/admin/pages/ManagerCruise.jsx

import { useMemo, useState } from "react";
import { Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";

import useCruises from "../hooks/useCruises";
import CruiseList from "../components/cruise/CruiseList";
import CruiseFormModal from "../components/cruise/CruiseFormModal";
import CruiseDeleteModal from "../components/cruise/CruiseDeleteModal";
import {
  CruiseStats,
  CruiseToolbar,
} from "../components/cruise/CruiseOverview";

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

  const [keyword, setKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [formState, setFormState] = useState({ open: false, cruise: null });
  const [saving, setSaving] = useState(false);

  const [cruiseToDelete, setCruiseToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  // ---------- LỌC ----------
  const counts = useMemo(
    () => ({
      ALL: cruises.length,
      ACTIVE: cruises.filter((c) => c.status === "ACTIVE").length,
      INACTIVE: cruises.filter((c) => c.status !== "ACTIVE").length,
    }),
    [cruises],
  );

  const filteredCruises = useMemo(() => {
    const query = keyword.trim().toLowerCase();

    return cruises.filter((cruise) => {
      if (statusFilter === "ACTIVE" && cruise.status !== "ACTIVE") return false;
      if (statusFilter === "INACTIVE" && cruise.status === "ACTIVE")
        return false;

      if (!query) return true;

      return (
        String(cruise.name || "")
          .toLowerCase()
          .includes(query) ||
        String(cruise.code || "")
          .toLowerCase()
          .includes(query)
      );
    });
  }, [cruises, keyword, statusFilter]);

  const isFiltered = keyword.trim() !== "" || statusFilter !== "ALL";

  const clearFilter = () => {
    setKeyword("");
    setStatusFilter("ALL");
  };

  // ---------- FORM ----------
  const openForm = (cruise = null) => {
    setError("");
    setSuccess("");
    setFormState({ open: true, cruise });
  };

  const closeForm = () => {
    if (saving) return;
    setFormState((previous) => ({ ...previous, open: false }));
  };

  const handleSubmit = async (formData) => {
    setSaving(true);

    try {
      const { cruise } = formState;

      const result = cruise
        ? await updateCruise(cruise.id, formData)
        : await createCruise(formData);

      if (result) setFormState((previous) => ({ ...previous, open: false }));
    } finally {
      setSaving(false);
    }
  };

  // ---------- XÓA ----------
  const handleConfirmDelete = async () => {
    setDeleting(true);

    try {
      await deleteCruise(cruiseToDelete.id);
    } finally {
      setDeleting(false);
      setCruiseToDelete(null);
    }
  };

  return (
    <div className="mc-page container-fluid py-4">
      <header className="mc-header">
        <div>
          <h2>Quản lý du thuyền</h2>
          <p>
            Quản lý thông tin, sức chứa và trạng thái các du thuyền trong hệ
            thống.
          </p>
        </div>

        <button
          type="button"
          className="mc-btn mc-btn--primary"
          onClick={() => openForm()}
        >
          <Plus size={18} />
          Thêm du thuyền
        </button>
      </header>

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {error && !formState.open && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <CruiseStats cruises={cruises} />

      <CruiseToolbar
        keyword={keyword}
        onKeywordChange={setKeyword}
        statusFilter={statusFilter}
        onStatusChange={setStatusFilter}
        counts={counts}
        shown={filteredCruises.length}
        total={cruises.length}
      />

      <CruiseList
        cruises={filteredCruises}
        loading={loading}
        isFiltered={isFiltered}
        onEdit={openForm}
        onDelete={setCruiseToDelete}
        onManageDecks={(cruise) =>
          navigate(`/admin/cruises/${cruise.id}/decks`)
        }
        onCreate={() => openForm()}
        onClearFilter={clearFilter}
      />

      <CruiseFormModal
        show={formState.open}
        cruise={formState.cruise}
        saving={saving}
        error={error}
        onClose={closeForm}
        onSubmit={handleSubmit}
      />

      <CruiseDeleteModal
        cruise={cruiseToDelete}
        loading={deleting}
        onConfirm={handleConfirmDelete}
        onClose={() => setCruiseToDelete(null)}
      />
    </div>
  );
}
