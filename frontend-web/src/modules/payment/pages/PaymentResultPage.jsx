import { useEffect, useState } from "react";
import { CheckCircle, CircleX, LoaderCircle } from "lucide-react";
import { Link, useSearchParams } from "react-router-dom";
import paymentService from "../services/paymentService";

export default function PaymentResultPage() {
  const [searchParams] = useSearchParams();
  const paymentId = searchParams.get("paymentId");
  const status = searchParams.get("status");
  const successful = status === "SUCCESS";
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(Boolean(paymentId));

  useEffect(() => {
    if (!paymentId) return;
    paymentService.getPayment(paymentId)
      .then(setPayment)
      .catch(() => setPayment(null))
      .finally(() => setLoading(false));
  }, [paymentId]);

  const formatMoney = (value) => new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value || 0);

  return (
    <main style={styles.page}>
      <section style={styles.card} aria-live="polite">
        {successful ? (
          <CheckCircle size={64} color="#15803d" aria-hidden="true" />
        ) : (
          <CircleX size={64} color="#b91c1c" aria-hidden="true" />
        )}
        <h1 style={styles.title}>
          {successful ? "Thanh toán thành công" : "Thanh toán chưa thành công"}
        </h1>
        <p style={styles.message}>
          {successful
            ? "Đặt chỗ của bạn đã được xác nhận."
            : "Giao dịch chưa hoàn tất. Bạn có thể quay lại và thử thanh toán lần nữa."}
        </p>
        {loading ? (
          <LoaderCircle size={26} style={styles.spinner} aria-label="Đang tải chi tiết thanh toán" />
        ) : (
          <div style={styles.details}>
            {paymentId && <p><span>Mã thanh toán</span><strong>#{paymentId}</strong></p>}
            {payment?.referenceId && <p><span>Mã booking</span><strong>#{payment.referenceId}</strong></p>}
            {payment?.amount != null && <p><span>Số tiền</span><strong>{formatMoney(payment.amount)}</strong></p>}
            {payment?.transactionCode && <p><span>Mã giao dịch</span><strong>{payment.transactionCode}</strong></p>}
          </div>
        )}
        <Link to="/passenger/dashboard" style={styles.link}>Về danh sách tour</Link>
      </section>
    </main>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    display: "grid",
    placeItems: "center",
    padding: "24px",
    background: "#f1f5f9",
  },
  card: {
    width: "min(100%, 520px)",
    padding: "40px 32px",
    borderRadius: "16px",
    background: "#ffffff",
    boxShadow: "0 16px 40px rgba(15, 23, 42, 0.12)",
    textAlign: "center",
  },
  title: { margin: "20px 0 8px", color: "#0f172a" },
  message: { margin: 0, color: "#475569", lineHeight: 1.6 },
  details: { margin: "24px 0", padding: "14px 20px", borderRadius: "12px", background: "#f8fafc" },
  spinner: { margin: "24px", color: "#0f766e", animation: "spin 1s linear infinite" },
  link: {
    display: "inline-block",
    padding: "11px 22px",
    borderRadius: "8px",
    background: "#0f766e",
    color: "#ffffff",
    textDecoration: "none",
    fontWeight: 600,
  },
};