// package com.project.tour.repository.product;

// import com.project.tour.model.ProductCruiseTour;
// import com.project.tour.model.enums.onboard.ProductCruiseTourStatus;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// public interface ProductCruiseTourRepository extends
// JpaRepository<ProductCruiseTour, UUID> {

// // Kiểm tra xem Tour + Khu vực này đã được phân công sản phẩm cụ thể này chưa
// boolean existsByTour_IdAndCruiseArea_IdAndProduct_Id(
// UUID tourId,
// UUID cruiseAreaId,
// UUID productId);

// // Tìm chi tiết 1 phân công theo Tour + Khu vực + Sản phẩm
// Optional<ProductCruiseTour> findByTour_IdAndCruiseArea_IdAndProduct_Id(
// UUID tourId,
// UUID cruiseAreaId,
// UUID productId);

// // Lấy danh sách tất cả sản phẩm được phân công trong 1 Tour
// List<ProductCruiseTour> findAllByTour_IdOrderByCreatedAtDesc(UUID tourId);

// // Lấy danh sách sản phẩm theo Tour và Khu vực cụ thể (VD: Lấy toàn bộ mặt
// hàng
// // ở Cửa hàng tiện lợi của Tour X)
// List<ProductCruiseTour> findAllByTour_IdAndCruiseArea_IdOrderByCreatedAtDesc(
// UUID tourId,
// UUID cruiseAreaId);

// // Lấy danh sách phân công theo Trạng thái trong Tour (VD: Lọc các phân công
// // đang WAITING_CONFIG)
// List<ProductCruiseTour> findAllByTour_IdAndStatusOrderByCreatedAtDesc(
// UUID tourId,
// ProductCruiseTourStatus status);

// // Xóa tất cả sản phẩm phân công của 1 khu vực trong Tour
// void deleteAllByTour_IdAndCruiseArea_Id(UUID tourId, UUID cruiseAreaId);
// }