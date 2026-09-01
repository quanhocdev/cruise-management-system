// //src/moduler/shore/components/visit-tour/VisitTourFilter.jsx
// import "../../styles/VisitTourFilter.css";
// function VisitTourFilter({ status, onStatusChange, statusOptions }) {
//   return (
//     <section className="visit-tour-filter">
//       <div className="visit-tour-filter-header">
//         <div>
//           <h3>Lọc Visit Tour</h3>

//           <p>Lọc danh sách theo trạng thái hiện tại.</p>
//         </div>
//       </div>

//       <div className="visit-tour-filter-body">
//         <label htmlFor="visit-tour-status-filter">Trạng thái</label>

//         <select
//           id="visit-tour-status-filter"
//           value={status}
//           onChange={(event) => onStatusChange(event.target.value)}
//         >
//           {statusOptions.map((option) => (
//             <option key={option.value} value={option.value}>
//               {option.label}
//             </option>
//           ))}
//         </select>
//       </div>
//     </section>
//   );
// }

// export default VisitTourFilter;
