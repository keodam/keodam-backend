package com.keodam.keodam_backend.oidc.service;

import com.keodam.keodam_backend.app.user.domain.SocialType;
import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.security.JwtService;
import com.keodam.keodam_backend.oidc.dto.IdTokenResponse;
import com.keodam.keodam_backend.oidc.domain.IdTokenAttributes;
import com.keodam.keodam_backend.oidc.dto.RefreshReqRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final JwtDecoder kakaoJwtDecoder;
    private final JwtDecoder googleJwtDecoder;
    private final JwtDecoder appleJwtDecoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public Pair<IdTokenResponse, String> loadUserByOidcIdToken(String provider, String accessToken){

        SocialType socialType;
        User findUser;
        IdTokenAttributes idTokenAttributes;
        Map<String, Object> attributes;

        socialType = checkIssuer(provider);

        try{
            attributes = tokenToAttributes(accessToken, socialType);
        } catch (JwtValidationException e){
            throw new GeneralException(ErrorStatus.JWT_EXPIRED);
        } catch (BadJwtException e) {
            throw new GeneralException(ErrorStatus.JWT_MALFORMED);
        } catch (JwtException e) {
            throw new GeneralException(ErrorStatus.INVALID_JWT);
        }

        idTokenAttributes = new IdTokenAttributes(attributes, socialType);
        findUser = checkUser(idTokenAttributes);

        return Pair.of(new IdTokenResponse(findUser.getRoleType(), findUser.getRefreshToken()), jwtService.createAccessToken(findUser.getEmail()));
    }

    @Transactional
    public Pair<RefreshReqRes, String> reIssueRefreshTokenAndAccessToken(String refreshToken){

        if(refreshToken.startsWith("BEARER"))
            refreshToken = refreshToken.substring(7);

        if(!jwtService.isTokenValid(refreshToken))
            throw new GeneralException(ErrorStatus.RELOGIN_REQUIRED);

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return Pair.of(new RefreshReqRes(reIssueRefreshToken(user)), jwtService.createAccessToken(user.getEmail()));
    }

    private String reIssueRefreshToken(User user) {

        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    private SocialType checkIssuer(String provider){

        return switch (provider) {
            case "kakao" -> SocialType.KAKAO;
            case "google" -> SocialType.GOOGLE;
            case "apple" -> SocialType.APPLE;
            default -> throw new GeneralException(ErrorStatus.INVALID_PROVIDER);
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

    private Map<String, Object> tokenToAttributes(String idToken, SocialType socialType){

        return switch (socialType) {
            case GOOGLE -> googleJwtDecoder.decode(idToken).getClaims();
            case KAKAO  -> kakaoJwtDecoder.decode(idToken).getClaims();
            case APPLE  -> appleJwtDecoder.decode(idToken).getClaims();
        };
    }
}
