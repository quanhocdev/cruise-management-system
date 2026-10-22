import { Edit3 } from "lucide-react";
import { formatDate } from "./helpers";
import "./PassengerRow.css";

export default function PassengerRow({ index, passenger: p, canEdit, onEdit }) {
  const initial = (p.fullName || "?").trim().charAt(0).toUpperCase();

  return (
    <article className="bd-passenger">
      <div className="bd-passenger__head">
        <span className="bd-passenger__avatar" aria-hidden="true">
          {initial}
        </span>

        <div className="bd-passenger__who">
          <h3 className="bd-passenger__name">{p.fullName}</h3>
          <p className="bd-passenger__sub">
            Hành khách {index + 1}
            {p.gender ? ` · ${p.gender}` : ""}
            {p.dateOfBirth ? ` · ${formatDate(p.dateOfBirth)}` : ""}
          </p>
        </div>

        {canEdit && (
          <button
            type="button"
            className="bd-passenger__edit"
            onClick={() => onEdit(p)}
          >
            <Edit3 size={14} /> Sửa
          </button>
        )}
      </div>

      <dl className="bd-passenger__info">
        <div>
          <dt>{p.idCardType || "Giấy tờ"}</dt>
          <dd>{p.identificationNumber || "—"}</dd>
        </div>
        <div>
          <dt>Số điện thoại</dt>
          <dd>{p.phoneNumber || "—"}</dd>
        </div>
        <div>
          <dt>Email</dt>
          <dd>{p.email || "—"}</dd>
        </div>
      </dl>

      {p.documentNote && (
        <p className="bd-passenger__note">Ghi chú: {p.documentNote}</p>
      )}
    </article>
  );
}
