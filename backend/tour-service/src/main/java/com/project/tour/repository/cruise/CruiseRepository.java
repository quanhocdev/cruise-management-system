package com.project.tour.repository.cruise;

import com.project.tour.model.Cruise;
import com.project.tour.model.enums.cruise.CruiseStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CruiseRepository
                extends JpaRepository<Cruise, UUID> {

        /*
         * Kiểm tra code đã tồn tại
         */
        boolean existsByCodeIgnoreCase(
                        String code);

        /*
         * Kiểm tra code đã tồn tại
         * nhưng loại trừ Cruise đang update
         */
        boolean existsByCodeIgnoreCaseAndIdNot(
                        String code,
                        UUID id);

        /*
         * Tìm Cruise theo code
         */
        Optional<Cruise> findByCodeIgnoreCase(
                        String code);

        /*
         * Lấy danh sách Cruise theo status
         * và sắp xếp theo tên tăng dần
         */
        List<Cruise> findAllByStatusOrderByNameAsc(
                        CruiseStatus status);

        /*
         * =====================================================
         * LẤY CRUISE KHẢ DỤNG CHO TOUR
         * =====================================================
         *
         * Cruise phải:
         *
         * 1. ACTIVE
         * 2. Không có Tour APPROVED / IN_PROGRESS
         * bị trùng khoảng thời gian.
         *
         * Điều kiện trùng:
         *
         * existing.startDate <= new.endDate
         * AND
         * existing.endDate >= new.startDate
         */
        @Query("""
                        SELECT c
                        FROM Cruise c
                        WHERE c.status = :status
                          AND NOT EXISTS (
                              SELECT t.id
                              FROM Tour t
                              WHERE t.cruise.id = c.id
                                AND t.statusTrip IN :statuses
                                AND t.startDate <= :endDate
                                AND t.endDate >= :startDate
                          )
                        ORDER BY c.name ASC
                        """)
        List<Cruise> findAvailableCruises(
                        @Param("status") CruiseStatus status,
                        @Param("statuses") List<TourStatusTrip> statuses,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}