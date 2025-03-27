package com.keodam.keodam_backend.app.phone.repository;

import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserIdentityInfoRepository extends JpaRepository<UserIdentityInfo, Long> {
    // 휴대폰 번호로 사용자 정보 조회
    Optional<UserIdentityInfo> findByPhoneNumber(String phoneNumber);

    // 사용자 이름(실명)으로 사용자 정보 조회
    Optional<UserIdentityInfo> findByUserRealName(String userRealName);
}
