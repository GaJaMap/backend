package com.map.gaja.user.infrastructure;

import com.map.gaja.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
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
    @Transactional
    int deleteWithdrawnUsers();

    /**
     * 이메일로 유저 조회
     */
    User findByEmail(String email);

    /**
     * CUD를 위한 유저 lock 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.active = true")
    Optional<User> findByEmailAndActiveForUpdate(@Param(value = "userId") Long userId);
}
