package com.project.booking.repository;

import com.project.booking.model.PosTerminal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PosTerminalRepository extends JpaRepository<PosTerminal, Long> {
    Optional<PosTerminal> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    List<PosTerminal> findAllByOrderByCodeAsc();
}
