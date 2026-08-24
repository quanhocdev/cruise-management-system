// // src/modules/onboard/hooks/useOnboardTour.js
// import { useState, useEffect, useCallback } from "react";
// import { fetchApprovedTours } from "../services/onboardTourService";

// export const useOnboardTour = () => {
//   const [tours, setTours] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   const loadApprovedTours = useCallback(async () => {
//     setLoading(true);
//     setError(null);
//     try {
//       const data = await fetchApprovedTours();
//       setTours(data);
//     } catch (err) {
//       setError(
//         err.response?.data?.message || "Không thể tải danh sách tour đã duyệt",
//       );
//     } finally {
//       setLoading(false);
//     }
//   }, []);

//   useEffect(() => {
//     loadApprovedTours();
//   }, [loadApprovedTours]);

//   return {
//     tours,
//     loading,
//     error,
//     refreshTours: loadApprovedTours,
//   };
// };
