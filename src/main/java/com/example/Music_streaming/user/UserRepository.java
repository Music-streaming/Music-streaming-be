package com.example.Music_streaming.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<[어떤 엔티티], [ID의 타입]>
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 이메일로 사용자를 찾는 기능 (로그인 시 필요)
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    // EXISTS (SELECT 1 FROM users WHERE email = ?)
    boolean existsByEmail(String email);
}