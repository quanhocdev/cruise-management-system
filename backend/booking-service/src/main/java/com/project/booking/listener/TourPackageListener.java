package com.project.booking.listener;

import com.project.booking.model.InfoTourPackage;
import com.project.booking.repository.InfoTourPackageRepository;
import com.project.common.event.TourPackageSyncedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TourPackageListener {

    private final InfoTourPackageRepository repository;

    public TourPackageListener(InfoTourPackageRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "tour-package-sync-topic", groupId = "booking-service-group")
    public void consume(TourPackageSyncedEvent event) {
        if ("DELETED".equals(event.getStatus())) {
            repository.deleteById(event.getId());
            System.out.println(">>> [KAFKA CONSUMER] Đã xóa gói tour cục bộ: " + event.getId());
            return;
        }

        InfoTourPackage pkg = repository.findById(event.getId()).orElse(new InfoTourPackage());
        pkg.setId(event.getId());
        pkg.setTourId(event.getTourId());
        pkg.setName(event.getName());
        pkg.setPrice(event.getPrice());
        pkg.setMaxPassengers(event.getMaxPassengers());
        pkg.setStatus(event.getStatus());

        repository.save(pkg);
        System.out.println(">>> [KAFKA CONSUMER] Đã đồng bộ thành công gói tour: " + event.getName());
    }
}