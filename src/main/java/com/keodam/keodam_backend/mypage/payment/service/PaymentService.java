package com.keodam.keodam_backend.mypage.payment.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.bootpay.service.BootpayService;
import com.keodam.keodam_backend.mypage.payment.domain.BeanTransaction;
import com.keodam.keodam_backend.mypage.payment.domain.BeanTransactionType;
import com.keodam.keodam_backend.mypage.payment.domain.BeanWallet;
import com.keodam.keodam_backend.mypage.payment.domain.Payment;
import com.keodam.keodam_backend.mypage.payment.dto.request.PaymentRequestDto;
import com.keodam.keodam_backend.mypage.payment.dto.response.BootpayConfirmResponse;
import com.keodam.keodam_backend.mypage.payment.dto.response.PaymentResponseDto;
import com.keodam.keodam_backend.mypage.payment.repository.PaymentRepository;
import com.keodam.keodam_backend.mypage.payment.repository.beanTransactionRepository;
import com.keodam.keodam_backend.mypage.payment.repository.beanWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BootpayService bootpayService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final beanWalletRepository beanWalletRepository;
    private final beanTransactionRepository beanTransactionRepository;

    public PaymentResponseDto verifyAndSavePayment(PaymentRequestDto paymentRequestDto) {

        BootpayConfirmResponse confirm = bootpayService.confirm(paymentRequestDto.getReceiptId());

        if (paymentRepository.existsByReceiptId(paymentRequestDto.getReceiptId())) {
            throw new GeneralException(ErrorStatus.ALREADY_PROCESSED_PAYMENT);
        }

        User user = userRepository.findById(paymentRequestDto.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Payment payment = savePayment(paymentRequestDto, confirm, user);

        paymentRepository.save(payment);

        BeanWallet beanWallet = getBeanWallet(paymentRequestDto, user);

        saveBeanTransaction(paymentRequestDto, user, payment);

        return createPaymentResponseDto(paymentRequestDto, confirm, payment, beanWallet);
    }

    private PaymentResponseDto createPaymentResponseDto(PaymentRequestDto paymentRequestDto, BootpayConfirmResponse confirm, Payment payment, BeanWallet beanWallet) {
        return PaymentResponseDto.builder()
                .receiptId(paymentRequestDto.getReceiptId())
                .price(paymentRequestDto.getPrice())
                .method(confirm.getMethod())
                .paidAt(payment.getRequestedAt())
                .beanAmount(paymentRequestDto.getBeanAmount())
                .totalBeans(beanWallet.getTotalBeans())
                .build();
    }

    private void saveBeanTransaction(PaymentRequestDto paymentRequestDto, User user, Payment payment) {
        beanTransactionRepository.save(
                BeanTransaction.builder()
                        .user(user)
                        .type(BeanTransactionType.CHARGE)
                        .payment(payment)
                        .beanAmount(paymentRequestDto.getBeanAmount())
                        .build()
        );
    }

    private BeanWallet getBeanWallet(PaymentRequestDto paymentRequestDto, User user) {
        BeanWallet beanWallet = beanWalletRepository.findByUser(user)
                .orElseGet(() -> createWalletForUser(user));

        beanWallet.increase(paymentRequestDto.getBeanAmount());
        beanWalletRepository.save(beanWallet);
        return beanWallet;
    }

    private Payment savePayment(PaymentRequestDto paymentRequestDto, BootpayConfirmResponse confirm, User user) {
        return Payment.builder()
                .receiptId(paymentRequestDto.getReceiptId())
                .beanAmount(paymentRequestDto.getBeanAmount())
                .price(paymentRequestDto.getPrice())
                .method(confirm.getMethod())
                .user(user)
                .build();
    }

    private BeanWallet createWalletForUser(User user) {
        return BeanWallet.builder()
                .user(user)
                .totalBeans(0)
                .build();
    }
}
