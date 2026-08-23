import { CheckCircle, CircleX } from "lucide-react";
import { Link, useSearchParams } from "react-router-dom";

export default function PaymentResultPage() {
  const [searchParams] = useSearchParams();
  const paymentId = searchParams.get("paymentId");
  const status = searchParams.get("status");
  const successful = status === "SUCCESS";

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
        {paymentId && <p style={styles.reference}>Mã thanh toán: #{paymentId}</p>}
        <Link to="/" style={styles.link}>Về trang chủ</Link>
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
  reference: { margin: "16px 0 24px", color: "#334155", fontWeight: 600 },
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
