package com.keodam.keodam_backend.mypage.roulette.service;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.roulette.domain.*;
import com.keodam.keodam_backend.mypage.roulette.dto.CoffeeExchangeRequest;
import com.keodam.keodam_backend.mypage.roulette.dto.RouletteStatusResponse;
import com.keodam.keodam_backend.mypage.roulette.dto.SpinResultResponse;
import com.keodam.keodam_backend.mypage.roulette.repository.ExchangeRequestRepository;
import com.keodam.keodam_backend.mypage.roulette.repository.RouletteSpinLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RouletteService {

    private final UserRepository userRepository;
    private final RouletteSpinLogRepository rouletteSpinLogRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    @Transactional(readOnly = true)
    public RouletteStatusResponse getRouletteStatus(Long userId) {
        User user = findUserById(userId);
        return new RouletteStatusResponse(user.getRouletteCoupon(), user.getCoffeeCoupon());
    }

    @Transactional
    public SpinResultResponse spinRoulette(Long userId) {
        User user = findUserById(userId);

        if (user.getRouletteCoupon() <= 0) {
            throw new GeneralException(ErrorStatus.NO_ROULETTE_COUPONS);
        }
        user.decreaseRouletteCoupon(1);

        SpinResultType result = determineSpinResult();

        if (result == SpinResultType.COUPON) {
            user.increaseCoffeeCoupon(1);
        }
        // TODO: 경험치 EXP, 꽝 당첨 시 로직 추가

        RouletteSpinLog log = new RouletteSpinLog();
        log.setUser(user);
        log.setResultType(result);
        rouletteSpinLogRepository.save(log);

        return new SpinResultResponse(result.name(), user.getRouletteCoupon());
    }

    @Transactional
    public void requestCoffeeExchange(Long userId, CoffeeExchangeRequest request) {
        User user = findUserById(userId);

        if (user.getCoffeeCoupon() <= 0) {
            throw new GeneralException(ErrorStatus.NO_COFFEE_COUPONS);
        }
        user.decreaseCoffeeCoupon(1);

        // 교환 신청 내역 저장
        ExchangeRequest exchangeRequest = ExchangeRequest.builder()
                .user(user)
                .phoneNumber(request.getPhoneNumber())
                .status(ExchangeRequestStatus.PENDING)
                .build();
        exchangeRequestRepository.save(exchangeRequest);
    }

    // 공통 사용자 조회 메소드
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private SpinResultType determineSpinResult() {
        // TODO: 실제 확률 정책에 맞게 구현
        double random = Math.random();
        if (random < 0.1) {
            return SpinResultType.COUPON;
        } else if (random < 0.4) {
            return SpinResultType.EXP300;
        } else if (random < 0.7) {
            return SpinResultType.EXP150;
        } else {
            return SpinResultType.NONE;
        }
    }
}