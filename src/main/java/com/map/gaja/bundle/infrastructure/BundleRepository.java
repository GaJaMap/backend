package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
}
