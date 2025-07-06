package com.keodam.keodam_backend.community.dto.response;

import com.keodam.keodam_backend.community.domain.CommunityComment;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommunityCommentResponseDto {
    private Long id;
    private String content;
    private String writer;
    private List<CommunityCommentResponseDto> children;

    public static CommunityCommentResponseDto from(CommunityComment comment) {
        return CommunityCommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(comment.getUser().getNickname())
                .children(comment.getChildren().stream()
                        .map(CommunityCommentResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<CommunityCommentResponseDto> toResponseList(List<CommunityComment> comments) {
        return comments.stream()
                .map(CommunityCommentResponseDto::from)
                .collect(Collectors.toList());
    }
}
