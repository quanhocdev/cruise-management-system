// src/modules/admin/pages/CruiseDeckDetail.jsx
import { useNavigate, useParams } from "react-router-dom";
import { Compass, DoorClosed, ArrowLeft } from "lucide-react";

import "../styles/cruise/ManagerCruise.css";

export default function CruiseDeckDetail() {
  const { deckId } = useParams();
  const navigate = useNavigate();

  return (
    <div className="mc-page container-fluid py-4">
      {/* HEADER */}
      <header className="mc-header mb-4">
        <div>
          <button
            type="button"
            className="btn btn-link px-0 mb-2 text-decoration-none d-inline-flex align-items-center gap-1"
            onClick={() => navigate(-1)}
          >
            <ArrowLeft size={16} /> Quay lại danh sách tầng
          </button>
          <h2>Quản lý chi tiết tầng</h2>
          <p>
            Lựa chọn phân vùng quản lý khu vực tiện ích hoặc hệ thống phòng lưu
            trú của tầng này.
          </p>
        </div>
      </header>

      {/* ACTION CARDS GRID */}
      <div
        className="mc-grid"
        style={{ gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))" }}
      >
        {/* AREA CARD */}
        <article className="mc-card" style={{ padding: "24px" }}>
          <div className="d-flex align-items-center gap-3 mb-3">
            <span className="mc-stat-icon mc-stat-icon--blue">
              <Compass size={26} />
            </span>
            <div>
              <h3 style={{ fontSize: "1.2rem", fontWeight: "700", margin: 0 }}>
                Khu vực & Tiện ích
              </h3>
              <span className="text-muted" style={{ fontSize: "13px" }}>
                Không gian chung, nhà hàng, hồ bơi, bar...
              </span>
            </div>
          </div>

          <p
            className="mc-card-desc mb-4"
            style={{ fontSize: "14px", WebkitLineClamp: 3 }}
          >
            Quản lý các khu vực chức năng trên tầng này, thêm hình ảnh minh họa,
            thiết lập trạng thái hoạt động hoặc vô hiệu hóa.
          </p>

          <button
            type="button"
            className="mc-btn mc-btn--primary w-100"
            onClick={() => navigate(`/admin/decks/${deckId}/areas`)}
          >
            <Compass size={18} />
            Quản lý khu vực tầng
          </button>
        </article>

        {/* ROOM CARD */}
        <article className="mc-card" style={{ padding: "24px" }}>
          <div className="d-flex align-items-center gap-3 mb-3">
            <span className="mc-stat-icon mc-stat-icon--sky">
              <DoorClosed size={26} />
            </span>
            <div>
              <h3 style={{ fontSize: "1.2rem", fontWeight: "700", margin: 0 }}>
                Phòng lưu trú
              </h3>
              <span className="text-muted" style={{ fontSize: "13px" }}>
                Phòng ngủ, hạng phòng, mã phòng...
              </span>
            </div>
          </div>

          <p
            className="mc-card-desc mb-4"
            style={{ fontSize: "14px", WebkitLineClamp: 3 }}
          >
            Quản lý các phòng trên tầng. Hỗ trợ tính năng tạo nhanh hàng loạt
            phòng theo dãy dựa trên cấu hình loại phòng.
          </p>

          <button
            type="button"
            className="mc-btn mc-btn--primary w-100"
            onClick={() => navigate(`/admin/decks/${deckId}/rooms`)}
            style={{ background: "#0369a1" }}
          >
            <DoorClosed size={18} />
            Quản lý phòng tầng
          </button>
        </article>
      </div>

      {/* FOOTER SYSTEM METADATA */}
      <div className="mt-5 pt-3 border-top text-muted d-flex align-items-center justify-content-between">
        <small>Hệ thống quản lý du thuyền thông minh</small>
        <small className="font-monospace bg-light px-2 py-1 rounded border">
          Deck ID: {deckId}
        </small>
      </div>
    </div>
  );
}
