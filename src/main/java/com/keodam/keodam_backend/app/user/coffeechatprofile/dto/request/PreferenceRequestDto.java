package com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PreferenceRequestDto {

    @NotNull(message = "사용자 타입은 필수입니다.")
    private String userType; // MENTOR, MENTEE

    @NotEmpty(message = "선호 요일은 최소 하나 이상 선택해야 합니다.")
    private List<String> preferredDays; // 월요일, 화요일

    @NotNull(message = "선호 장소는 필수입니다.")
    @Size(min = 1, max = 2, message = "선호 장소는 최소 1개, 최대 2개까지 선택 가능합니다.")
    private List<String> preferredLocations; // 정확한 주소 입력
}
