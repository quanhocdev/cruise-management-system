import { Button, Spinner, Table } from "react-bootstrap";

export default function PolicyTable({
  policies,
  loading,
  filterLoading,
  onEdit,
  onDelete,
  onManageRules,
}) {
  // =====================================================
  // INITIAL LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="policy-table-loading">
        <Spinner animation="border" />

        <span>Đang tải danh sách chính sách...</span>
      </div>
    );
  }

  // =====================================================
  // EMPTY
  // =====================================================

  if (!policies || policies.length === 0) {
    return (
      <div className="policy-table-empty">
        <div className="policy-table-empty-icon">📋</div>

        <h5>Chưa có chính sách</h5>

        <p>Không có chính sách nào phù hợp với bộ lọc hiện tại.</p>
      </div>
    );
  }

  // =====================================================
  // TYPE LABEL
  // =====================================================

  const getTypeLabel = (type) => {
    switch (type) {
      case "BOOKING":
        return "Đặt tour";

      case "CANCEL":
        return "Hủy tour";

      default:
        return type || "-";
    }
  };

  // =====================================================
  // STATUS LABEL
  // =====================================================

  const getStatusLabel = (status) => {
    switch (status) {
      case "ACTIVE":
        return "Đang hoạt động";

      case "INACTIVE":
        return "Ngừng hoạt động";

      default:
        return status || "-";
    }
  };

  // =====================================================
  // FORMAT DATE
  // =====================================================

  const formatDateTime = (value) => {
    if (!value) {
      return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString("vi-VN");
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="policy-table-wrapper">
      {/* FILTER LOADING INDICATOR */}

      {filterLoading && (
        <div className="policy-table-filter-loading">
          <Spinner animation="border" size="sm" />

          <span>Đang cập nhật danh sách...</span>
        </div>
      )}

      <Table responsive hover bordered className="policy-table align-middle">
        <thead>
          <tr>
            <th>Loại</th>

            <th>Tiêu đề</th>

            <th>Nội dung</th>

            <th>Trạng thái</th>

            <th>Cập nhật</th>

            <th className="policy-table-action-column">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {policies.map((policy) => (
            <tr key={policy.id}>
              {/* TYPE */}

              <td>
                <span
                  className={`policy-type-badge ${
                    policy.type === "BOOKING"
                      ? "policy-type-booking"
                      : "policy-type-cancel"
                  }`}
                >
                  {getTypeLabel(policy.type)}
                </span>
              </td>

              {/* TITLE */}

              <td>
                <div className="policy-table-title">{policy.title}</div>
              </td>

              {/* CONTENT */}

              <td>
                <div className="policy-table-content">
                  {policy.content || "-"}
                </div>
              </td>

              {/* STATUS */}

              <td>
                <span
                  className={`policy-status-badge ${
                    policy.status === "ACTIVE"
                      ? "policy-status-active"
                      : "policy-status-inactive"
                  }`}
                >
                  {getStatusLabel(policy.status)}
                </span>
              </td>

              {/* UPDATED */}

              <td>{formatDateTime(policy.updatedAt)}</td>

              {/* ACTIONS */}

              <td>
                <div className="policy-table-actions">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(policy)}
                    disabled={filterLoading}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-success"
                    onClick={() => onManageRules(policy)}
                    disabled={filterLoading}
                  >
                    Quy tắc
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(policy)}
                    disabled={filterLoading}
                  >
                    Xóa
                  </Button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}
