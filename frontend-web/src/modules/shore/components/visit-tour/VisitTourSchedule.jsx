// // src/modules/shore/components/visit-tour/VisitTourSchedule.jsx
// import VisitTourStop from "./VisitTourStop";
// import "../../styles/VisitTourSchedule.css";
// function VisitTourSchedule({
//   schedule,
//   onCreate,
//   onEdit,
//   onDelete,
//   onStatusChange,
// }) {
//   return (
//     <section className="visit-tour-schedule">
//       {/* =================================================
//           SCHEDULE HEADER
//           ================================================= */}

//       <div className="visit-tour-schedule-header">
//         <div className="visit-tour-schedule-day">Ngày {schedule.dayNumber}</div>

//         <div className="visit-tour-schedule-info">
//           <h2>{schedule.scheduleName}</h2>

//           <span>{schedule.realDay}</span>
//         </div>
//       </div>

//       {/* =================================================
//           STOPS
//           ================================================= */}

//       <div className="visit-tour-schedule-stops">
//         {!schedule.stops || schedule.stops.length === 0 ? (
//           <div className="visit-tour-schedule-empty">
//             Ngày này chưa có điểm dừng.
//           </div>
//         ) : (
//           schedule.stops.map((stop) => (
//             <VisitTourStop
//               key={stop.scheduleStopId}
//               stop={stop}
//               onCreate={onCreate}
//               onEdit={onEdit}
//               onDelete={onDelete}
//               onStatusChange={onStatusChange}
//             />
//           ))
//         )}
//       </div>
//     </section>
//   );
// }

// export default VisitTourSchedule;
