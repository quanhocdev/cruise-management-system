// src/modules/passenger/pages/PaymentResultPage.jsx
import { useEffect, useState } from "react";
import { CheckCircle, CircleX, LoaderCircle } from "lucide-react";
import { Link, useSearchParams } from "react-router-dom";
import paymentService from "../services/paymentService";
import "../styles/PaymentResult.css";

export default function PaymentResultPage() {
  const [searchParams] = useSearchParams();
  const paymentId = searchParams.get("paymentId");
  const status = searchParams.get("status");
  const successful = status === "SUCCESS";
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(Boolean(paymentId));

  useEffect(() => {
    if (!paymentId) return;
    paymentService
      .getPayment(paymentId)
      .then(setPayment)
      .catch(() => setPayment(null))
      .finally(() => setLoading(false));
  }, [paymentId]);

  const formatMoney = (value) =>
    new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
      maximumFractionDigits: 0,
    }).format(value || 0);

  return (
    <main className="payment-result-page">
      <section className="payment-result-card" aria-live="polite">
        {successful ? (
          <CheckCircle size={64} color="#15803d" aria-hidden="true" />
        ) : (
          <CircleX size={64} color="#b91c1c" aria-hidden="true" />
        )}
        <h1 className="payment-result-title">
          {successful ? "Thanh toán thành công" : "Thanh toán chưa thành công"}
        </h1>
        <p className="payment-result-message">
          {successful
            ? "Đặt chỗ của bạn đã được xác nhận."
            : "Giao dịch chưa hoàn tất. Bạn có thể quay lại và thử thanh toán lần nữa."}
        </p>
        {loading ? (
          <LoaderCircle
            size={26}
            className="payment-result-spinner"
            aria-label="Đang tải chi tiết thanh toán"
          />
        ) : (
          <div className="payment-result-details">
            {paymentId && (
              <p>
                <span>Mã thanh toán</span>
                <strong>#{paymentId}</strong>
              </p>
            )}
            {payment?.referenceId && (
              <p>
                <span>Mã booking</span>
                <strong>#{payment.referenceId}</strong>
              </p>
            )}
            {payment?.amount != null && (
              <p>
                <span>Số tiền</span>
                <strong>{formatMoney(payment.amount)}</strong>
              </p>
            )}
            {payment?.transactionCode && (
              <p>
                <span>Mã giao dịch</span>
                <strong>{payment.transactionCode}</strong>
              </p>
            )}
          </div>
        )}
        <Link to="/passenger/bookings" className="payment-result-link">
          Về danh sách vé
        </Link>
      </section>
    </main>
  );
}
