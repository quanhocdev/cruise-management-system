import React from "react";
import { Badge, Button, Card } from "react-bootstrap";
import "./FeedbackInvitationCard.css";

const TYPE_LABELS = {
  PRODUCT: "Sản phẩm",
  SERVICE: "Dịch vụ",
  ACTIVITY_CRUISE: "Hoạt động trên tàu",
  ACTIVITY_VISIT: "Hoạt động tham quan",
};

const STATUS_CONFIG = {
  PENDING: {
    label: "Chưa đánh giá",
    variant: "warning",
  },
  COMPLETED: {
    label: "Đã đánh giá",
    variant: "success",
  },
  SKIPPED: {
    label: "Đã bỏ qua",
    variant: "secondary",
  },
};

function formatDate(value) {
  if (!value) return "—";

  return new Date(value).toLocaleString("vi-VN", {
    dateStyle: "short",
    timeStyle: "short",
  });
}

export default function FeedbackInvitationCard({
  invitation,
  onSubmit,
  onSkip,
  onView,
}) {
  const status = STATUS_CONFIG[invitation.status] ?? {
    label: invitation.status,
    variant: "secondary",
  };

  const isPending = invitation.status === "PENDING";

  return (
    <Card className="feedback-invitation-card">
      <Card.Body>
        <div className="feedback-invitation-card__top">
          <div>
            <div className="feedback-invitation-card__type">
              {TYPE_LABELS[invitation.feedbackType] ?? invitation.feedbackType}
            </div>

            <h5 className="feedback-invitation-card__title">
              {invitation.targetName || "Nội dung cần đánh giá"}
            </h5>
          </div>

          <Badge bg={status.variant}>{status.label}</Badge>
        </div>

        <div className="feedback-invitation-card__meta">
          <span>Mã đặt chỗ: #{invitation.bookingId}</span>
          <span>Ngày nhận: {formatDate(invitation.createdAt)}</span>
        </div>

        <div className="feedback-invitation-card__actions">
          {isPending ? (
            <>
              <Button
                variant="primary"
                size="sm"
                onClick={() => onSubmit(invitation)}
              >
                Đánh giá
              </Button>

              <Button
                variant="outline-secondary"
                size="sm"
                onClick={() => onSkip(invitation)}
              >
                Bỏ qua
              </Button>
            </>
          ) : (
            <Button
              variant="outline-primary"
              size="sm"
              onClick={() => onView(invitation)}
            >
              Xem chi tiết
            </Button>
          )}
        </div>
      </Card.Body>
    </Card>
  );
}
