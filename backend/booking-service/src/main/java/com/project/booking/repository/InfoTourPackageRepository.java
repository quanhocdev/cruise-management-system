package com.project.booking.repository;

import com.project.booking.model.InfoTourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InfoTourPackageRepository extends JpaRepository<InfoTourPackage, UUID> {
}