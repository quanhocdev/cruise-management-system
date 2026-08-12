package com.project.tour.model;

import com.project.tour.model.enums.PolicyStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cancel_policies")
public class CancelPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

}
