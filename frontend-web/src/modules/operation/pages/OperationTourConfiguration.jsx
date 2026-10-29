import React from "react";
import {
  AlertCircle,
  ArrowLeft,
  RefreshCw,
  ShieldCheck,
  Save,
  Users,
  CheckCircle,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

import useTourConfigurationPage from "../hooks/useTourConfigurationPage";

import TourConfigurationSummary from "../components/tour-configuration/TourConfigurationSummary";
import ActivityCruiseConfigurationTable from "../components/tour-configuration/ActivityCruiseConfigurationTable";
import ActivityVisitConfigurationTable from "../components/tour-configuration/ActivityVisitConfigurationTable";
import ProductConfigurationTable from "../components/tour-configuration/ProductConfigurationTable";
import ServiceConfigurationTable from "../components/tour-configuration/ServiceConfigurationTable";

import "../styles/OperationTourConfiguration.css";

function ErrorState({ title, message, onBack }) {
  return (
    <div className="operation-tour-configuration-page">
      <div className="operation-tour-configuration-header">
        <button
          type="button"
          className="operation-tour-configuration-back"
          onClick={onBack}
        >
          <ArrowLeft size={18} />
          Quay lại
        </button>
      </div>

      <div className="operation-tour-configuration-error">
        <AlertCircle size={22} />
        <div>
          <strong>{title}</strong>
          <p>{message}</p>
        </div>
      </div>
    </div>
  );
}

function PatrolRequirementSection({ m }) {
  const totalRequired = m.patrolRequirements.reduce(
    (total, item) => total + (Number(item.requiredCount) || 0),
    0,
  );

  return (
    <section
      id="patrol-section"
      className="operation-tour-configuration-section otc-patrol"
    >
      <div className="otc-patrol-header">
        <div className="otc-patrol-heading">
          <div className="otc-patrol-icon">
            <ShieldCheck size={23} />
          </div>

          <div>
            <h2>Cấu hình nhân sự tuần tra</h2>
            <p>
              Thiết lập số lượng nhân viên ONBOARD cần bố trí tuần tra tại từng
              tầng của tàu.
            </p>
          </div>
        </div>

        <div className="otc-patrol-total">
          <Users size={19} />
          <div>
            <strong>{totalRequired}</strong>
            <span>Tổng nhân sự yêu cầu</span>
          </div>
        </div>
      </div>

      {m.patrolError && (
        <div className="otc-patrol-message otc-patrol-message--error">
          <AlertCircle size={18} />
          <span>{m.patrolError}</span>
        </div>
      )}

      {m.patrolSuccess && (
        <div className="otc-patrol-message otc-patrol-message--success">
          <CheckCircle size={18} />
          <span>{m.patrolSuccess}</span>
        </div>
      )}

      {m.patrolLoading ? (
        <div className="otc-patrol-loading">
          <RefreshCw size={19} className="operation-tour-configuration-spin" />
          <span>Đang tải danh sách tầng...</span>
        </div>
      ) : m.patrolRequirements.length === 0 ? (
        <div className="otc-patrol-empty">
          <AlertCircle size={20} />
          <div>
            <strong>Chưa có dữ liệu tầng tàu</strong>
            <p>
              Tour cần được gán tàu và tàu cần có tầng đang hoạt động (ACTIVE)
              trước khi cấu hình nhân sự tuần tra.
            </p>
          </div>
        </div>
      ) : (
        <>
          <div className="otc-patrol-table-wrapper">
            <table className="otc-patrol-table">
              <thead>
                <tr>
                  <th>Tầng tàu</th>
                  <th>Nhân sự cần thiết</th>
                </tr>
              </thead>

              <tbody>
                {m.patrolRequirements.map((item) => (
                  <tr key={item.cruiseDeckId}>
                    <td>
                      <div className="otc-patrol-deck">
                        <span className="otc-patrol-deck-icon">
                          <ShieldCheck size={17} />
                        </span>
                        <strong>Tầng {item.deckNumber}</strong>
                      </div>
                    </td>

                    <td>
                      <div className="otc-patrol-input-wrapper">
                        <input
                          type="number"
                          min="0"
                          step="1"
                          value={item.requiredCount ?? 0}
                          onChange={(event) =>
                            m.updatePatrolRequirement(
                              item.cruiseDeckId,
                              event.target.value,
                            )
                          }
                          disabled={m.patrolSaving}
                          aria-label={`Số nhân sự tuần tra tầng ${item.deckNumber}`}
                        />
                        <span>người</span>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="otc-patrol-footer">
            <p>
              Nhập 0 nếu tầng không cần bố trí nhân sự tuần tra. Hệ thống sẽ
              kiểm tra số nhân sự đã được ONBOARD phân công trước khi lưu.
            </p>

            <button
              type="button"
              className="otc-patrol-save"
              onClick={m.savePatrolRequirements}
              disabled={m.patrolSaving || m.patrolLoading}
            >
              {m.patrolSaving ? (
                <>
                  <RefreshCw
                    size={17}
                    className="operation-tour-configuration-spin"
                  />
                  Đang lưu...
                </>
              ) : (
                <>
                  <Save size={17} />
                  Lưu cấu hình tuần tra
                </>
              )}
            </button>
          </div>
        </>
      )}
    </section>
  );
}

const OperationTourConfiguration = () => {
  const navigate = useNavigate();
  const m = useTourConfigurationPage();

  const handleBack = () => navigate("/operation/tours");

  if (!m.tourId) {
    return (
      <ErrorState
        title="Thiếu Tour ID"
        message="Không xác định được Tour cần xem cấu hình."
        onBack={handleBack}
      />
    );
  }

  if (m.loading && !m.hasAnyData) {
    return (
      <div className="operation-tour-configuration-loading">
        <RefreshCw size={22} className="operation-tour-configuration-spin" />
        <span>Đang tải cấu hình Tour...</span>
      </div>
    );
  }

  if (m.error && !m.hasAnyData) {
    return (
      <ErrorState
        title="Không thể tải cấu hình Tour"
        message={m.error}
        onBack={handleBack}
      />
    );
  }

  const sections = [
    {
      key: "activityCruise",
      content: (
        <ActivityCruiseConfigurationTable activities={m.configuredActivities} />
      ),
    },
    {
      key: "activityVisit",
      content: (
        <ActivityVisitConfigurationTable
          activities={m.configuredActivityVisits}
        />
      ),
    },
    {
      key: "product",
      content: <ProductConfigurationTable products={m.configuredProducts} />,
    },
    {
      key: "service",
      content: <ServiceConfigurationTable services={m.configuredServices} />,
    },
  ];

  return (
    <div className="operation-tour-configuration-page">
      <div className="operation-tour-configuration-header">
        <div className="operation-tour-configuration-header-left">
          <button
            type="button"
            className="operation-tour-configuration-back"
            onClick={handleBack}
          >
            <ArrowLeft size={18} />
            Quay lại
          </button>

          <div className="operation-tour-configuration-title">
            <span>Cấu hình Tour</span>
            <h1>{m.tourId}</h1>
          </div>
        </div>

        <button
          type="button"
          className="operation-tour-configuration-refresh"
          onClick={m.refresh}
          disabled={m.loading || m.patrolLoading || m.patrolSaving}
        >
          <RefreshCw
            size={17}
            className={
              m.loading || m.patrolLoading
                ? "operation-tour-configuration-spin"
                : ""
            }
          />
          Làm mới
        </button>
      </div>

      <TourConfigurationSummary
        activityCruises={m.configuredActivities}
        activityVisits={m.configuredActivityVisits}
        products={m.configuredProducts}
        services={m.configuredServices}
        configurationComplete={m.configurationComplete}
        activeFilter={m.activeFilter}
        onFilterChange={m.handleFilterChange}
      />

      {/* Patrol requirement configuration */}
      <PatrolRequirementSection m={m} />

      <div className="operation-tour-configuration-tables">
        {sections
          .filter(
            ({ key }) => m.activeFilter === "ALL" || m.activeFilter === key,
          )
          .map(({ key, content }) => (
            <section
              key={key}
              id={`${key}-section`}
              className="operation-tour-configuration-section"
            >
              {content}
            </section>
          ))}
      </div>

      <section className="operation-tour-configuration-package">
        <div>
          <strong>Sẵn sàng tạo gói Tour & Mở bán vé?</strong>

          <p>
            Khi toàn bộ Activity, Product và Service đã được cấu hình đầy đủ,
            Operation có thể tạo các gói Tour hoặc tiến hành cấu hình thời gian
            mở bán vé cho tour này.
          </p>
        </div>

        <div className="operation-tour-configuration-package-actions">
          <button
            type="button"
            disabled={!m.configurationComplete}
            className="operation-tour-configuration-create-package"
            onClick={() =>
              navigate(`/operation/tour-packages?tourId=${m.tourId}`)
            }
          >
            Tạo gói Tour
          </button>

          <button
            type="button"
            className={`operation-tour-configuration-open-booking ${
              m.hasBookingConfig
                ? "operation-tour-configuration-open-booking--has-config"
                : ""
            }`}
            onClick={() =>
              navigate(`/operation/tour-booking-open?tourId=${m.tourId}`)
            }
          >
            {m.hasBookingConfig ? "Xem / Sửa cấu hình mở bán" : "Mở bán vé"}
          </button>
        </div>
      </section>
    </div>
  );
};

export default OperationTourConfiguration;
