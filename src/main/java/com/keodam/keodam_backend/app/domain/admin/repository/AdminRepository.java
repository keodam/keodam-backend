package com.keodam.keodam_backend.app.domain.admin.repository;

import com.keodam.keodam_backend.app.domain.admin.domain.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByRefreshToken(String refreshToken);
    boolean existsByEmail(String email);
    Admin findByName(String name);
}
