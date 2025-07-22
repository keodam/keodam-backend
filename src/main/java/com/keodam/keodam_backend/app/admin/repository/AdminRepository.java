package com.keodam.keodam_backend.app.admin.repository;

import com.keodam.keodam_backend.app.admin.domain.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
}
