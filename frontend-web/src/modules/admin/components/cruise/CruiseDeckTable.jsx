import { Button, Spinner, Table } from "react-bootstrap";

export default function CruiseDeckTable({
  decks,
  loading,
  onEdit,
  onDelete,
  onView,
}) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />
        <p className="mt-2 text-muted">Đang tải danh sách tầng...</p>
      </div>
    );
  }

  if (!decks || decks.length === 0) {
    return (
      <div className="text-center py-5 text-muted">
        Chưa có tầng nào cho du thuyền này.
      </div>
    );
  }

  return (
    <div className="table-responsive">
      <Table
        bordered
        hover
        responsive
        className="align-middle cruise-deck-table"
      >
        <thead>
          <tr>
            <th style={{ width: "80px" }}>STT</th>

            <th style={{ width: "120px" }}>Tầng</th>

            <th>Trạng thái</th>

            <th className="text-center" style={{ width: "300px" }}>
              Thao tác
            </th>
          </tr>
        </thead>

        <tbody>
          {decks.map((deck, index) => (
            <tr key={deck.id}>
              <td>{index + 1}</td>

              <td>
                <strong>Tầng {deck.deckNumber}</strong>
              </td>

              <td>
                <span
                  className={
                    deck.status === "ACTIVE"
                      ? "badge bg-success"
                      : "badge bg-secondary"
                  }
                >
                  {deck.status}
                </span>
              </td>

              <td>
                <div className="d-flex justify-content-center gap-2">
                  <Button
                    size="sm"
                    variant="primary"
                    onClick={() => onView(deck)}
                  >
                    Xem
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(deck)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(deck)}
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
