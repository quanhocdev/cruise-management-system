// src/components/scheduler/SchedulerHeader.jsx

import { UserCircle } from "lucide-react";

import NotificationBell from "../admin/NotificationBell";
import "../../styles/scheduler/SchedulerHeader.css";

function SchedulerHeader() {
  return (
    <header className="scheduler-header">
      <div className="scheduler-header-left">
        <h1>Scheduler</h1>
      </div>

      <div className="scheduler-header-right">
        <NotificationBell />

        <div className="scheduler-header-user">
          <UserCircle size={32} />

          <div className="scheduler-header-user-info">
            <span className="scheduler-header-user-name">Scheduler</span>

            <span className="scheduler-header-user-role">Tour Scheduler</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default SchedulerHeader;
