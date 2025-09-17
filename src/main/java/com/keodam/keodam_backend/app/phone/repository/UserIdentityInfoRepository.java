package com.keodam.keodam_backend.app.phone.repository;

import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserIdentityInfoRepository extends JpaRepository<UserIdentityInfo, Long> {
    // 휴대폰 번호로 사용자 정보 조회
    Optional<UserIdentityInfo> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);

    // User 객체로 휴대폰 인증 정보 조회 (추가된 메소드)
    Optional<UserIdentityInfo> findByUser(User user);

    @Query("SELECT u.phoneNumber FROM UserIdentityInfo u WHERE u.phoneNumber LIKE CONCAT(:basePhone, '%')")
    List<String> findAllArchivedPhones(@Param("basePhone") String basePhone);

    // 사용자 이름(실명)으로 사용자 정보 조회
    Optional<UserIdentityInfo> findByUserRealName(String userRealName);
    void deleteByUser(User user);
}
