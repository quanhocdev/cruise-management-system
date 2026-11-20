import { useState, useEffect } from "react";
import { Badge, Spinner } from "react-bootstrap";
import "./SelectedRoomPreview.css";

export default function SelectedRoomPreview({ roomType, loading }) {
  const [activeImage, setActiveImage] = useState(null);

  useEffect(() => {
    if (roomType?.imageUrl) {
      setActiveImage(roomType.imageUrl);
    } else if (roomType?.detailImages?.length > 0) {
      const firstValid = roomType.detailImages.find((img) => img.imageUrl);
      setActiveImage(firstValid ? firstValid.imageUrl : null);
    } else {
      setActiveImage(null);
    }
  }, [roomType]);

  if (loading) {
    return (
      <div className="selected-room-loading">
        <Spinner size="sm" animation="border" className="me-2" />
        <span className="text-muted small">
          Đang tải thông tin hạng phòng...
        </span>
      </div>
    );
  }

  if (!roomType) return null;

  const gallery = [
    ...(roomType.imageUrl
      ? [{ id: "cover", imageUrl: roomType.imageUrl, label: "Ảnh đại diện" }]
      : []),
    ...(roomType.detailImages || []).filter((img) => img.imageUrl),
  ];

  return (
    <div className="selected-room-preview">
      <div className="selected-room-header">
        <div>
          <span className="selected-room-subtitle">Hạng phòng đi kèm gói</span>
          <h6 className="selected-room-title">{roomType.name}</h6>
        </div>
        <Badge bg="info" text="dark">
          Tối đa {roomType.capacity || 2} khách / phòng
        </Badge>
      </div>

      {roomType.description && (
        <p className="selected-room-desc">{roomType.description}</p>
      )}

      {activeImage && (
        <div className="selected-room-stage">
          <img
            src={activeImage}
            alt={roomType.name}
            className="selected-room-main-img"
          />
        </div>
      )}

      {gallery.length > 1 && (
        <div className="selected-room-thumbs">
          {gallery.map((item) => {
            const isActive = activeImage === item.imageUrl;
            return (
              <button
                key={item.id}
                type="button"
                onClick={() => setActiveImage(item.imageUrl)}
                className={`selected-room-thumb-btn ${
                  isActive ? "is-active" : ""
                }`}
                title={item.label || "Ảnh chi tiết"}
              >
                <img
                  src={item.imageUrl}
                  alt={item.label || roomType.name}
                  className="selected-room-thumb-img"
                />
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
