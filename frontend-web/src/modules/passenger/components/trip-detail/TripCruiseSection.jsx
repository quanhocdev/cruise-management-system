import React, { useMemo, useState } from "react";
import {
  Ship,
  Layers3,
  MapPin,
  ChevronDown,
  ChevronUp,
  Building2,
  Compass,
  Info,
} from "lucide-react";
import { Alert, Spinner } from "react-bootstrap";

import "./TripCruiseSection.css";

function unwrapList(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.decks)) return data.decks;
  if (Array.isArray(data?.areas)) return data.areas;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

function getCruiseName(cruise) {
  return (
    cruise?.cruiseName ||
    cruise?.name ||
    cruise?.vesselName ||
    cruise?.shipName ||
    "Thông tin tàu"
  );
}

function getCruiseCode(cruise) {
  return cruise?.code || cruise?.cruiseCode || cruise?.vesselCode || null;
}

function getCruiseDescription(cruise) {
  return cruise?.description || cruise?.introduction || cruise?.note || "";
}

function getDeckId(deck) {
  return deck?.deckId || deck?.id || deck?.cruiseDeckId;
}

function getDeckName(deck, index) {
  return (
    deck?.deckName ||
    deck?.name ||
    deck?.title ||
    (deck?.deckNumber != null
      ? `Boong ${deck.deckNumber}`
      : `Boong ${index + 1}`)
  );
}

function getAreaName(area) {
  return (
    area?.areaName ||
    area?.name ||
    area?.title ||
    area?.productAreaName ||
    area?.serviceAreaName ||
    area?.activityAreaName ||
    "Khu vực"
  );
}

function getAreaType(area) {
  const type = area?.areaType || area?.type || area?.category;
  if (!type) return null;

  const normalized = String(type).toUpperCase();

  const labels = {
    PRODUCT: "Khu vực sản phẩm",
    SERVICE: "Khu vực dịch vụ",
    ACTIVITY: "Khu vực hoạt động",
    RESTAURANT: "Nhà hàng",
    ENTERTAINMENT: "Giải trí",
    ACCOMMODATION: "Lưu trú",
  };

  return labels[normalized] || type;
}

function getAreaDescription(area) {
  return area?.description || area?.note || area?.detail || "";
}

function AreaCard({ area }) {
  const type = getAreaType(area);
  const description = getAreaDescription(area);

  return (
    <div className="trip-cruise-area">
      <div className="trip-cruise-area__icon">
        <Building2 size={17} />
      </div>

      <div className="trip-cruise-area__content">
        <div className="trip-cruise-area__heading">
          <h4>{getAreaName(area)}</h4>
          {type && <span>{type}</span>}
        </div>

        {description && (
          <p className="trip-cruise-area__description">{description}</p>
        )}

        {area?.location && (
          <div className="trip-cruise-area__location">
            <MapPin size={12} />
            {area.location}
          </div>
        )}
      </div>
    </div>
  );
}

export default function TripCruiseSection({
  cruise,
  decks = [],
  deckAreas = {},
  loading = false,
  decksLoading = false,
  error = "",
  decksError = "",
  areasLoading = {},
  areasError = {},
  onLoadDeckAreas,
  onReload,
}) {
  const [selectedDeckId, setSelectedDeckId] = useState(null);

  const deckList = useMemo(() => unwrapList(decks), [decks]);

  const selectedDeck = deckList.find(
    (deck) => String(getDeckId(deck)) === String(selectedDeckId),
  );

  const areasForSelectedDeck = useMemo(() => {
    if (!selectedDeckId) return [];

    const value = deckAreas?.[selectedDeckId];
    return unwrapList(value);
  }, [deckAreas, selectedDeckId]);

  const handleSelectDeck = async (deck) => {
    const deckId = getDeckId(deck);
    if (!deckId) return;

    if (String(selectedDeckId) === String(deckId)) {
      setSelectedDeckId(null);
      return;
    }

    setSelectedDeckId(deckId);

    if (!Object.prototype.hasOwnProperty.call(deckAreas || {}, deckId)) {
      await onLoadDeckAreas?.(deckId);
    }
  };

  return (
    <section className="trip-cruise-section">
      <div className="trip-cruise-section__header">
        <div className="trip-cruise-section__heading-icon">
          <Ship size={21} />
        </div>

        <div className="trip-cruise-section__heading-content">
          <span className="trip-cruise-section__eyebrow">
            THÔNG TIN PHƯƠNG TIỆN
          </span>
          <h2 className="trip-cruise-section__title">Tàu du lịch</h2>
          <p className="trip-cruise-section__subtitle">
            Thông tin tàu và sơ đồ các boong, khu vực trên tàu.
          </p>
        </div>
      </div>

      {loading ? (
        <div className="trip-cruise-section__state">
          <Spinner animation="border" size="sm" variant="primary" />
          <span>Đang tải thông tin tàu...</span>
        </div>
      ) : error ? (
        <Alert variant="danger" className="trip-cruise-section__alert">
          <span>{error}</span>
          {onReload && (
            <button type="button" onClick={onReload}>
              Thử tải lại
            </button>
          )}
        </Alert>
      ) : !cruise ? (
        <div className="trip-cruise-section__empty">
          <Ship size={30} />
          <strong>Chưa có thông tin tàu</strong>
          <span>Thông tin tàu của chuyến đi chưa được cập nhật.</span>
        </div>
      ) : (
        <>
          <div className="trip-cruise-info">
            <div className="trip-cruise-info__main">
              <div className="trip-cruise-info__name-row">
                <h3>{getCruiseName(cruise)}</h3>
                {getCruiseCode(cruise) && (
                  <span className="trip-cruise-info__code">
                    {getCruiseCode(cruise)}
                  </span>
                )}
              </div>

              {getCruiseDescription(cruise) && (
                <p className="trip-cruise-info__description">
                  {getCruiseDescription(cruise)}
                </p>
              )}

              <div className="trip-cruise-info__meta">
                {cruise?.capacity != null && (
                  <span>
                    <Info size={14} />
                    Sức chứa: {cruise.capacity}
                  </span>
                )}

                {cruise?.numberOfDecks != null && (
                  <span>
                    <Layers3 size={14} />
                    {cruise.numberOfDecks} boong
                  </span>
                )}

                {cruise?.flagCountry && (
                  <span>
                    <Compass size={14} />
                    {cruise.flagCountry}
                  </span>
                )}
              </div>
            </div>

            {cruise?.imageUrl && (
              <div className="trip-cruise-info__image">
                <img src={cruise.imageUrl} alt={getCruiseName(cruise)} />
              </div>
            )}
          </div>

          <div className="trip-cruise-decks">
            <div className="trip-cruise-decks__header">
              <div>
                <h3>Khám phá các boong tàu</h3>
                <p>Chọn một boong để xem các khu vực tương ứng.</p>
              </div>

              <span className="trip-cruise-decks__count">
                {deckList.length} boong
              </span>
            </div>

            {decksLoading ? (
              <div className="trip-cruise-section__state">
                <Spinner animation="border" size="sm" variant="primary" />
                <span>Đang tải danh sách boong...</span>
              </div>
            ) : decksError ? (
              <Alert variant="warning" className="trip-cruise-section__alert">
                {decksError}
              </Alert>
            ) : deckList.length === 0 ? (
              <div className="trip-cruise-decks__empty">
                Chưa có thông tin boong tàu.
              </div>
            ) : (
              <div className="trip-cruise-decks__list">
                {deckList.map((deck, index) => {
                  const deckId = getDeckId(deck);
                  const isSelected = String(selectedDeckId) === String(deckId);

                  const areasErrorForDeck = areasError?.[deckId];
                  const loadingAreasForDeck = areasLoading?.[deckId];

                  return (
                    <article
                      className={`trip-cruise-deck ${
                        isSelected ? "trip-cruise-deck--selected" : ""
                      }`}
                      key={deckId || index}
                    >
                      <button
                        type="button"
                        className="trip-cruise-deck__button"
                        onClick={() => handleSelectDeck(deck)}
                        aria-expanded={isSelected}
                      >
                        <div className="trip-cruise-deck__icon">
                          <Layers3 size={18} />
                        </div>

                        <div className="trip-cruise-deck__content">
                          <h4>{getDeckName(deck, index)}</h4>

                          {deck?.description && <p>{deck.description}</p>}
                        </div>

                        <span className="trip-cruise-deck__toggle">
                          {isSelected ? (
                            <ChevronUp size={19} />
                          ) : (
                            <ChevronDown size={19} />
                          )}
                        </span>
                      </button>

                      {isSelected && (
                        <div className="trip-cruise-deck__areas">
                          {loadingAreasForDeck ? (
                            <div className="trip-cruise-section__state">
                              <Spinner
                                animation="border"
                                size="sm"
                                variant="primary"
                              />
                              <span>Đang tải khu vực...</span>
                            </div>
                          ) : areasErrorForDeck ? (
                            <Alert
                              variant="danger"
                              className="trip-cruise-section__alert"
                            >
                              <span>{areasErrorForDeck}</span>
                              <button
                                type="button"
                                onClick={() => onLoadDeckAreas?.(deckId)}
                              >
                                Thử lại
                              </button>
                            </Alert>
                          ) : areasForSelectedDeck.length === 0 ? (
                            <div className="trip-cruise-deck__areas-empty">
                              Chưa có khu vực nào được cấu hình cho boong này.
                            </div>
                          ) : (
                            <div className="trip-cruise-deck__areas-list">
                              {areasForSelectedDeck.map((area, areaIndex) => (
                                <AreaCard
                                  key={
                                    area?.id ||
                                    area?.areaId ||
                                    `${deckId}-${areaIndex}`
                                  }
                                  area={area}
                                />
                              ))}
                            </div>
                          )}
                        </div>
                      )}
                    </article>
                  );
                })}
              </div>
            )}
          </div>
        </>
      )}
    </section>
  );
}
