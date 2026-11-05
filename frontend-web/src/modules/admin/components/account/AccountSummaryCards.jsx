import { Card, Col, Row } from "react-bootstrap";

const categories = [
  { key: "ALL", label: "Tất cả", color: "primary" },
  { key: "MANAGER", label: "Quản lý", color: "info" },
  { key: "STAFF", label: "Nhân viên", color: "success" },
  { key: "PASSENGER", label: "Hành khách", color: "warning" },
];

export default function AccountSummaryCards({
  counts,
  activeCategory,
  onCategoryChange,
}) {
  return (
    <Row className="g-3 mb-4">
      {categories.map((category) => {
        const active = activeCategory === category.key;

        return (
          <Col xs={12} sm={6} xl={3} key={category.key}>
            <Card
              role="button"
              onClick={() => onCategoryChange(category.key)}
              className={`h-100 border-2 ${
                active ? `border-${category.color}` : "border-light"
              }`}
              style={{
                cursor: "pointer",
                transition: "0.2s",
              }}
            >
              <Card.Body>
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <div className="text-muted mb-2">{category.label}</div>

                    <div className="fs-3 fw-bold">
                      {counts[category.key] ?? 0}
                    </div>

                    <small className="text-muted">tài khoản</small>
                  </div>

                  <div
                    className={`rounded-circle bg-${category.color} bg-opacity-10
                      text-${category.color} d-flex align-items-center
                      justify-content-center`}
                    style={{ width: 48, height: 48, fontSize: 22 }}
                  >
                    {category.key === "ALL" && "👥"}
                    {category.key === "MANAGER" && "🧑‍💼"}
                    {category.key === "STAFF" && "🧑‍🔧"}
                    {category.key === "PASSENGER" && "🧳"}
                  </div>
                </div>

                {active && (
                  <div
                    className={`text-${category.color} small mt-3 fw-semibold`}
                  >
                    Đang xem danh mục này
                  </div>
                )}
              </Card.Body>
            </Card>
          </Col>
        );
      })}
    </Row>
  );
}
