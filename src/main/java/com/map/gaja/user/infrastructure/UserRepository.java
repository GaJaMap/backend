package com.map.gaja.user.infrastructure;

import com.map.gaja.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * active가 활성화된 유저의 이메일 조회
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmailAndActive(@Param(value = "email") String email);

    /**
     * 회원탈퇴한 user 전부 삭제
     */
    @Modifying
    @Query(value = "DELETE FROM User u WHERE u.active = false")
    int deleteWithdrawnUsers();

    /**
     * 이메일로 유저 조회
     */
    User findByEmail(String email);
}
