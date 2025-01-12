package com.keodam.keodam_backend.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.keodam.keodam_backend.app.domain.SocialType;
import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.oauth.domain.CustomIdTokenUser;
import com.keodam.keodam_backend.oauth.domain.IdTokenAttributes;
import com.keodam.keodam_backend.oauth.domain.userinfo.KakaoUserInfo;
import com.keodam.keodam_backend.oauth.domain.userinfo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
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

    public CustomIdTokenUser loadUserByAccessToken(String accessToken){

        DecodedJWT decodedJWT = JWT.decode(accessToken);
        SocialType socialType = checkIssuer(decodedJWT.getIssuer());

        Map<String, Object> attributes = tokenToattributes(accessToken, socialType);
        IdTokenAttributes idTokenAttributes = new IdTokenAttributes(attributes, socialType);

        User findUser = checkUser(idTokenAttributes);

        return new CustomIdTokenUser(
                Collections.singleton(new SimpleGrantedAuthority(findUser.getRoleType().toString())),
                findUser.getOAuthId(),
                findUser.getPassword(),
                findUser.getEmail(),
                findUser.getRoleType()
        );
    }

    private SocialType checkIssuer(String issuer){
        if(issuer.equals("https://kauth.kakao.com")) return SocialType.KAKAO;
        else if(issuer.equals("https://accounts.google.com")) return SocialType.GOOGLE;
        return SocialType.APPLE;
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
        if(socialType == SocialType.KAKAO) return kakaoJwtDecoder.decode(idToken).getClaims();
        if(socialType == SocialType.APPLE) return appleJwtDecoder.decode(idToken).getClaims();
        return null;
    }
}
