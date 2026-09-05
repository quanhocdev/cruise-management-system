package com.project.tour.service.tour;

import com.project.tour.dto.tour.packages.PackageBenefitRequest;
import com.project.tour.dto.tour.packages.TourPackageRequest;
import com.project.tour.dto.tour.packages.TourPackageResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourPackageMapper;
import com.project.tour.model.PackageBenefit;
import com.project.tour.model.TourPackage;
import com.project.tour.model.Tour;
import com.project.tour.model.RoomType;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.repository.tour.PackageBenefitRepository;
import com.project.tour.repository.tour.TourPackageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.room.RoomTypeRepository;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final TourRepository tourRepository;
    private final RoomTypeRepository roomTypeRepository;

    public TourPackageService(
            TourPackageRepository tourPackageRepository,
            PackageBenefitRepository packageBenefitRepository,
            TourRepository tourRepository,
            RoomTypeRepository roomTypeRepository) {
        this.tourPackageRepository = tourPackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.tourRepository = tourRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    // =========================================================
    // TẠO MỚI GÓI TOUR
    // =========================================================
    public TourPackageResponse createPackage(TourPackageRequest request) {
        // Kiểm tra tên gói có bị trùng trong cùng một tour hay không
        boolean exists = tourPackageRepository.existsByTourIdAndName(request.tourId(), request.name());
        if (exists) {
            throw new AppException("A package with this name already exists for this tour", HttpStatus.BAD_REQUEST);
        }

        // 1. Lưu TourPackage
        TourPackage tourPackage = new TourPackage();
        tourPackage.setTourId(request.tourId());
        tourPackage.setRoomTypeId(request.roomTypeId());
        tourPackage.setName(request.name());
        tourPackage.setDescription(request.description());
        tourPackage.setPrice(request.price());
        tourPackage.setMaxPassengers(request.maxPassengers());
        tourPackage.setStatus(request.status());

        TourPackage savedPackage = tourPackageRepository.save(tourPackage);

        // 2. Lưu danh sách quyền lợi (PackageBenefit) nếu có
        List<PackageBenefit> savedBenefits = List.of();
        if (request.benefits() != null && !request.benefits().isEmpty()) {
            List<PackageBenefit> benefits = request.benefits().stream().map(dto -> {
                PackageBenefit benefit = new PackageBenefit();
                benefit.setTourPackageId(savedPackage.getId());
                benefit.setType(dto.type());
                benefit.setReferenceId(dto.referenceId());
                benefit.setQuantity(dto.quantity());
                benefit.setDiscountPercent(dto.discountPercent());
                return benefit;
            }).toList();

            savedBenefits = packageBenefitRepository.saveAll(benefits);
        }

        return TourPackageMapper.toResponse(savedPackage, savedBenefits);
    }

    public TourPackageResponse patchPackage(UUID packageId, TourPackageRequest request) {
        TourPackage tourPackage = tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException("Tour package not found", HttpStatus.NOT_FOUND));

        // Cập nhật các trường nếu có truyền lên (không null)
        if (request.roomTypeId() != null) {
            tourPackage.setRoomTypeId(request.roomTypeId());
        }
        if (request.name() != null) {
            boolean exists = tourPackageRepository.existsByTourIdAndName(tourPackage.getTourId(), request.name());
            if (exists && !tourPackage.getName().equals(request.name())) {
                throw new AppException("A package with this name already exists for this tour", HttpStatus.BAD_REQUEST);
            }
            tourPackage.setName(request.name());
        }
        if (request.description() != null) {
            tourPackage.setDescription(request.description());
        }
        if (request.price() != null) {
            tourPackage.setPrice(request.price());
        }
        if (request.maxPassengers() != null) {
            tourPackage.setMaxPassengers(request.maxPassengers());
        }
        if (request.status() != null) {
            tourPackage.setStatus(request.status());
        }

        TourPackage updatedPackage = tourPackageRepository.save(tourPackage);

        // Nếu request có truyền danh sách benefits mới, tiến hành đồng bộ (xóa cũ, thêm
        // mới)
        List<PackageBenefit> savedBenefits = packageBenefitRepository.findAllByTourPackageId(updatedPackage.getId());
        if (request.benefits() != null) {
            packageBenefitRepository.deleteAllByTourPackageId(updatedPackage.getId());

            List<PackageBenefit> newBenefits = request.benefits().stream().map(dto -> {
                PackageBenefit benefit = new PackageBenefit();
                benefit.setTourPackageId(updatedPackage.getId());
                benefit.setType(dto.type());
                benefit.setReferenceId(dto.referenceId());
                benefit.setQuantity(dto.quantity());
                benefit.setDiscountPercent(dto.discountPercent());
                return benefit;
            }).toList();

            savedBenefits = packageBenefitRepository.saveAll(newBenefits);
        }

        return TourPackageMapper.toResponse(updatedPackage, savedBenefits);
    }

    // =========================================================
    // LẤY DANH SÁCH GÓI TOUR THEO TOUR ID
    // =========================================================
    @Transactional(readOnly = true)
    public List<TourPackageResponse> getPackagesByTourId(UUID tourId) {
        List<TourPackage> packages = tourPackageRepository.findAllByTourId(tourId);

        return packages.stream().map(pkg -> {
            List<PackageBenefit> benefits = packageBenefitRepository.findAllByTourPackageId(pkg.getId());
            return TourPackageMapper.toResponse(pkg, benefits);
        }).toList();
    }

    // =========================================================
    // XÓA GÓI TOUR
    // =========================================================
    public void deletePackage(UUID packageId) {
        TourPackage pkg = tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException("Tour package not found", HttpStatus.NOT_FOUND));

        // Xóa các quyền lợi đi kèm trước để tránh khóa ngoại (nếu DB chưa set Cascade
        // Delete)
        packageBenefitRepository.deleteAllByTourPackageId(pkg.getId());

        // Xóa gói tour
        tourPackageRepository.delete(pkg);
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getRoomTypesByTourId(UUID tourId) {
        // 1. Tìm Tour để lấy thông tin Cruise được gán
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

        if (tour.getCruise() == null) {
            throw new AppException("This tour does not have an assigned cruise yet", HttpStatus.BAD_REQUEST);
        }

        UUID cruiseId = tour.getCruise().getId();

        // 2. Lấy danh sách các RoomType thuộc con tàu này
        List<RoomType> roomTypes = roomTypeRepository.findRoomTypesByCruiseId(cruiseId);

        // 3. Map sang RoomTypeResponse DTO
        return roomTypes.stream().map(rt -> {
            com.project.tour.dto.roomtype.RoomTypeResponse dto = new com.project.tour.dto.roomtype.RoomTypeResponse();
            dto.setId(rt.getId());
            dto.setName(rt.getName());
            dto.setDescription(rt.getDescription());
            return dto;
        }).toList();
    }
}