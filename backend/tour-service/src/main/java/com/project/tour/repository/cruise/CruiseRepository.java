package com.project.tour.repository.cruise;

import com.project.tour.model.Cruise;
import com.project.tour.model.enums.cruise.CruiseStatus;

import org.springframework.data.jpa.repository.JpaRepository;

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
}