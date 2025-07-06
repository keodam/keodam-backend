package com.keodam.keodam_backend.community.dto.response;

import com.keodam.keodam_backend.community.domain.Community;
import com.keodam.keodam_backend.community.domain.CommunityImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommunityResponseDto {

    private Long id;
    private String content;
    private String userNickname;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
    private List<CommunityHashTagDto> hashtags;

    public static CommunityResponseDto from(Community community, List<CommunityImage> imageList) {
        return CommunityResponseDto.builder()
                .id(community.getId())
                .content(community.getContent())
                .userNickname(community.getUser().getNickname())
                .photoUrls(imageList.stream().map(CommunityImage::getImgUrl).collect(Collectors.toList()))
                .createdAt(community.getCreationDate())
                .hashtags(community.getCommunityHashTags().stream().map(CommunityHashTagDto::from).collect(Collectors.toList()))
                .build();
    }
}
