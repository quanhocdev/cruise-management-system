// src/modules/scheduler/components/vessel/WeatherPanel.jsx
import React, { useState } from "react";
import {
  CloudSun,
  Sun,
  Cloud,
  CloudRain,
  CloudLightning,
  RefreshCw,
  Droplets,
  Wind,
  Waves,
} from "lucide-react";
import useWeatherForecast from "../../hooks/useWeatherForecast";
import "../../styles/WeatherPanel.css";

function pickIcon(text = "") {
  const t = text.toLowerCase();
  if (/giông|storm|thunder/.test(t)) return CloudLightning;
  if (/mưa|rain|drizzle|shower/.test(t)) return CloudRain;
  if (/nắng|quang|clear|sun/.test(t)) return Sun;
  if (/mây|cloud|overcast/.test(t)) return Cloud;
  return CloudSun;
}

const num = (v) => (v === null || v === undefined || v === "" ? null : v);

export default function WeatherPanel({ lat, lon, startDate, endDate }) {
  const [enabled, setEnabled] = useState(false);
  const { weather, loading, reloadWeather } = useWeatherForecast(
    enabled ? lat : null,
    enabled ? lon : null,
    startDate,
    endDate,
  );

  const handleClick = () => {
    if (enabled) reloadWeather();
    else setEnabled(true);
  };

  const temp = num(weather?.temperature ?? weather?.current?.temperature);
  const condition = weather?.condition || weather?.summary || "Bình thường";
  const humidity = num(weather?.humidity);
  const wind = num(weather?.windSpeed ?? weather?.wind?.speed);
  const wave = num(weather?.waveHeight);
  const daily = Array.isArray(weather?.daily) ? weather.daily : [];
  const Icon = pickIcon(condition);

  const metrics = [
    humidity !== null && {
      icon: Droplets,
      label: "Độ ẩm",
      value: `${humidity}%`,
    },
    wind !== null && { icon: Wind, label: "Gió", value: `${wind} km/h` },
    wave !== null && { icon: Waves, label: "Sóng", value: `${wave} m` },
  ].filter(Boolean);

  return (
    <section className="wp-panel">
      <div className="wp-head">
        <h3>
          <CloudSun size={18} /> Thời tiết khu vực tàu
        </h3>
        <button
          className="wp-btn"
          onClick={handleClick}
          disabled={loading || lat == null}
        >
          <RefreshCw size={14} className={loading ? "vm-spin" : ""} />
          <span>{weather ? "Cập nhật" : "Xem thời tiết"}</span>
        </button>
      </div>

      {lat == null && (
        <p className="wp-note">Chưa có tọa độ để tra cứu thời tiết.</p>
      )}

      {!weather && !loading && lat != null && (
        <p className="wp-note">
          Nhấn “Xem thời tiết” để lấy dự báo tại vị trí hiện tại của tàu
          (Open-Meteo).
        </p>
      )}

      {loading && <p className="wp-note">Đang tải dữ liệu thời tiết...</p>}

      {weather && !loading && (
        <>
          <div className="wp-now">
            <Icon size={56} strokeWidth={1.5} className="wp-icon" />
            <div>
              <div className="wp-temp">
                {temp ?? "N/A"}
                {temp !== null && <small>°C</small>}
              </div>
              <div className="wp-cond">{condition}</div>
            </div>
          </div>

          {metrics.length > 0 && (
            <div className="wp-metrics">
              {metrics.map(({ icon: M, label, value }) => (
                <div key={label}>
                  <M size={16} />
                  <span>{label}</span>
                  <strong>{value}</strong>
                </div>
              ))}
            </div>
          )}

          {daily.length > 0 && (
            <div className="wp-daily">
              {daily.slice(0, 7).map((d, i) => {
                const date = d.date ?? d.time;
                const max = d.tempMax ?? d.temperature_2m_max;
                const min = d.tempMin ?? d.temperature_2m_min;
                return (
                  <div key={date || i}>
                    <span>
                      {date
                        ? new Date(date).toLocaleDateString("vi-VN", {
                            day: "2-digit",
                            month: "2-digit",
                          })
                        : ""}
                    </span>
                    <strong>{max != null ? Math.round(max) + "°" : "—"}</strong>
                    <em>{min != null ? Math.round(min) + "°" : ""}</em>
                  </div>
                );
              })}
            </div>
          )}
        </>
      )}
    </section>
  );
}
