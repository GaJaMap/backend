package com.map.gaja.user.infrastructure;

import com.map.gaja.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmail(@Param(value = "email") String email);
}
