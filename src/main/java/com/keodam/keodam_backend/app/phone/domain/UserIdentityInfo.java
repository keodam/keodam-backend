package com.keodam.keodam_backend.app.phone.domain;


import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_identity_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIdentityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber; // 휴대폰 번호 숫자 : 휴대폰 인증에서 저장한
    @Column(name = "user_birth", nullable = false)
    private String userBirth; // 생년월일 6자리 숫자 : 휴대폰 인증에서 저장한
    @Column(name = "user_real_name", nullable = false)
    private String userRealName; // 실명 : 휴대폰 인증에서 저장한
    @Column(name = "user_gender", nullable = false)
    private Boolean userGender;  // 성별 : false(0) = 여성 | true(1) = 남성
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt; // 휴대폰 본인인증 완료 시간
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 회원탈퇴 일자 기록
    @Column(name = "deleted_reason")
    private String deletedReason; // 탈퇴 사유 기록
    @Column(name = "is_active", nullable = false)
    private Boolean isActive; // 탈퇴/비활성 상태 여부 (true = 활성, false = 탈퇴)
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Builder
    public UserIdentityInfo(String phoneNumber, String userBirth, String userRealName, Boolean userGender, LocalDateTime verifiedAt, Boolean isActive) {
        this.phoneNumber = phoneNumber;
        this.userBirth = userBirth;
        this.userRealName = userRealName;
        this.userGender = userGender;
        this.verifiedAt = verifiedAt;
        this.isActive = isActive;
    }

    public void updateInfo(String birth, String realName, Boolean gender) {
        this.userBirth = birth;
        this.userRealName = realName;
        this.userGender = gender;
        this.verifiedAt = LocalDateTime.now();
    }

    public void markVerifiedNow() {
        this.verifiedAt = LocalDateTime.now();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void deactivate(String reason) {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedReason = reason;
    }

    public void linkUser(User user) {
        this.user = user;
    }

    public void unlinkUser() {
        this.user = null;
    }
}
