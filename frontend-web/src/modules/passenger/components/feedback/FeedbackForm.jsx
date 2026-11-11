import React, { useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import "./FeedbackForm.css";

export default function FeedbackForm({
  show,
  invitation,
  submitting,
  onHide,
  onSubmit,
}) {
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");

  useEffect(() => {
    if (show) {
      setRating(5);
      setComment("");
    }
  }, [show, invitation?.id]);

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({ rating, comment: comment.trim() });
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>Đánh giá trải nghiệm</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <div className="feedback-form__target">
            <div className="feedback-form__label">Nội dung đánh giá</div>
            <div className="feedback-form__name">
              {invitation?.targetName || "Trải nghiệm của bạn"}
            </div>
          </div>

          <Form.Group className="mb-4">
            <Form.Label>Chất lượng trải nghiệm</Form.Label>

            <div className="feedback-form__stars">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  className={`feedback-form__star ${
                    star <= rating ? "selected" : ""
                  }`}
                  onClick={() => setRating(star)}
                  aria-label={`${star} sao`}
                >
                  ★
                </button>
              ))}
            </div>

            <div className="feedback-form__rating-text">{rating}/5 sao</div>
          </Form.Group>

          <Form.Group>
            <Form.Label>Nhận xét của bạn</Form.Label>
            <Form.Control
              as="textarea"
              rows={5}
              maxLength={2000}
              value={comment}
              onChange={(event) => setComment(event.target.value)}
              placeholder="Chia sẻ cảm nhận của bạn..."
            />
            <div className="feedback-form__character-count">
              {comment.length}/2000
            </div>
          </Form.Group>
        </Modal.Body>

        <Modal.Footer>
          <Button
            variant="outline-secondary"
            onClick={onHide}
            disabled={submitting}
          >
            Hủy
          </Button>

          <Button type="submit" disabled={submitting}>
            {submitting ? "Đang gửi..." : "Gửi đánh giá"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
