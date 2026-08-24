// // src/modules/onboard/pages/OnboardTour.jsx
// import React from "react";
// import { useOnboardTour } from "../hooks/useOnboardTour";
// import { OnboardToursTable } from "../components/OnboardToursTable";
// import "../styles/OnboardTour.css";

// const OnboardTour = () => {
//   const { tours, loading, error, refreshTours } = useOnboardTour();

//   return (
//     <div className="onboard-container">
//       <div className="onboard-header">
//         <h2>Danh Sách Tour Onboard Đã Duyệt</h2>
//         <button
//           className="btn-action"
//           onClick={refreshTours}
//           disabled={loading}
//         >
//           {loading ? "Đang tải..." : "Làm mới"}
//         </button>
//       </div>

//       {error && <div className="onboard-error">{error}</div>}

//       {loading ? (
//         <div className="onboard-loading">Đang tải dữ liệu...</div>
//       ) : (
//         <OnboardToursTable tours={tours} />
//       )}
//     </div>
//   );
// };

// export default OnboardTour;
