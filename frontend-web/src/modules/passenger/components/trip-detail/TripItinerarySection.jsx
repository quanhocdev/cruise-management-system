import React, { useMemo, useState } from "react";
import {
  CalendarDays,
  Clock3,
  MapPin,
  Ship,
  ChevronDown,
  ChevronUp,
  Anchor,
  CircleDot,
} from "lucide-react";
import { Alert, Spinner } from "react-bootstrap";

import "./TripItinerarySection.css";

function getItems(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.days)) return data.days;
  if (Array.isArray(data?.schedules)) return data.schedules;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.itinerary)) return data.itinerary;
  return [];
}

function getStops(day) {
  return Array.isArray(day?.stops) ? day.stops : [];
}

function formatDate(value) {
  if (!value) return null;

  // Backend trả LocalDate dạng yyyy-MM-dd.
  // Tạo Date theo local time để tránh lệch ngày do UTC.
  const match = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);

  if (match) {
    const [, year, month, day] = match;
    const date = new Date(Number(year), Number(month) - 1, Number(day));

    return new Intl.DateTimeFormat("vi-VN", {
      weekday: "long",
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    }).format(date);
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return new Intl.DateTimeFormat("vi-VN", {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(date);
}

function formatTime(value) {
  if (!value) return null;

  // Backend trả LocalDateTime dạng yyyy-MM-ddTHH:mm:ss.
  if (typeof value === "string") {
    const match = /T(\d{2}):(\d{2})/.exec(value);

    if (match) {
      return `${match[1]}:${match[2]}`;
    }

    if (/^\d{2}:\d{2}/.test(value)) {
      return value.slice(0, 5);
    }
  }

  return value;
}

function getDayTitle(day, index) {
  return day?.name || `Ngày ${day?.dayNumber ?? index + 1}`;
}

function getPort(stop) {
  return stop?.port || null;
}

function getStopTitle(stop) {
  const port = getPort(stop);

  return (
    port?.name || stop?.portName || stop?.name || stop?.title || "Điểm dừng"
  );
}

function getStopDescription(stop) {
  const port = getPort(stop);

  return (
    stop?.description || stop?.content || stop?.note || port?.description || ""
  );
}

function getStopLocation(stop) {
  const port = getPort(stop);

  return (
    port?.address ||
    [port?.city, port?.country].filter(Boolean).join(", ") ||
    stop?.address ||
    stop?.location ||
    null
  );
}

function ItineraryStop({ stop, index, isLast }) {
  const port = getPort(stop);
  const title = getStopTitle(stop);
  const description = getStopDescription(stop);
  const location = getStopLocation(stop);

  const arrivalTime = formatTime(stop?.arriveAt);
  const departureTime = formatTime(stop?.leaveAt);

  return (
    <div className="trip-itinerary-stop">
      <div className="trip-itinerary-stop__timeline">
        <span className="trip-itinerary-stop__dot">
          {index === 0 ? <Anchor size={14} /> : <CircleDot size={13} />}
        </span>

        {!isLast && <span className="trip-itinerary-stop__line" />}
      </div>

      <div className="trip-itinerary-stop__content">
        <div className="trip-itinerary-stop__top">
          <h4 className="trip-itinerary-stop__title">{title}</h4>

          {(arrivalTime || departureTime) && (
            <span className="trip-itinerary-stop__time">
              <Clock3 size={13} />
              {arrivalTime || "Chưa cập nhật"}
              {departureTime && departureTime !== arrivalTime
                ? ` - ${departureTime}`
                : ""}
            </span>
          )}
        </div>

        {location && (
          <div className="trip-itinerary-stop__location">
            <MapPin size={13} />
            <span>{location}</span>
          </div>
        )}

        {description && (
          <p className="trip-itinerary-stop__description">{description}</p>
        )}
      </div>
    </div>
  );
}

function ItineraryDay({ day, index, expanded, onToggle }) {
  const stops = getStops(day);

  // Đúng tên trường từ PassengerScheduleDTO.
  const date = formatDate(day?.realDay);
  const description = day?.description || "";

  return (
    <article
      className={`trip-itinerary-day ${
        expanded ? "trip-itinerary-day--expanded" : ""
      }`}
    >
      <button
        type="button"
        className="trip-itinerary-day__header"
        onClick={onToggle}
        aria-expanded={expanded}
      >
        <div className="trip-itinerary-day__number">
          {day?.dayNumber ?? index + 1}
        </div>

        <div className="trip-itinerary-day__heading">
          <h3 className="trip-itinerary-day__title">
            {getDayTitle(day, index)}
          </h3>

          <div className="trip-itinerary-day__meta">
            {date && (
              <span>
                <CalendarDays size={13} />
                {date}
              </span>
            )}

            <span>
              <MapPin size={13} />
              {stops.length} điểm dừng
            </span>
          </div>
        </div>

        <span className="trip-itinerary-day__toggle">
          {expanded ? <ChevronUp size={19} /> : <ChevronDown size={19} />}
        </span>
      </button>

      {expanded && (
        <div className="trip-itinerary-day__body">
          {description && (
            <p className="trip-itinerary-day__description">{description}</p>
          )}

          {stops.length > 0 ? (
            <div className="trip-itinerary-day__stops">
              {stops.map((stop, stopIndex) => (
                <ItineraryStop
                  key={stop?.id || `${index}-${stopIndex}`}
                  stop={stop}
                  index={stopIndex}
                  isLast={stopIndex === stops.length - 1}
                />
              ))}
            </div>
          ) : (
            <div className="trip-itinerary-day__empty">
              Chưa có thông tin các điểm dừng trong ngày này.
            </div>
          )}
        </div>
      )}
    </article>
  );
}

export default function TripItinerarySection({
  itinerary,
  loading = false,
  error = "",
  onReload,
}) {
  const days = useMemo(() => getItems(itinerary), [itinerary]);

  const [expandedDays, setExpandedDays] = useState({});

  const toggleDay = (index) => {
    setExpandedDays((previous) => ({
      ...previous,
      [index]: !previous[index],
    }));
  };

  const expandAll = () => {
    setExpandedDays(Object.fromEntries(days.map((_, index) => [index, true])));
  };

  const collapseAll = () => {
    setExpandedDays({});
  };

  return (
    <section className="trip-itinerary-section">
      <div className="trip-itinerary-section__header">
        <div>
          <div className="trip-itinerary-section__eyebrow">
            <Ship size={15} />
            LỊCH TRÌNH CHUYẾN ĐI
          </div>

          <h2 className="trip-itinerary-section__title">Lịch trình chi tiết</h2>

          <p className="trip-itinerary-section__subtitle">
            Theo dõi các ngày khởi hành, điểm dừng và hoạt động trong chuyến đi.
          </p>
        </div>

        {days.length > 1 && (
          <div className="trip-itinerary-section__actions">
            <button type="button" onClick={expandAll}>
              Mở tất cả
            </button>

            <button type="button" onClick={collapseAll}>
              Thu gọn
            </button>
          </div>
        )}
      </div>

      {loading ? (
        <div className="trip-itinerary-section__state">
          <Spinner animation="border" size="sm" variant="primary" />
          <span>Đang tải lịch trình...</span>
        </div>
      ) : error ? (
        <Alert variant="danger" className="trip-itinerary-section__alert">
          <div>{error}</div>

          {onReload && (
            <button type="button" onClick={onReload}>
              Thử tải lại
            </button>
          )}
        </Alert>
      ) : days.length === 0 ? (
        <div className="trip-itinerary-section__empty">
          <CalendarDays size={30} />
          <strong>Chưa có lịch trình</strong>
          <span>
            Thông tin lịch trình của chuyến đi hiện chưa được cập nhật.
          </span>
        </div>
      ) : (
        <div className="trip-itinerary-section__list">
          {days.map((day, index) => (
            <ItineraryDay
              key={day?.id || day?.dayNumber || index}
              day={day}
              index={index}
              expanded={expandedDays[index] ?? index === 0}
              onToggle={() => toggleDay(index)}
            />
          ))}
        </div>
      )}
    </section>
  );
}
