// package com.project.common.event;

// import java.time.Instant;
// import java.util.UUID;

// public record ProductTourAssignedEvent(
// UUID tourId,
// UUID cruiseAreaId,
// String areaType,
// String action, // CREATE, UPDATE, DELETE
// String timestamp) {

// // Constructor mở rộng nhận hành động cụ thể (CREATE, UPDATE, DELETE)
// public ProductTourAssignedEvent(UUID tourId, UUID cruiseAreaId, String
// areaType, String action) {
// this(tourId, cruiseAreaId, areaType, action, Instant.now().toString());
// }

// // Constructor khi gán/tạo mới (mặc định action = CREATE)
// public ProductTourAssignedEvent(UUID tourId, UUID cruiseAreaId, String
// areaType) {
// this(tourId, cruiseAreaId, areaType, "CREATE", Instant.now().toString());
// }

// // Constructor tiện lợi mặc định "PRODUCT" và "CREATE"
// public ProductTourAssignedEvent(UUID tourId, UUID cruiseAreaId) {
// this(tourId, cruiseAreaId, "PRODUCT", "CREATE", Instant.now().toString());
// }
// }
// // mvn clean install -DskipTests