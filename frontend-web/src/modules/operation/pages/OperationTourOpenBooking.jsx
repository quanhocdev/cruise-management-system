// src/modules/operation/pages/OperationTourOpenBooking.jsx

import { AlertCircle, ArrowLeft, CheckCircle2 } from "lucide-react";
import { useNavigate } from "react-router-dom";

import useOperationTourOpenBooking from "../hooks/useOperationTourOpenBooking";
import {
  BookingConfigForm,
  BookingConfigView,
  TourSummaryCard,
} from "../components/tour-booking/BookingPanels";

import "../styles/OperationTourOpenBooking.css";

const OperationTourOpenBooking = () => {
  const navigate = useNavigate();
  const b = useOperationTourOpenBooking();

  return (
    <div className="otb-page">
      <header className="otb-header">
        <button type="button" className="otb-back" onClick={() => navigate(-1)}>
          <ArrowLeft size={18} />
          Quay lại
        </button>

        <div className="otb-title">
          <span>Quản lý Vận hành</span>
          <h1>Cấu hình mở bán Tour</h1>
        </div>
      </header>

      <div className="otb-card">
        {!b.tourId && (
          <div className="otb-alert otb-alert--error">
            <AlertCircle size={18} />
            <span>Thiếu Tour ID, không xác định được Tour cần cấu hình.</span>
          </div>
        )}

        {b.error && (
          <div className="otb-alert otb-alert--error">
            <AlertCircle size={18} />
            <span>{b.error}</span>
          </div>
        )}

        {b.successMessage && (
          <div className="otb-alert otb-alert--success">
            <CheckCircle2 size={18} />
            <span>{b.successMessage}</span>
          </div>
        )}

        <TourSummaryCard
          tourInfo={b.tourInfo}
          currentStatus={b.currentStatus}
        />

        {b.isEditing ? (
          <BookingConfigForm
            isUpdate={b.isUpdate}
            bookingStart={b.bookingStart}
            bookingEnd={b.bookingEnd}
            onChangeStart={b.setBookingStart}
            onChangeEnd={b.setBookingEnd}
            formError={b.formError}
            loading={b.loading}
            onSave={b.handleSave}
            onCancel={b.cancelEditing}
          />
        ) : (
          <BookingConfigView
            bookingStart={b.bookingStart}
            bookingEnd={b.bookingEnd}
            loading={b.loading}
            onEdit={b.startEditing}
            onDelete={b.handleDelete}
          />
        )}

        {/* Khu vực dành cho bảng lượng đăng ký / vé sẽ bổ sung sau */}
        <section className="otb-placeholder">
          <h3>Quản lý lượng đăng ký &amp; Vé</h3>
          <p>
            Khu vực hiển thị danh sách khách hàng đăng ký và thống kê vé sẽ được
            bổ sung tại đây.
          </p>
        </section>
      </div>
    </div>
  );
};

export default OperationTourOpenBooking;
