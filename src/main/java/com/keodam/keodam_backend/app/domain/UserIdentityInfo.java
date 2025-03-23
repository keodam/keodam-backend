package com.keodam.keodam_backend.app.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_identity_info")
@Getter
@NoArgsConstructor
public class UserIdentityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber; // 휴대폰 인증에서 저장한 휴대폰 번호
    @Column(name = "user_sex", nullable = false)
    private Boolean userSex;  // false(0) = 여성 | true(1) = 남성
    @Column(name = "user_real_name", nullable = false)
    private String userRealName; // 휴대폰 인증에서 저장한 실명
    @Column(name = "user_birth", nullable = false)
    private String userBirth; // 휴대폰 인증에서 저장한 생년월일 6자리
    @Column(name = "user_name")
    private String userName;  // 소셜에서 가져온 이름

    @Builder
    public UserIdentityInfo(String phoneNumber, Boolean userSex, String userRealName, String userBirth, String userName) {
        this.phoneNumber = phoneNumber;
        this.userSex = userSex;
        this.userRealName = userRealName;
        this.userBirth = userBirth;
        this.userName = userName;
    }
}
