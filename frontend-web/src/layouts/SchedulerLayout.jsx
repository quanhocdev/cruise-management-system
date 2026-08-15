// src/ layouts/SchedulerLayout.jsx
// src/layouts/SchedulerLayout.jsx

import SchedulerSidebar from "../components/scheduler/SchedulerSidebar";
import SchedulerHeader from "../components/scheduler/SchedulerHeader";
import SchedulerFooter from "../components/scheduler/SchedulerFooter";

import "./SchedulerLayout.css";

function SchedulerLayout({ children }) {
  return (
    <div className="scheduler-layout">
      <SchedulerSidebar />

      <div className="scheduler-layout-main">
        <SchedulerHeader />

        <main className="scheduler-layout-content">{children}</main>

        <SchedulerFooter />
      </div>
    </div>
  );
}

export default SchedulerLayout;
