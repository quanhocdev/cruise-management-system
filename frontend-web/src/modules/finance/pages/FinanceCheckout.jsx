import React, { useCallback, useState } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import financeService from "../services/financeService";
import { useTourSocket } from "../hooks/useTourSocket";
import "../styles/layout.css";
import "../styles/financeCheckout.css";

const FinanceCheckout = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [currentBookingId, setCurrentBookingId] = useState(null);
  const [scannedBookingCode, setScannedBookingCode] = useState("");
  const [checkoutPreview, setCheckoutPreview] = useState(null);

  const [loadingCheckout, setLoadingCheckout] = useState(false);
  const [processingPayment, setProcessingPayment] = useState(false);

  const loadCheckoutPreview = async (bookingId) => {
    if (!bookingId) return;

    setLoadingCheckout(true);

    try {
      const data = await financeService.getCheckoutPreview(bookingId);

      setCheckoutPreview(data);
      setCurrentBookingId(bookingId);
    } catch (error) {
      console.error("Không thể tải thông tin checkout:", error);
      setCheckoutPreview(null);
      alert(
        error.response?.data?.message ||
          "Không thể tải thông tin thanh toán của booking!",
      );
    } finally {
      setLoadingCheckout(false);
    }
  };

  const handleBookingScanned = useCallback((notification) => {
    console.log("[CHECKOUT] Booking được quét:", notification);

    setScannedBookingCode(notification.bookingCode || "");
    setCurrentBookingId(notification.bookingId || null);

    if (notification.bookingId) {
      loadCheckoutPreview(notification.bookingId);
    }
  }, []);

  const { socketStatus } = useTourSocket(selectedTourId, handleBookingScanned);

  const handleManualSearch = async (bookingCode) => {
    if (!bookingCode.trim() || !selectedTourId) {
      return;
    }

    setLoadingCheckout(true);
    setCheckoutPreview(null);

    try {
      const bookings = await financeService.getBookingsByTour(selectedTourId);

      const found = bookings.find(
        (booking) =>
          booking.bookingCode?.toLowerCase() ===
          bookingCode.trim().toLowerCase(),
      );

      if (!found) {
        alert("Không tìm thấy mã booking này trong tour hiện tại!");

        setCurrentBookingId(null);
        setCheckoutPreview(null);
        return;
      }

      setCurrentBookingId(found.id);
      setScannedBookingCode(found.bookingCode);

      const data = await financeService.getCheckoutPreview(found.id);

      setCheckoutPreview(data);
    } catch (error) {
      console.error("Lỗi tìm kiếm booking:", error);

      setCurrentBookingId(null);
      setCheckoutPreview(null);

      alert(
        error.response?.data?.message ||
          "Lỗi khi tìm kiếm hoặc tải thông tin checkout!",
      );
    } finally {
      setLoadingCheckout(false);
    }
  };

  const handleConfirmPayment = async () => {
    if (!currentBookingId) {
      alert("Chưa có booking để thanh toán!");
      return;
    }

    setProcessingPayment(true);

    try {
      const response = await financeService.confirmCheckout(currentBookingId);

      console.log("[CHECKOUT] Payment response:", response);

      if (!response.paymentUrl) {
        alert("Không nhận được đường dẫn thanh toán VNPay!");
        return;
      }

      window.location.href = response.paymentUrl;
    } catch (error) {
      console.error("Lỗi tạo thanh toán:", error);

      alert(
        error.response?.data?.message ||
          "Không thể tạo thanh toán. Vui lòng thử lại!",
      );
    } finally {
      setProcessingPayment(false);
    }
  };

  const handleTourChange = (tourId) => {
    setSelectedTourId(tourId);
    setCurrentBookingId(null);
    setScannedBookingCode("");
    setCheckoutPreview(null);
  };

  const formatMoney = (value) => {
    return `${Number(value || 0).toLocaleString("vi-VN")} đ`;
  };

  const usageColumns = [
    {
      header: "Usage ID",
      accessor: "usageId",
    },
    {
      header: "Số lượng",
      accessor: "quantity",
    },
    {
      header: "Đơn giá",
      accessor: "unitPrice",
      render: (value) => formatMoney(value),
    },
    {
      header: "Giảm giá",
      accessor: "discountAmount",
      render: (value) => formatMoney(value),
    },
    {
      header: "Thành tiền",
      accessor: "finalAmount",
      render: (value) => <strong>{formatMoney(value)}</strong>,
    },
    {
      header: "Thời gian sử dụng",
      accessor: "usedAt",
      render: (value) =>
        value ? new Date(value).toLocaleString("vi-VN") : "—",
    },
  ];

  const usageSections = [
    {
      title: "Hoạt động tham quan",
      key: "activityVisitUsages",
    },
    {
      title: "Hoạt động trên tàu",
      key: "activityCruiseUsages",
    },
    {
      title: "Dịch vụ",
      key: "serviceUsages",
    },
    {
      title: "Sản phẩm",
      key: "productUsages",
    },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <h2 className="finance-page__title">Quầy Thanh toán</h2>
      </div>

      {/* Chọn Tour + WebSocket */}
      <div className="finance-card">
        <TourSelector
          selectedTourId={selectedTourId}
          onSelectTour={handleTourChange}
        />

        <div style={{ marginTop: 12 }}>Trạng thái kênh: {socketStatus}</div>
      </div>

      {/* Booking Code */}
      {selectedTourId && (
        <div className="finance-card finance-checkout__container">
          <label
            htmlFor="checkout-booking-code"
            style={{
              fontSize: "14px",
              fontWeight: "bold",
            }}
          >
            Mã Booking (Quét QR hoặc Nhập tay):
          </label>

          <div className="finance-checkout__input-group">
            <input
              id="checkout-booking-code"
              type="text"
              className="finance-checkout__input"
              value={scannedBookingCode}
              onChange={(e) => setScannedBookingCode(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleManualSearch(scannedBookingCode);
                }
              }}
              placeholder="Nhập mã booking hoặc chờ quét QR..."
            />

            <button
              type="button"
              className="finance-checkout__btn-search"
              onClick={() => handleManualSearch(scannedBookingCode)}
              disabled={loadingCheckout}
            >
              Tải checkout
            </button>
          </div>
        </div>
      )}

      {/* Loading */}
      {selectedTourId && loadingCheckout && <LoadingSpinner />}

      {/* Checkout Preview */}
      {!loadingCheckout && checkoutPreview && (
        <div className="finance-card finance-checkout__preview">
          {/* Thông tin booking */}
          <div className="finance-checkout__booking-info">
            <h3>Thông tin Booking</h3>

            <div>
              <span>Mã Booking:</span>
              <strong>{checkoutPreview.bookingCode || "—"}</strong>
            </div>

            <div>
              <span>Người liên hệ:</span>
              <strong>{checkoutPreview.primaryContactName || "—"}</strong>
            </div>

            <div>
              <span>Email:</span>
              <strong>{checkoutPreview.primaryContactEmail || "—"}</strong>
            </div>
          </div>

          {/* Chi tiết từng hành khách */}
          <div className="finance-checkout__passengers">
            <h3>Chi tiết sử dụng</h3>

            {!checkoutPreview.passengers ||
            checkoutPreview.passengers.length === 0 ? (
              <p>Booking chưa có dữ liệu sử dụng.</p>
            ) : (
              checkoutPreview.passengers.map((passenger, index) => (
                <div
                  className="finance-checkout__passenger"
                  key={passenger.bookingPassengerId}
                >
                  <div className="finance-checkout__passenger-header">
                    <h4>Hành khách #{index + 1}</h4>

                    <strong>
                      Tổng: {formatMoney(passenger.passengerTotal)}
                    </strong>
                  </div>

                  {usageSections.map(({ title, key }) => {
                    const usages = passenger[key] || [];

                    if (usages.length === 0) {
                      return null;
                    }

                    return (
                      <div
                        className="finance-checkout__usage-section"
                        key={key}
                      >
                        <h4>{title}</h4>

                        <DataTable
                          columns={usageColumns}
                          data={usages}
                          keyField="usageId"
                          emptyText="Không có dữ liệu sử dụng"
                        />
                      </div>
                    );
                  })}
                </div>
              ))
            )}
          </div>

          {/* Tổng tiền */}
          <div className="finance-checkout__total">
            <span>Tổng thanh toán</span>

            <strong>{formatMoney(checkoutPreview.grandTotal)}</strong>
          </div>

          {/* Thanh toán */}
          <div className="finance-checkout__actions">
            <button
              type="button"
              className="finance-checkout__btn-payment"
              onClick={handleConfirmPayment}
              disabled={
                !currentBookingId || processingPayment || loadingCheckout
              }
            >
              {processingPayment ? "Đang tạo thanh toán..." : "Thanh toán"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default FinanceCheckout;
