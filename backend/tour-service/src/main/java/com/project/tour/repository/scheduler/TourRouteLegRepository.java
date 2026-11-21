package com.project.tour.repository.scheduler;

import com.project.tour.model.scheduler.TourRouteLeg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TourRouteLegRepository extends JpaRepository<TourRouteLeg, UUID> {

    // Lấy danh sách tất cả các chặng của một route được sắp xếp theo thứ tự hành
    // trình
    List<TourRouteLeg> findAllByTourRoute_IdOrderByLegOrderAsc(UUID tourRouteId);

    // Xóa các chặng cũ (nếu có tính năng tính toán lại lộ trình) trước khi lưu mới
    void deleteByTourRoute_Id(UUID tourRouteId);
}