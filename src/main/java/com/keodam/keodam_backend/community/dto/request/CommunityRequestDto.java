package com.keodam.keodam_backend.community.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class CommunityRequestDto {
    private String content;
    private List<String> tags;
}
