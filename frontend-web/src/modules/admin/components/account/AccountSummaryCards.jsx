import { Card, Col, Row } from "react-bootstrap";

const categories = [
  {
    key: "ALL",
    title: "Tất cả tài khoản",
    icon: "bi-people",
    color: "primary",
  },
  {
    key: "ADMIN",
    title: "Quản trị viên",
    icon: "bi-person-lock",
    color: "danger",
  },
  {
    key: "MANAGEMENT",
    title: "Quản lý",
    icon: "bi-person-gear",
    color: "info",
  },
  {
    key: "STAFF",
    title: "Nhân viên",
    icon: "bi-person-badge",
    color: "success",
  },
  {
    key: "PASSENGER",
    title: "Hành khách",
    icon: "bi-ticket-perforated",
    color: "warning",
  },
];

export default function AccountSummaryCards({
  counts,
  activeCategory,
  onCategoryChange,
}) {
  return (
    <Row className="g-3 mb-4">
      {categories.map((category) => {
        const isActive = activeCategory === category.key;

        return (
          <Col
            key={category.key}
            xs={12}
            sm={6}
            xl={category.key === "ALL" ? 12 : 3}
          >
            <Card
              role="button"
              tabIndex={0}
              className={`account-summary-card h-100 ${
                isActive ? "active" : ""
              }`}
              onClick={() => onCategoryChange(category.key)}
              onKeyDown={(event) => {
                if (event.key === "Enter" || event.key === " ") {
                  event.preventDefault();
                  onCategoryChange(category.key);
                }
              }}
            >
              <Card.Body>
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <div className="account-summary-title">
                      {category.title}
                    </div>

                    <div className="account-summary-count">
                      {counts[category.key] ?? 0}
                    </div>
                  </div>

                  <div
                    className={`account-summary-icon text-${category.color}`}
                  >
                    <i className={`bi ${category.icon}`} />
                  </div>
                </div>
              </Card.Body>
            </Card>
          </Col>
        );
      })}
    </Row>
  );
}
