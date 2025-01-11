package com.keodam.keodam_backend.oauth.service;

import com.keodam.keodam_backend.app.domain.SocialType;
import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.oauth.domain.CustomOAuth2User;
import com.keodam.keodam_backend.oauth.domain.OAuthAttributes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    final private UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2User_ = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2User_.loadUser(userRequest);
        String test = userRequest.getAccessToken().getTokenType().getValue();
        log.info(test);
        String socialType_ = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = SocialType.valueOf(socialType_.toUpperCase());

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes oAuthAttributes = new OAuthAttributes(socialType, userNameAttributeName, attributes);

        User findUser = checkUser(oAuthAttributes);
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(findUser.getRoleType().toString())),
                attributes,
                oAuthAttributes.getNameAttributeKey(),
                findUser.getEmail(),
                findUser.getRoleType()
        );
    }

    private User checkUser(OAuthAttributes oAuthAttributes){
        User findUser = userRepository.findByEmail(oAuthAttributes.getUserInfo().getEmail()).orElse(null);
        if (findUser == null) return createUser(oAuthAttributes);
        return findUser;
    }

    private User createUser(OAuthAttributes oAuthAttributes) {
        User createdUser = oAuthAttributes.toEntity();
        return userRepository.save(createdUser);
    }
}
