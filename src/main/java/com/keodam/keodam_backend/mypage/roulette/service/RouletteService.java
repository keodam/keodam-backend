package com.keodam.keodam_backend.mypage.roulette.service;

import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.MypageStats;
import com.keodam.keodam_backend.app.user.coffeechatprofile.repository.MypageStatsRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouletteService {

    private final UserRepository userRepository;
    private final RouletteSpinLogRepository rouletteSpinLogRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final MypageStatsRepository mypageStatsRepository;

    @Transactional(readOnly = true)
    public RouletteStatusResponse getRouletteStatus(String email) {
        User user = findUserByEmail(email);
        return new RouletteStatusResponse(user.getRouletteCoupon(), user.getCoffeeCoupon());
    }

    @Transactional
    public SpinResultResponse spinRoulette(String email) {
        User user = findUserByEmail(email);

        user.decreaseRouletteCoupon(1);

        SpinResultType result = determineSpinResult();
        String itemIndex;

        if (result == SpinResultType.COUPON) {
            user.increaseCoffeeCoupon(1);
            itemIndex = "item2";
        } else if (result == SpinResultType.EXP150) {
            MypageStats stats = mypageStatsRepository.findByUser(user)
                    .orElseGet(() -> mypageStatsRepository.save(MypageStats.builder().user(user).build()));
            stats.addExpPoint(150);
            itemIndex = "item1";
        } else if (result == SpinResultType.EXP300) {
            MypageStats stats = mypageStatsRepository.findByUser(user)
                    .orElseGet(() -> mypageStatsRepository.save(MypageStats.builder().user(user).build()));
            stats.addExpPoint(300);
            itemIndex = "item4";
        } else {
            itemIndex = "item3";
        }

        RouletteSpinLog log = new RouletteSpinLog();
        log.setUser(user);
        log.setResultType(result);
        rouletteSpinLogRepository.save(log);

        return new SpinResultResponse(result.name(), user.getRouletteCoupon(), itemIndex);
    }

    @Transactional
    public void requestCoffeeExchange(String email, CoffeeExchangeRequest request) {
        String formattedPhoneNumber = normalizeAndValidatePhoneNumber(request.getPhoneNumber());
        User user = findUserByEmail(email);

        if (user.getCoffeeCoupon() < request.getQuantity()) {
            throw new GeneralException(ErrorStatus.NOT_ENOUGH_COFFEE_COUPONS);
        }

        user.decreaseCoffeeCoupon(request.getQuantity());

        try {
            for (int i = 0; i < request.getQuantity(); i++) {
                ExchangeRequest exchangeRequest = ExchangeRequest.builder()
                        .user(user)
                        .phoneNumber(formattedPhoneNumber)
                        .status(ExchangeRequestStatus.PENDING)
                        .build();
                exchangeRequestRepository.save(exchangeRequest);
            }
        } catch (Exception e) {
            log.error("커피 교환 신청 처리 중 에러 발생: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.EXCHANGE_REQUEST_FAILED);
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private SpinResultType determineSpinResult() {
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

    private String normalizeAndValidatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new GeneralException(ErrorStatus.INVALID_PHONE_NUMBER_FORMAT);
        }

        String digits = phoneNumber.replaceAll("[^0-9]", "");

        if (!digits.startsWith("010") || digits.length() != 11) {
            throw new GeneralException(ErrorStatus.INVALID_PHONE_NUMBER_FORMAT);
        }

        return digits.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }
}
