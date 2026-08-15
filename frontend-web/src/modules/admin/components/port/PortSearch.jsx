// src/modules/admin/components/port/PortSearch.jsx

import { useEffect, useRef, useState } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import { Search, X } from "lucide-react";

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;

export default function PortSearch({ onLocationSelect, disabled = false }) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const wrapperRef = useRef(null);

  useEffect(() => {
    if (!query.trim()) {
      setResults([]);
      setError("");
      return;
    }

    const controller = new AbortController();

    const timer = setTimeout(async () => {
      setLoading(true);
      setError("");

      try {
        if (!MAPBOX_TOKEN) {
          throw new Error("Chưa cấu hình VITE_MAPBOX_PUBLIC_TOKEN.");
        }

        const url =
          "https://api.mapbox.com/search/geocode/v6/forward?" +
          new URLSearchParams({
            q: query,
            access_token: MAPBOX_TOKEN,
            language: "vi",
            limit: "5",
            country: "VN",
          });

        const response = await fetch(url, {
          signal: controller.signal,
        });

        if (!response.ok) {
          throw new Error("Không thể tìm kiếm địa chỉ trên Mapbox.");
        }

        const data = await response.json();

        setResults(data.features || []);
      } catch (err) {
        if (err.name === "AbortError") {
          return;
        }

        console.error("Mapbox search error:", err);

        setResults([]);
        setError(err.message || "Không thể tìm kiếm địa chỉ.");
      } finally {
        setLoading(false);
      }
    }, 350);

    return () => {
      clearTimeout(timer);
      controller.abort();
    };
  }, [query]);

  const handleSelect = (feature) => {
    const coordinates = feature?.geometry?.coordinates;

    if (!Array.isArray(coordinates) || coordinates.length < 2) {
      return;
    }

    const [longitude, latitude] = coordinates;

    onLocationSelect({
      latitude,
      longitude,
      placeName:
        feature.properties?.full_address ||
        feature.properties?.name ||
        feature.place_name ||
        "",
      feature,
    });

    setQuery(
      feature.properties?.full_address ||
        feature.properties?.name ||
        feature.place_name ||
        "",
    );

    setResults([]);
  };

  const handleClear = () => {
    setQuery("");
    setResults([]);
    setError("");
  };

  return (
    <div ref={wrapperRef} className="port-search">
      <div className="port-search-input-wrapper">
        <Search size={19} className="port-search-icon" />

        <Form.Control
          type="text"
          value={query}
          disabled={disabled}
          placeholder="Tìm kiếm địa chỉ, thành phố, cảng..."
          className="port-search-input"
          onChange={(event) => setQuery(event.target.value)}
        />

        {loading && (
          <Spinner
            animation="border"
            size="sm"
            className="port-search-spinner"
          />
        )}

        {!loading && query && (
          <Button
            variant="link"
            className="port-search-clear"
            onClick={handleClear}
            disabled={disabled}
          >
            <X size={18} />
          </Button>
        )}
      </div>

      {error && <div className="port-search-error">{error}</div>}

      {results.length > 0 && (
        <div className="port-search-results">
          {results.map((feature) => {
            const name = feature.properties?.name || feature.text || "Địa điểm";

            const address =
              feature.properties?.full_address || feature.place_name || "";

            return (
              <button
                key={feature.id}
                type="button"
                className="port-search-result"
                onClick={() => handleSelect(feature)}
              >
                <div className="port-search-result-title">{name}</div>

                <div className="port-search-result-address">{address}</div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
