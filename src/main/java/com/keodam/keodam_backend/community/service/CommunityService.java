package com.keodam.keodam_backend.community.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public CommunityResponseDto createCommunityPost(String email, CommunityRequestDto communityRequestDto, List<MultipartFile> imgs) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Community community = communityRepository.save(
                Community.builder()
                        .content(communityRequestDto.getContent())
                        .user(user)
                        .build());

        saveTagsToCommunity(community, communityRequestDto.getTags());

        List<CommunityImage> images = uploadImagesToCommunity(community, imgs);

        return CommunityResponseDto.from(community, images);
    }

    @Transactional(readOnly = true)
    public CommunityResponseDto getCommunityPost(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_NOT_FOUND));

        List<CommunityImage> images = communityImageRepository.findByCommunityId(communityId);
        return CommunityResponseDto.from(community, images);
    }

    @Transactional
    public CommunityResponseDto updateCommunityPost(String email, Long communityId, CommunityRequestDto communityRequestDto, List<MultipartFile> imgs) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_NOT_FOUND));

        if (!community.getUser().getEmail().equals(email)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_COMMUNITY_MODIFICATION);
        }

        community.updateContent(communityRequestDto.getContent());

        community.getCommunityHashTags().clear();
        saveTagsToCommunity(community, communityRequestDto.getTags());

        List<CommunityImage> existingPhotos = communityImageRepository.findByCommunityId(communityId);

        for (CommunityImage image : existingPhotos) {
            awsS3Service.deleteFile(image.getImgUrl());
            communityImageRepository.delete(image);
        }

        List<CommunityImage> communityImages = uploadImagesToCommunity(community, imgs);

        return CommunityResponseDto.from(community, communityImages);
    }

    @Transactional
    public void deleteCommunityPost(String email, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMUNITY_NOT_FOUND));

        if (!community.getUser().getEmail().equals(email)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_COMMUNITY_MODIFICATION);
        }
        communityRepository.delete(community);
    }

    private void saveTagsToCommunity(Community community, List<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        String mainTag = tags.get(0);

        for (String tag : tags) {
            HashTag hashTag = hashTagRepository.findByName(tag)
                    .orElseGet(() -> hashTagRepository.save(new HashTag(tag)));

            CommunityHashTag cht = CommunityHashTag.builder()
                    .community(community)
                    .hashtag(hashTag)
                    .isMain(tag.equals(mainTag))
                    .build();

            community.addCommunityHashTag(cht);
        }
    }

    private List<CommunityImage> uploadImagesToCommunity(Community community, List<MultipartFile> imgs) {
        List<CommunityImage> communityImages = new ArrayList<>();
        if (imgs == null) return communityImages;

        for (MultipartFile img : imgs) {
            String imgUrl = awsS3Service.uploadFile(img);

            CommunityImage image = CommunityImage.builder()
                    .community(community)
                    .imgUrl(imgUrl)
                    .build();

            communityImageRepository.save(image);
            communityImages.add(image);
        }
        return communityImages;
    }
}
