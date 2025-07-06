package com.keodam.keodam_backend.community.dto.response;

import com.keodam.keodam_backend.community.domain.CommunityHashTag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityHashTagDto {
    private String hashTagName;
    private boolean isMain;

    public static CommunityHashTagDto from(CommunityHashTag communityHashTag) {
        return CommunityHashTagDto.builder()
                .hashTagName(communityHashTag.getHashtag().getName())
                .isMain(communityHashTag.isMain())
                .build();
    }
}
