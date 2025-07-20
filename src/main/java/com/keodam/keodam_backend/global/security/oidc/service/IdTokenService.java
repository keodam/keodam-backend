package com.keodam.keodam_backend.global.security.oidc.service;

import com.keodam.keodam_backend.app.user.domain.SocialType;
import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.global.security.oidc.domain.CustomUserDetails;
import com.keodam.keodam_backend.global.security.oidc.domain.IdTokenAttributes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class IdTokenService {

    private final JwtDecoder kakaoJwtDecoder;
    private final JwtDecoder googleJwtDecoder;
    private final JwtDecoder appleJwtDecoder;
    private final UserRepository userRepository;

    public CustomUserDetails loadUserByOidcIdToken(String provider, String accessToken){

        SocialType socialType;
        User findUser;
        IdTokenAttributes idTokenAttributes;
        Map<String, Object> attributes;

        socialType = checkIssuer(provider);
        attributes = tokenToattributes(accessToken, socialType);
        idTokenAttributes = new IdTokenAttributes(attributes, socialType);

        findUser = checkUser(idTokenAttributes);

        return new CustomUserDetails(
                findUser.getEmail(),
                findUser.getRoleType(),
                Collections.singleton(new SimpleGrantedAuthority(findUser.getRoleType().toString())),
                attributes
        );
    }

    private SocialType checkIssuer(String provider){

        return switch (provider) {
            case "kakao" -> SocialType.KAKAO;
            case "google" -> SocialType.GOOGLE;
            case "apple" -> SocialType.APPLE;
            default -> throw new ProviderNotFoundException("잘못된 제공 업체입니다. :" + provider);
        };
    }

    private User checkUser(IdTokenAttributes idTokenAttributes){

        User findUser = userRepository.findByEmail(idTokenAttributes.getUserInfo().getEmail()).orElse(null);
        if (findUser == null) return createUser(idTokenAttributes);
        return findUser;
    }

    private User createUser(IdTokenAttributes idTokenAttributes) {

        User createdUser = idTokenAttributes.toUser();
        return userRepository.save(createdUser);
    }

    private Map<String, Object> tokenToattributes(String idToken, SocialType socialType){

        if(socialType == SocialType.GOOGLE) return googleJwtDecoder.decode(idToken).getClaims();
        if(socialType == SocialType.KAKAO)  return kakaoJwtDecoder.decode(idToken).getClaims();
        if(socialType == SocialType.APPLE)  return appleJwtDecoder.decode(idToken).getClaims();
        return null;
    }
}
