package com.findDream.repository;

import com.findDream.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);
    Optional<User> findByUserId(String userId); // 로그인 시 사용
}


