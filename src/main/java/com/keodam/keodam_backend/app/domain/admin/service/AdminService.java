package com.keodam.keodam_backend.app.domain.admin.service;

import com.keodam.keodam_backend.app.domain.admin.domain.RoleTypeAdmin;
import com.keodam.keodam_backend.app.domain.admin.domain.Admin;
import com.keodam.keodam_backend.app.domain.admin.dto.AdminResponseDto;
import com.keodam.keodam_backend.app.domain.admin.repository.AdminRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin getByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Transactional
    public void register(String name, String email, String rawPassword) {
        if (adminRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorStatus.ALREADY_REGISTER_ADMIN);
        }

        Admin newAdmin = Admin.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .roleType(RoleTypeAdmin.INACTIVE_ADMIN)
                .build();

        adminRepository.save(newAdmin);
    }

    public AdminResponseDto login(String email, String rawPassword) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (admin.getRoleType() == RoleTypeAdmin.INACTIVE_ADMIN) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        return new AdminResponseDto(admin.getName(), admin.getEmail(), admin.getRoleType());
    }

    @Transactional
    public void approveAdmin(String targetAdminEmail, String superAdminEmail) {
        Admin superAdmin = getByEmail(superAdminEmail);
        if (superAdmin.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        if (superAdmin.getEmail().equals(targetAdminEmail)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Admin targetAdmin = getByEmail(targetAdminEmail);
        if (targetAdmin.getRoleType() != RoleTypeAdmin.INACTIVE_ADMIN) {
            throw new GeneralException(ErrorStatus.ALREADY_REGISTER_ADMIN);
        }

        targetAdmin.changeRole(RoleTypeAdmin.ADMIN);
    }

    @Transactional
    public void rejectAdmin(String targetAdminEmail, String superAdminEmail) {
        Admin superAdmin = getByEmail(superAdminEmail);
        if (superAdmin.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        if (superAdmin.getEmail().equals(targetAdminEmail)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Admin targetAdmin = getByEmail(targetAdminEmail);
        targetAdmin.changeRole(RoleTypeAdmin.INACTIVE_ADMIN);
    }

    public List<AdminResponseDto> getAllAdmins(String requesterEmail) {
        Admin requester = getByEmail(requesterEmail);
        if (requester.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        return adminRepository.findAll().stream()
                .map(admin -> new AdminResponseDto(admin.getName(), admin.getEmail(), admin.getRoleType()))
                .collect(Collectors.toList());
    }
}
