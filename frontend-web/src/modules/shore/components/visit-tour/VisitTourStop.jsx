// // src/modules/shore/components/visit-tour/VisitTourStop.jsx
// import { Plus } from "lucide-react";

// import VisitTourCard from "./VisitTourCard";
// import "../../styles/VisitTourStop.css";
// function VisitTourStop({ stop, onCreate, onEdit, onDelete, onStatusChange }) {
//   return (
//     <section className="visit-tour-stop">
//       {/* =================================================
//           STOP HEADER
//           ================================================= */}

//       <div className="visit-tour-stop-header">
//         <div className="visit-tour-stop-info">
//           <div className="visit-tour-stop-order">Điểm {stop.stopOrder}</div>

//           <div className="visit-tour-stop-main">
//             <h3>{stop.portName}</h3>

//             <div className="visit-tour-stop-time">
//               <span>{stop.arriveAt}</span>

//               <span>→</span>

//               <span>{stop.leaveAt}</span>
//             </div>
//           </div>
//         </div>

//         <button
//           type="button"
//           className="visit-tour-stop-add"
//           onClick={() => onCreate(stop)}
//         >
//           <Plus size={17} />

//           <span>Thêm Visit Tour</span>
//         </button>
//       </div>

//       {/* =================================================
//           VISIT TOURS
//           ================================================= */}

//       <div className="visit-tour-stop-list">
//         {!stop.visitTours || stop.visitTours.length === 0 ? (
//           <div className="visit-tour-stop-empty">
//             <span>Chưa có Visit Tour nào được cấu hình tại điểm dừng này.</span>

//             <button type="button" onClick={() => onCreate(stop)}>
//               Thêm Visit Tour
//             </button>
//           </div>
//         ) : (
//           stop.visitTours.map((visitTour) => (
//             <VisitTourCard
//               key={visitTour.id}
//               visitTour={visitTour}
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

// export default VisitTourStop;
