package com.keodam.keodam_backend.community.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.community.domain.Community;
import com.keodam.keodam_backend.community.domain.CommunityHashTag;
import com.keodam.keodam_backend.community.domain.CommunityImage;
import com.keodam.keodam_backend.community.domain.HashTag;
import com.keodam.keodam_backend.community.dto.request.CommunityRequestDto;
import com.keodam.keodam_backend.community.dto.response.CommunityResponseDto;
import com.keodam.keodam_backend.community.repository.CommunityImageRepository;
import com.keodam.keodam_backend.community.repository.CommunityRepository;
import com.keodam.keodam_backend.community.repository.HashTagRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.aws.AwsS3Service;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final CommunityImageRepository communityImageRepository;
    private final HashTagRepository hashTagRepository;

    public CommunityResponseDto createCommunityPost(String email, CommunityRequestDto communityRequestDto, List<MultipartFile> imgs) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));


        Community community = communityRepository.save(
                Community.builder()
                        .content(communityRequestDto.getContent())
                        .user(user)
                        .build());

        List<String> tags = communityRequestDto.getTags();

        String mainTag = communityRequestDto.getTags().get(0);

        for (String tag : tags) {
            HashTag hashTag = hashTagRepository.findByName(tag)
                    .orElseGet(() -> hashTagRepository.save(HashTag.builder()
                            .name(tag)
                            .build()));

            CommunityHashTag communityHashTag = CommunityHashTag.builder()
                    .community(community)
                    .hashtag(hashTag)
                    .isMain(tag.equals(mainTag))
                    .build();

            community.addCommunityHashTag(communityHashTag);
        }

        List<CommunityImage> communityImages = new ArrayList<>();
        if (imgs == null) {
            imgs = new ArrayList<>();
        }

        for (MultipartFile file : imgs) {
            String imgUrl = awsS3Service.uploadFile(file);

            CommunityImage communityImage = CommunityImage.builder()
                    .imgUrl(imgUrl)
                    .community(community)
                    .build();

            communityImageRepository.save(communityImage);

            communityImages.add(communityImage);
        }

        return CommunityResponseDto.from(community, communityImages);
    }
}
