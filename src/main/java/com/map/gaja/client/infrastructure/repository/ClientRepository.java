package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
