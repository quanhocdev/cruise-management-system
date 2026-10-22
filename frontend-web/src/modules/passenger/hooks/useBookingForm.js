import { useState, useEffect } from "react";

let slotCounter = 0;

const createPassenger = () => ({
  _key: `slot-${++slotCounter}`,
  fullName: "",
  dateOfBirth: "",
  gender: "Nam",
  phoneNumber: "",
  email: "",
  idCardType: "CCCD",
  identificationNumber: "",
  documentNote: "",
  idCardImage: null,
});

export default function useBookingForm({
  user,
  tour,
  packageIdFromUrl,
  savedPassengers,
}) {
  const [tourPackageId, setTourPackageId] = useState(packageIdFromUrl || "");
  const [numberOfRooms, setNumberOfRooms] = useState(1);
  const [primaryContactName, setPrimaryContactName] = useState(
    user?.fullName || "",
  );
  const [primaryContactPhone, setPrimaryContactPhone] = useState(
    user?.phone || "",
  );
  const [passengers, setPassengers] = useState(() => [createPassenger()]);

  // Mặc định chọn gói đầu tiên nếu chưa có gói nào được chọn
  useEffect(() => {
    if (!tourPackageId && tour?.packages?.length > 0) {
      setTourPackageId(tour.packages[0].id);
    }
  }, [tour, tourPackageId]);

  // ---- Giá trị suy ra ----
  const selectedPackage = tour?.packages?.find(
    (p) => String(p.id) === String(tourPackageId),
  );
  const maxPassengersPerRoom = Number(selectedPackage?.maxPassengers || 2);
  const totalAllowedPassengers = maxPassengersPerRoom * numberOfRooms;
  const isPassengerExceeded = passengers.length > totalAllowedPassengers;
  const totalPrice = Number(selectedPackage?.price || 0) * numberOfRooms;

  // ---- Handlers ----
  const changeRooms = (value) =>
    setNumberOfRooms(Math.max(1, Math.min(50, Number(value) || 1)));

  const updatePassenger = (index, field, value) =>
    setPassengers((prev) =>
      prev.map((p, i) => (i === index ? { ...p, [field]: value } : p)),
    );

  const addPassenger = () =>
    setPassengers((prev) => [...prev, createPassenger()]);

  const removePassenger = (index) =>
    setPassengers((prev) =>
      prev.length === 1 ? prev : prev.filter((_, i) => i !== index),
    );

  const fillFromSaved = (index, savedId) => {
    const found = savedPassengers.find((p) => p.id === Number(savedId));
    if (!found) return;
    setPassengers((prev) =>
      prev.map((p, i) =>
        i === index
          ? {
              ...p,
              fullName: found.fullName || "",
              dateOfBirth: found.dateOfBirth || "",
              gender: found.gender || "Nam",
              phoneNumber: found.phoneNumber || "",
              email: found.email || "",
              idCardType: found.idCardType || "CCCD",
              identificationNumber: found.identificationNumber || "",
              documentNote: found.documentNote || "",
              idCardImage: null,
            }
          : p,
      ),
    );
  };

  const buildFormData = (tourId) => {
    const formData = new FormData();
    formData.append("tourId", tourId);
    formData.append("tourPackageId", tourPackageId);
    formData.append("numberOfRooms", numberOfRooms);
    formData.append("primaryContactName", primaryContactName);
    formData.append("primaryContactPhone", primaryContactPhone);

    passengers.forEach((p, i) => {
      const prefix = `passengers[${i}]`;
      formData.append(`${prefix}.fullName`, p.fullName);
      formData.append(`${prefix}.dateOfBirth`, p.dateOfBirth);
      formData.append(`${prefix}.gender`, p.gender);
      if (p.phoneNumber)
        formData.append(`${prefix}.phoneNumber`, p.phoneNumber);
      if (p.email) formData.append(`${prefix}.email`, p.email);
      formData.append(`${prefix}.idCardType`, p.idCardType);
      formData.append(`${prefix}.identificationNumber`, p.identificationNumber);
      if (p.documentNote)
        formData.append(`${prefix}.documentNote`, p.documentNote);
      if (p.idCardImage)
        formData.append(`${prefix}.idCardImage`, p.idCardImage);
    });

    return formData;
  };

  return {
    tourPackageId,
    setTourPackageId,
    numberOfRooms,
    changeRooms,
    primaryContactName,
    setPrimaryContactName,
    primaryContactPhone,
    setPrimaryContactPhone,
    passengers,
    updatePassenger,
    addPassenger,
    removePassenger,
    fillFromSaved,
    selectedPackage,
    maxPassengersPerRoom,
    totalAllowedPassengers,
    isPassengerExceeded,
    totalPrice,
    buildFormData,
  };
}
