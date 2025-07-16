package com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HashtagInfo {
    private String name;
    private boolean isMain;
}
