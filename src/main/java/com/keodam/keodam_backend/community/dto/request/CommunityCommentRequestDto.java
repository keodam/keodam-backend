package com.keodam.keodam_backend.community.dto.request;

import lombok.Getter;

@Getter
public class CommunityCommentRequestDto {
    private String content;
    private Long parentId;
}
