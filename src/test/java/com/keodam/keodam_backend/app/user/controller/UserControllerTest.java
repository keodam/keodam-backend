package com.keodam.keodam_backend.app.user.controller;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.dto.NicknameRequestDto;
import com.keodam.keodam_backend.app.user.dto.UserResponseDto;
import com.keodam.keodam_backend.app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.oauth2.core.user.OAuth2User;


import static org.junit.jupiter.api.Assertions.*;

import com.keodam.keodam_backend.app.domain.RoleType;

import com.keodam.keodam_backend.app.user.dto.RoleRequestDto;
import com.keodam.keodam_backend.app.user.dto.SignupStatusResponseDto;

import com.keodam.keodam_backend.global.ApiResponse;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private OAuth2User mockOAuth2User;

    private User user;

    @BeforeEach
    void setUp() {
        mockOAuth2User = mock(OAuth2User.class);
        lenient().when(mockOAuth2User.getName()).thenReturn("123456");

        user = User.builder()
                .oauthId("123456")
                .email("test@example.com")
                .build();
    }

    @Test
    void updateNickname_success() {
        // given
        NicknameRequestDto request = new NicknameRequestDto("haeun");

        UserResponseDto updatedDto = UserResponseDto.builder()
                .id(1L)
                .nickname("haeun")
                .email("test@example.com")
                .profileUrl(null)
                .roleType(null)
                .hasProfileImage(false)
                .hasRole(false)
                .build();

        when(userService.findByOAuthId("123456")).thenReturn(Optional.of(user));
        when(userService.updateNickname(eq(user), eq("haeun"))).thenReturn(updatedDto);

        // when
        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.updateNickname(request, mockOAuth2User);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("haeun", response.getBody().getResult().nickname());
    }


    @Test
    void selectRole_success() {
        // given
        RoleRequestDto request = new RoleRequestDto(RoleType.MENTOR);
        when(userService.findByOAuthId("123456")).thenReturn(Optional.of(user));

        UserResponseDto updatedDto = UserResponseDto.builder()
                .id(1L)
                .nickname("haeun")
                .email("test@example.com")
                .roleType(RoleType.MENTOR)
                .profileUrl(null)
                .hasProfileImage(false)
                .hasRole(true)
                .build();

        when(userService.updateRole(user, RoleType.MENTOR)).thenReturn(updatedDto);

        // when
        ResponseEntity<ApiResponse<UserResponseDto>> response = userController.selectRole(request, mockOAuth2User);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(RoleType.MENTOR, response.getBody().getResult().roleType());
    }

    @Test
    void checkSignupStatus_success() {
        // given
        when(userService.findByOAuthId("123456")).thenReturn(Optional.of(user));

        SignupStatusResponseDto status = new SignupStatusResponseDto(true, true,true);
        when(userService.checkSignupStatus(user)).thenReturn(status);

        // when
        ResponseEntity<ApiResponse<SignupStatusResponseDto>> response = userController.checkSignupStatus(mockOAuth2User);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getResult().hasNickname());
        assertTrue(response.getBody().getResult().hasRole());
    }
}
