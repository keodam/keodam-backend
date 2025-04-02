package com.keodam.keodam_backend.mypage.service;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.domain.DocumentType;
import com.keodam.keodam_backend.mypage.domain.EmailTitle;
import com.keodam.keodam_backend.mypage.domain.UserVerification;
import com.keodam.keodam_backend.mypage.dto.request.EmailRequestDto;
import com.keodam.keodam_backend.mypage.dto.response.EmailResponseDto;
import com.keodam.keodam_backend.mypage.repository.UserVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MailSendService {

    private static final int AUTH_CODE_LENGTH = 6;
    private static final Random random = new Random();
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private final JavaMailSender mailSender;
    private final UserVerificationRepository userVerificationRepository;
    private final UserRepository userRepository;

    private int authNumber;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public EmailResponseDto checkEmail(String userEmail, EmailRequestDto.EmailSenderDto emailRequestDto) {

        validateEmailFormat(emailRequestDto);

        generateAuthCode();
        String setFrom = mailUsername;
        String toMail = emailRequestDto.getEmail();
        String content =
                "<br><br>" +
                        "인증 번호는 <strong>" + authNumber + "</strong>입니다." +
                        "<br>" +
                        "인증번호를 정확히 입력해주세요." +
                        "<br>" +
                        "이 인증번호는 3분 이내에 입력하셔야 유효합니다.";
        mailSend(setFrom, toMail, EmailTitle.EMAIL_VERIFICATION.getFullTitle(), content);
        String code = Integer.toString(authNumber);

        saveVerificationInfo(userEmail, emailRequestDto, code);

        return EmailResponseDto.builder()
                .code(code)
                .build();
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCode(String email, String code) {

        UserVerification userVerification = userVerificationRepository.findByUser_Email(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!userVerification.getVerificationCode().equals(code)) {
            throw new GeneralException(ErrorStatus.INVALID_VERIFICATION_CODE);
        }

        if (userVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            userVerificationRepository.delete(userVerification);
            throw new GeneralException(ErrorStatus.EXPIRED_CODE);
        }
        return true;
    }

    private void saveVerificationInfo(String idTokenUserEmail, EmailRequestDto.EmailSenderDto emailRequestDto, String code) {
        User user = userRepository.findByEmail(idTokenUserEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        DocumentType documentType = DocumentType.valueOf(emailRequestDto.getDocumentType().toUpperCase());


        userVerificationRepository.save(UserVerification.builder()
                .user(user)
                .documentType(documentType)
                .verificationEmail(emailRequestDto.getEmail())
                .verificationCode(code)
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .build()); //처음엔 보류상태로
    }

    private void generateAuthCode() {
        String randomNumber = "";
        for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
            randomNumber += Integer.toString(random.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }

    private void validateEmailFormat(EmailRequestDto.EmailSenderDto emailRequestDto) {
        if (!Pattern.matches(EMAIL_REGEX, emailRequestDto.getEmail())) {
            throw new GeneralException(ErrorStatus.INVALID_EMAIL_FORMAT);
        }
    }
}
