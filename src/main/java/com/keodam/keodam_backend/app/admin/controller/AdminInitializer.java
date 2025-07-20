package com.keodam.keodam_backend.app.admin.controller;

import com.keodam.keodam_backend.app.admin.domain.Admin;
import com.keodam.keodam_backend.app.admin.domain.RoleTypeAdmin;
import com.keodam.keodam_backend.app.admin.repository.AdminRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer { // 실제 운영환경서는 삭제할 코드임.

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initSuperAdmin() {
        String email = "superadmin@example.com";
        String password = "SuperTemp1234";

        if (adminRepository.findByEmail(email).isPresent()) {
            return;
        }

        try {
            Admin superAdmin = Admin.builder()
                    .name("슈퍼관리자")
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .roleType(RoleTypeAdmin.SUPER_ADMIN)
                    .build();

            adminRepository.save(superAdmin);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
