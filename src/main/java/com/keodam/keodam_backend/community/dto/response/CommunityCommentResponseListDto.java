package com.keodam.keodam_backend.community.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CommunityCommentResponseListDto {
    private List<CommunityCommentResponseDto> comments;
}
