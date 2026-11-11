import React, { useEffect, useMemo, useState } from "react";
import { Alert, Col, Container, Row, Spinner } from "react-bootstrap";

import useFeedback from "../hooks/useFeedback";

import FeedbackFilter from "../components/feedback/FeedbackFilter";
import FeedbackInvitationCard from "../components/feedback/FeedbackInvitationCard";
import FeedbackForm from "../components/feedback/FeedbackForm";
import FeedbackDetail from "../components/feedback/FeedbackDetail";

import "./PassengerFeedback.css";

export default function PassengerFeedback() {
  const {
    invitations,
    currentFeedback,
    loading,
    detailLoading,
    submitting,
    error,
    success,
    loadMyInvitations,
    submitFeedback,
    skipInvitation,
    loadMyFeedback,
    clearMessages,
  } = useFeedback();

  const [activeFilter, setActiveFilter] = useState("ALL");
  const [selectedInvitation, setSelectedInvitation] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [showDetail, setShowDetail] = useState(false);

  useEffect(() => {
    loadMyInvitations();
  }, [loadMyInvitations]);

  const counts = useMemo(() => {
    const result = {
      all: invitations.length,
      pending: 0,
      completed: 0,
      skipped: 0,
    };

    invitations.forEach((invitation) => {
      if (invitation.status === "PENDING") result.pending += 1;
      if (invitation.status === "COMPLETED") result.completed += 1;
      if (invitation.status === "SKIPPED") result.skipped += 1;
    });

    return result;
  }, [invitations]);

  const filteredInvitations = useMemo(() => {
    if (activeFilter === "ALL") return invitations;

    return invitations.filter(
      (invitation) => invitation.status === activeFilter,
    );
  }, [invitations, activeFilter]);

  const handleOpenForm = (invitation) => {
    clearMessages();
    setSelectedInvitation(invitation);
    setShowForm(true);
  };

  const handleSubmit = async (formData) => {
    if (!selectedInvitation) return;

    const result = await submitFeedback(selectedInvitation.id, formData);

    if (result !== false) {
      setShowForm(false);
      setSelectedInvitation(null);
    }
  };

  const handleSkip = async (invitation) => {
    const confirmed = window.confirm(
      "Bạn có chắc chắn muốn bỏ qua lời mời đánh giá này?",
    );

    if (!confirmed) return;

    clearMessages();
    await skipInvitation(invitation.id);
  };

  const handleOpenDetail = async (invitation) => {
    clearMessages();
    setSelectedInvitation(invitation);
    setShowDetail(true);

    await loadMyFeedback(invitation.id);
  };

  const handleCloseForm = () => {
    if (submitting) return;

    setShowForm(false);
    setSelectedInvitation(null);
  };

  const handleCloseDetail = () => {
    setShowDetail(false);
    setSelectedInvitation(null);
  };

  return (
    <Container fluid className="passenger-feedback-page">
      <div className="feedback-page-header">
        <div>
          <div className="feedback-page-eyebrow">TRẢI NGHIỆM CỦA BẠN</div>
          <h2>Đánh giá của tôi</h2>
          <p>
            Chia sẻ cảm nhận để giúp chúng tôi cải thiện chất lượng dịch vụ.
          </p>
        </div>
      </div>

      {error && (
        <Alert variant="danger" dismissible onClose={clearMessages}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert variant="success" dismissible onClose={clearMessages}>
          {success}
        </Alert>
      )}

      <Row className="feedback-statistics">
        <Col xs={6} md={3}>
          <div className="feedback-stat-card">
            <span>Tổng lời mời</span>
            <strong>{counts.all}</strong>
          </div>
        </Col>

        <Col xs={6} md={3}>
          <div className="feedback-stat-card">
            <span>Chưa đánh giá</span>
            <strong>{counts.pending}</strong>
          </div>
        </Col>

        <Col xs={6} md={3}>
          <div className="feedback-stat-card">
            <span>Đã đánh giá</span>
            <strong>{counts.completed}</strong>
          </div>
        </Col>

        <Col xs={6} md={3}>
          <div className="feedback-stat-card">
            <span>Đã bỏ qua</span>
            <strong>{counts.skipped}</strong>
          </div>
        </Col>
      </Row>

      <section className="feedback-list-section">
        <div className="feedback-list-heading">
          <div>
            <h4>Lời mời đánh giá</h4>
            <p>Danh sách các trải nghiệm bạn có thể đánh giá.</p>
          </div>
        </div>

        <FeedbackFilter
          value={activeFilter}
          onChange={setActiveFilter}
          counts={counts}
        />

        {loading ? (
          <div className="feedback-page-loading">
            <Spinner animation="border" />
            <span>Đang tải danh sách...</span>
          </div>
        ) : filteredInvitations.length === 0 ? (
          <div className="feedback-empty-state">
            <h5>Không có lời mời đánh giá</h5>
            <p>
              {activeFilter === "ALL"
                ? "Hiện tại bạn chưa có lời mời đánh giá nào."
                : "Không có lời mời nào thuộc trạng thái này."}
            </p>
          </div>
        ) : (
          <Row className="g-3 mt-1">
            {filteredInvitations.map((invitation) => (
              <Col key={invitation.id} xs={12} md={6} xl={4}>
                <FeedbackInvitationCard
                  invitation={invitation}
                  onSubmit={handleOpenForm}
                  onSkip={handleSkip}
                  onView={handleOpenDetail}
                />
              </Col>
            ))}
          </Row>
        )}
      </section>

      <FeedbackForm
        show={showForm}
        invitation={selectedInvitation}
        submitting={submitting}
        onHide={handleCloseForm}
        onSubmit={handleSubmit}
      />

      <FeedbackDetail
        show={showDetail}
        loading={detailLoading}
        feedback={currentFeedback}
        onHide={handleCloseDetail}
      />
    </Container>
  );
}
