// src/modules/admin/pages/Dashboard.jsx

import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Activity,
  Anchor,
  ArrowUpRight,
  CalendarDays,
  CheckCircle2,
  ChevronRight,
  Clock3,
  CreditCard,
  FileText,
  Ship,
  TriangleAlert,
  Users,
  Wallet,
} from "lucide-react";

import "../styles/Dashboard.css";

const AdminDashboard = () => {
  const navigate = useNavigate();

  // =====================================================
  // MOCK DATA
  // Sau này thay bằng dữ liệu từ API dashboard
  // =====================================================

  const stats = [
    {
      title: "Nhân viên",
      value: "24",
      change: "+2 tháng này",
      icon: Users,
      color: "blue",
      path: "/admin/accounts",
    },
    {
      title: "Du thuyền",
      value: "8",
      change: "7 đang hoạt động",
      icon: Ship,
      color: "cyan",
      path: "/admin/cruises",
    },
    {
      title: "Cảng",
      value: "32",
      change: "30 đang hoạt động",
      icon: Anchor,
      color: "green",
      path: "/admin/ports",
    },
    {
      title: "Lịch trình",
      value: "15",
      change: "12 sắp tới",
      icon: CalendarDays,
      color: "purple",
      path: "/admin/schedules",
    },
  ];

  const businessStats = [
    {
      title: "Booking",
      value: "1,284",
      change: "+12.5%",
      icon: CalendarDays,
      color: "blue",
    },
    {
      title: "Doanh thu",
      value: "2.84B",
      change: "+8.2%",
      icon: Wallet,
      color: "green",
    },
    {
      title: "Hoạt động",
      value: "126",
      change: "+5.4%",
      icon: Activity,
      color: "orange",
    },
    {
      title: "Thanh toán",
      value: "1,142",
      change: "+10.8%",
      icon: CreditCard,
      color: "purple",
    },
  ];

  const upcomingSchedules = [
    {
      date: "16/08",
      time: "08:30",
      route: "Ho Chi Minh → Singapore",
      ship: "Ocean Dream",
      status: "Đã xác nhận",
    },
    {
      date: "17/08",
      time: "09:00",
      route: "Singapore → Bangkok",
      ship: "Sea Pearl",
      status: "Đã xác nhận",
    },
    {
      date: "18/08",
      time: "07:30",
      route: "Bangkok → Phuket",
      ship: "Ocean Dream",
      status: "Đang chuẩn bị",
    },
    {
      date: "20/08",
      time: "10:00",
      route: "Phuket → Ho Chi Minh",
      ship: "Royal Ocean",
      status: "Đã xác nhận",
    },
  ];

  const recentActivities = [
    {
      icon: Anchor,
      title: 'Tạo cảng "Singapore Port"',
      description: "Admin",
      time: "10 phút trước",
      color: "green",
    },
    {
      icon: FileText,
      title: "Cập nhật chính sách hủy tour",
      description: "Admin",
      time: "32 phút trước",
      color: "blue",
    },
    {
      icon: Users,
      title: "Tạo tài khoản nhân viên mới",
      description: "Admin",
      time: "1 giờ trước",
      color: "purple",
    },
    {
      icon: Ship,
      title: 'Cập nhật du thuyền "Ocean Dream"',
      description: "Manager Cruise",
      time: "2 giờ trước",
      color: "cyan",
    },
    {
      icon: CheckCircle2,
      title: "Booking #BK-10284 đã thanh toán",
      description: "System",
      time: "3 giờ trước",
      color: "green",
    },
  ];

  const alerts = [
    {
      type: "warning",
      title: "Chính sách sắp hết hiệu lực",
      description: "3 chính sách cần được kiểm tra trong 7 ngày tới.",
      action: "Xem chính sách",
      path: "/admin/policies",
    },
    {
      type: "warning",
      title: "Du thuyền đang bảo trì",
      description: "2 du thuyền hiện không sẵn sàng khai thác.",
      action: "Xem du thuyền",
      path: "/admin/cruises",
    },
    {
      type: "info",
      title: "Booking đang chờ xử lý",
      description: "5 booking cần được kiểm tra.",
      action: "Xem booking",
      path: "/admin/bookings",
    },
  ];

  const quickActions = [
    {
      label: "Tạo nhân viên",
      icon: Users,
      path: "/admin/accounts",
    },
    {
      label: "Tạo cảng",
      icon: Anchor,
      path: "/admin/ports",
    },
    {
      label: "Tạo chính sách",
      icon: FileText,
      path: "/admin/policies",
    },
    {
      label: "Quản lý du thuyền",
      icon: Ship,
      path: "/admin/cruises",
    },
  ];

  return (
    <div className="admin-dashboard">
      {/* =================================================
          HEADER
         ================================================= */}

      <div className="dashboard-header">
        <div>
          <div className="dashboard-eyebrow">
            <span className="dashboard-eyebrow-dot" />
            ADMIN OVERVIEW
          </div>

          <h1 className="dashboard-title">Tổng quan hệ thống</h1>

          <p className="dashboard-description">
            Theo dõi tình trạng hoạt động và các chỉ số quan trọng của hệ thống
            Cruise.
          </p>
        </div>

        <div className="dashboard-date">
          <CalendarDays size={18} />

          <div>
            <span>Hôm nay</span>
            <strong>15 tháng 08, 2026</strong>
          </div>
        </div>
      </div>

      {/* =================================================
          MAIN STATS
         ================================================= */}

      <div className="dashboard-stats-grid">
        {stats.map((item) => {
          const Icon = item.icon;

          return (
            <button
              type="button"
              className="dashboard-stat-card"
              key={item.title}
              onClick={() => navigate(item.path)}
            >
              <div className={`dashboard-stat-icon ${item.color}`}>
                <Icon size={21} />
              </div>

              <div className="dashboard-stat-content">
                <span className="dashboard-stat-label">{item.title}</span>

                <strong className="dashboard-stat-value">{item.value}</strong>

                <span className="dashboard-stat-change">{item.change}</span>
              </div>

              <ArrowUpRight className="dashboard-stat-arrow" size={18} />
            </button>
          );
        })}
      </div>

      {/* =================================================
          BUSINESS STATS
         ================================================= */}

      <section className="dashboard-section">
        <div className="dashboard-section-heading">
          <div>
            <h2>Hoạt động kinh doanh</h2>

            <p>Tổng quan các chỉ số hoạt động hiện tại.</p>
          </div>
        </div>

        <div className="dashboard-business-grid">
          {businessStats.map((item) => {
            const Icon = item.icon;

            return (
              <div className="dashboard-business-card" key={item.title}>
                <div className={`dashboard-business-icon ${item.color}`}>
                  <Icon size={19} />
                </div>

                <div>
                  <span>{item.title}</span>

                  <strong>{item.value}</strong>

                  <small>
                    <ArrowUpRight size={13} />
                    {item.change} so với tháng trước
                  </small>
                </div>
              </div>
            );
          })}
        </div>
      </section>

      {/* =================================================
          MIDDLE GRID
         ================================================= */}

      <div className="dashboard-middle-grid">
        {/* =================================================
            UPCOMING SCHEDULE
           ================================================= */}

        <section className="dashboard-card">
          <div className="dashboard-card-header">
            <div>
              <h3>Lịch trình sắp tới</h3>

              <p>Các chuyến gần nhất trong hệ thống</p>
            </div>

            <button
              type="button"
              className="dashboard-link-button"
              onClick={() => navigate("/admin/schedules")}
            >
              Xem tất cả
              <ChevronRight size={16} />
            </button>
          </div>

          <div className="schedule-list">
            {upcomingSchedules.map((schedule) => (
              <div
                className="schedule-item"
                key={`${schedule.date}-${schedule.route}`}
              >
                <div className="schedule-date">
                  <strong>{schedule.date}</strong>
                  <span>{schedule.time}</span>
                </div>

                <div className="schedule-info">
                  <strong>{schedule.route}</strong>

                  <span>
                    <Ship size={14} />
                    {schedule.ship}
                  </span>
                </div>

                <span
                  className={`schedule-status ${
                    schedule.status === "Đã xác nhận"
                      ? "confirmed"
                      : "preparing"
                  }`}
                >
                  {schedule.status}
                </span>
              </div>
            ))}
          </div>
        </section>

        {/* =================================================
            QUICK ACTIONS
           ================================================= */}

        <section className="dashboard-card">
          <div className="dashboard-card-header">
            <div>
              <h3>Truy cập nhanh</h3>

              <p>Các chức năng thường sử dụng</p>
            </div>
          </div>

          <div className="quick-actions">
            {quickActions.map((action) => {
              const Icon = action.icon;

              return (
                <button
                  type="button"
                  className="quick-action"
                  key={action.label}
                  onClick={() => navigate(action.path)}
                >
                  <span className="quick-action-icon">
                    <Icon size={18} />
                  </span>

                  <span>{action.label}</span>

                  <ChevronRight size={16} />
                </button>
              );
            })}
          </div>
        </section>
      </div>

      {/* =================================================
          LOWER GRID
         ================================================= */}

      <div className="dashboard-lower-grid">
        {/* =================================================
            RECENT ACTIVITIES
           ================================================= */}

        <section className="dashboard-card">
          <div className="dashboard-card-header">
            <div>
              <h3>Hoạt động gần đây</h3>

              <p>Các thay đổi mới nhất trong hệ thống</p>
            </div>

            <Clock3 size={19} />
          </div>

          <div className="activity-list">
            {recentActivities.map((activity) => {
              const Icon = activity.icon;

              return (
                <div className="activity-item" key={activity.title}>
                  <div className={`activity-icon ${activity.color}`}>
                    <Icon size={16} />
                  </div>

                  <div className="activity-content">
                    <strong>{activity.title}</strong>

                    <span>
                      {activity.description} · {activity.time}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </section>

        {/* =================================================
            ALERTS
           ================================================= */}

        <section className="dashboard-card">
          <div className="dashboard-card-header">
            <div>
              <h3>Cần chú ý</h3>

              <p>Các vấn đề cần Admin xử lý</p>
            </div>

            <TriangleAlert size={19} className="dashboard-warning-icon" />
          </div>

          <div className="alert-list">
            {alerts.map((alert) => (
              <div
                className={`dashboard-alert ${alert.type}`}
                key={alert.title}
              >
                <div className="dashboard-alert-icon">
                  {alert.type === "warning" ? (
                    <TriangleAlert size={17} />
                  ) : (
                    <Activity size={17} />
                  )}
                </div>

                <div className="dashboard-alert-content">
                  <strong>{alert.title}</strong>

                  <p>{alert.description}</p>

                  <button type="button" onClick={() => navigate(alert.path)}>
                    {alert.action}
                    <ChevronRight size={14} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>

      {/* =================================================
          SYSTEM STATUS
         ================================================= */}

      <section className="dashboard-system-status">
        <div className="system-status-left">
          <span className="system-status-indicator" />

          <div>
            <strong>Hệ thống đang hoạt động bình thường</strong>

            <span>Tất cả các dịch vụ chính đang sẵn sàng.</span>
          </div>
        </div>

        <div className="system-status-items">
          <span>
            <CheckCircle2 size={15} />
            API
          </span>

          <span>
            <CheckCircle2 size={15} />
            Database
          </span>

          <span>
            <CheckCircle2 size={15} />
            Payment
          </span>

          <span>
            <CheckCircle2 size={15} />
            Email
          </span>
        </div>
      </section>
    </div>
  );
};

export default AdminDashboard;
