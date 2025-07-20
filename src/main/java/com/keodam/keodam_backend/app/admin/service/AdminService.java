package com.keodam.keodam_backend.app.admin.service;

import com.keodam.keodam_backend.app.admin.domain.Admin;
import com.keodam.keodam_backend.app.admin.domain.RoleTypeAdmin;
import com.keodam.keodam_backend.app.admin.dto.*;
import com.keodam.keodam_backend.app.admin.repository.AdminRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Admin getByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Transactional
    public void register(AdminRegisterDto dto) {
        if (adminRepository.existsByEmail(dto.email())) {
            throw new GeneralException(ErrorStatus.ALREADY_REGISTER_ADMIN);
        }

        Admin newAdmin = Admin.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roleType(RoleTypeAdmin.INACTIVE_ADMIN)
                .build();

        adminRepository.save(newAdmin);
    }

    @Transactional
    public AdminLoginResponseDto login(AdminLoginDto dto) {
        Admin admin = getByEmail(dto.email());

        if (admin.getRoleType() == RoleTypeAdmin.INACTIVE_ADMIN) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(dto.password(), admin.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        String role = "ROLE_" + admin.getRoleType().name();
        String accessToken = jwtService.createAdminAccessToken(admin.getEmail(), admin.getId(), role);
        String refreshToken = jwtService.createRefreshToken();
        admin.updateRefreshToken(refreshToken);
        adminRepository.save(admin);

        return new AdminLoginResponseDto(
                admin.getName(),
                admin.getEmail(),
                admin.getRoleType(),
                accessToken,
                refreshToken
        );
    }

    @Transactional
    public void approveAdmin(SuperAdminRequestDto dto, Admin superAdmin) {
        if (superAdmin.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Admin targetAdmin = getByEmail(dto.targetAdminEmail());
        if (superAdmin.getEmail().equals(targetAdmin.getEmail())) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        if (targetAdmin.getRoleType() != RoleTypeAdmin.INACTIVE_ADMIN) {
            throw new GeneralException(ErrorStatus.ALREADY_REGISTER_ADMIN);
        }

        targetAdmin.changeRole(RoleTypeAdmin.ADMIN);
    }

    @Transactional
    public void rejectAdmin(SuperAdminRequestDto dto, Admin superAdmin) {
        if (superAdmin.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Admin targetAdmin = getByEmail(dto.targetAdminEmail());
        if (superAdmin.getEmail().equals(targetAdmin.getEmail())) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        targetAdmin.changeRole(RoleTypeAdmin.INACTIVE_ADMIN);
    }

    public List<AdminResponseDto> getAllAdmins(Admin superAdmin) {
        if (superAdmin.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        return adminRepository.findAll().stream()
                .map(admin -> new AdminResponseDto(
                        admin.getName(),
                        admin.getEmail(),
                        admin.getRoleType()
                )).collect(Collectors.toList());
    }

    @Transactional
    public void updatePassword(Admin admin, String currentPw, String newPw) {
        if (!passwordEncoder.matches(currentPw, admin.getPassword())) {
            throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
        }

        admin.updatePassword(passwordEncoder.encode(newPw));
    }

    @Transactional
    public void resetPasswordBySuperAdmin(SuperAdminPasswordResetDto dto, Admin superAdmin) {
        if (superAdmin.getRoleType() != RoleTypeAdmin.SUPER_ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Admin target = getByEmail(dto.targetAdminEmail());
        target.updatePassword(passwordEncoder.encode(dto.temporaryPassword()));
    }
}
