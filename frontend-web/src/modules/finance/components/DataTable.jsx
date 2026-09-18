// src/modules/finance/components/DataTable.jsx
import React from "react";
import "./DataTable.css";

const DataTable = ({
  columns,
  data = [],
  keyField = "id",
  emptyText = "Không có dữ liệu",
  onRowClick,
  selectedKey,
}) => (
  <div className="data-table-container">
    <table className="data-table">
      <thead>
        <tr>
          {columns.map((col) => (
            <th key={col.accessor ?? col.header} style={col.headerStyle}>
              {col.header}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.length === 0 ? (
          <tr>
            <td colSpan={columns.length} style={{ textAlign: "center" }}>
              {emptyText}
            </td>
          </tr>
        ) : (
          data.map((row, rowIndex) => {
            const key = row?.[keyField] ?? rowIndex;
            return (
              <tr
                key={key}
                onClick={onRowClick ? () => onRowClick(row) : undefined}
                className={[
                  onRowClick ? "data-table-row--clickable" : "",
                  selectedKey != null && selectedKey === key
                    ? "data-table-row--selected"
                    : "",
                ]
                  .filter(Boolean)
                  .join(" ")}
              >
                {columns.map((col) => (
                  <td key={col.accessor ?? col.header}>
                    {col.render
                      ? col.render(row[col.accessor], row)
                      : (row[col.accessor] ?? "—")}
                  </td>
                ))}
              </tr>
            );
          })
        )}
      </tbody>
    </table>
  </div>
);

export default DataTable;
