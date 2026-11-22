import { Edit3 } from "lucide-react";
import { formatDate } from "./helpers";
import "./PassengerRow.css";

const ID_CARD_LABELS = {
  CCCD: "CCCD",
  CMND: "CMND",
  PASSPORT: "Hộ chiếu",
  CITIZEN_ID: "CCCD",
  NATIONAL_ID: "CMND / CCCD",
};

export default function PassengerRow({ index, passenger: p, canEdit, onEdit }) {
  const initial = (p.fullName || "?").trim().charAt(0).toUpperCase();
  const docLabel = p.idCardType
    ? ID_CARD_LABELS[p.idCardType] || p.idCardType
    : "Giấy tờ";

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
          <dt>{docLabel}</dt>
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
