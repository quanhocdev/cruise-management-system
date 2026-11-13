import React from "react";
import { Badge, Modal, Spinner } from "react-bootstrap";
import "./FeedbackDetail.css";

const TYPE_LABELS = {
  PRODUCT: "Sản phẩm",
  SERVICE: "Dịch vụ",
  ACTIVITY_CRUISE: "Hoạt động trên tàu",
  ACTIVITY_VISIT: "Hoạt động tham quan",
};

export default function FeedbackDetail({ show, loading, feedback, onHide }) {
  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Chi tiết đánh giá</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        {loading ? (
          <div className="feedback-detail__loading">
            <Spinner animation="border" size="sm" />
            <span>Đang tải đánh giá...</span>
          </div>
        ) : feedback ? (
          <div className="feedback-detail">
            <div className="feedback-detail__type">
              {TYPE_LABELS[feedback.feedbackType] ?? feedback.feedbackType}
            </div>

            <h5 className="feedback-detail__target">
              {feedback.targetName || "Nội dung đánh giá"}
            </h5>

            <div className="feedback-detail__rating">
              {Array.from({ length: 5 }, (_, index) => (
                <span
                  key={index}
                  className={index < (feedback.rating ?? 0) ? "active" : ""}
                >
                  ★
                </span>
              ))}
              <Badge bg="light" text="dark">
                {feedback.rating ?? 0}/5
              </Badge>
            </div>

            <div className="feedback-detail__comment">
              <div className="feedback-detail__label">Nhận xét</div>
              <p>{feedback.comment?.trim() || "Không có nhận xét."}</p>
            </div>

            <div className="feedback-detail__meta">
              Mã đặt chỗ: #{feedback.bookingId}
            </div>
          </div>
        ) : (
          <p className="text-muted mb-0">Không tìm thấy nội dung đánh giá.</p>
        )}
      </Modal.Body>
    </Modal>
  );
}
