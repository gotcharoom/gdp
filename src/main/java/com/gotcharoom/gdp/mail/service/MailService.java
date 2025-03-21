package com.gotcharoom.gdp.mail.service;

import com.gotcharoom.gdp.global.security.model.SocialType;
import com.gotcharoom.gdp.mail.model.FindIdRequest;
import com.gotcharoom.gdp.mail.model.FindPasswordRequest;
import com.gotcharoom.gdp.mail.model.FindResponse;
import com.gotcharoom.gdp.mail.model.MailRequest;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Slf4j
@Service
public class MailService {

    private static final String FIND_ID_MAIL_SUBJECT = "GDP ID 찾기 메일입니다";
    private static final String GENERATE_TEMP_PASSWORD_MAIL_SUBJECT = "GDP 임시 비밀번호 발급 메일입니다";

    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public MailService(
            UserRepository userRepository,
            TemplateEngine templateEngine,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public FindResponse sendFindIdEmail(FindIdRequest request) throws MessagingException {
        log.info("ID 찾기 이메일 발송 요청 수신 - email: {}", request.getEmail());

        boolean isFound = false;

        GdpUser gdpUser = userRepository.findBySocialTypeAndEmail(SocialType.GDP, request.getEmail())
                .orElse(null);

        if (gdpUser != null) {
            isFound = true;
            log.info("사용자 조회 성공 - ID: {}, 이름: {}", gdpUser.getId(), gdpUser.getName());

            String templated = applyTemplateToFindIdMail(gdpUser);
            log.info("ID 찾기 메일 템플릿 생성 완료");

            MailRequest mailRequest = MailRequest.builder()
                    .mailTo(gdpUser.getEmail())
                    .subject(FIND_ID_MAIL_SUBJECT)
                    .htmlContents(templated)
                    .build();

            MimeMessage message = createMail(mailRequest);
            mailSender.send(message);

            log.info("ID 찾기 메일 발송 완료 - 수신자: {}", gdpUser.getEmail());
        } else {
            log.warn("해당 이메일에 해당하는 사용자를 찾을 수 없음 - email: {}", request.getEmail());
        }

        return FindResponse.builder()
                .isFound(isFound)
                .build();
    }

    public FindResponse sendGenerateTempPasswordEmail(FindPasswordRequest request) throws MessagingException {
        log.info("임시 비밀번호 이메일 발송 요청 수신 - ID: {}, email: {}", request.getId(), request.getEmail());

        boolean isFound = false;

        GdpUser gdpUser = userRepository.findBySocialTypeAndIdAndEmail(SocialType.GDP, request.getId(), request.getEmail())
                .orElse(null);

        if (gdpUser != null) {
            isFound = true;
            log.info("사용자 조회 성공 - ID: {}, 이름: {}", gdpUser.getId(), gdpUser.getName());

            String tempPassword = createTempPassword(gdpUser);
            log.info("임시 비밀번호 생성 완료");

            String templated = applyTemplateToFindPasswordMail(gdpUser, tempPassword);
            log.info("임시 비밀번호 메일 템플릿 생성 완료");

            MailRequest mailRequest = MailRequest.builder()
                    .mailTo(gdpUser.getEmail())
                    .subject(GENERATE_TEMP_PASSWORD_MAIL_SUBJECT)
                    .htmlContents(templated)
                    .build();

            MimeMessage message = createMail(mailRequest);
            mailSender.send(message);

            log.info("임시 비밀번호 메일 발송 완료 - 수신자: {}", gdpUser.getEmail());
        } else {
            log.warn("사용자 조회 실패 - ID: {}, email: {}", request.getId(), request.getEmail());
        }

        return FindResponse.builder()
                .isFound(isFound)
                .build();
    }

    private MimeMessage createMail(MailRequest request) throws MessagingException {
        log.info("메일 객체 생성 시작 - 수신자: {}, 제목: {}", request.getMailTo(), request.getSubject());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(request.getMailFrom());
        helper.setTo(request.getMailTo());
        helper.setSubject(request.getSubject());
        helper.setText(request.getHtmlContents(), true);

        log.info("메일 객체 생성 완료");
        return message;
    }

    private String applyTemplateToFindIdMail(GdpUser gdpUser) {
        log.info("ID 찾기 템플릿 적용 - ID: {}", gdpUser.getId());

        Context context = new Context();
        context.setVariable("username", gdpUser.getName());
        context.setVariable("userId", gdpUser.getId());

        return templateEngine.process("find-id-template", context);
    }

    private String applyTemplateToFindPasswordMail(GdpUser gdpUser, String tempPassword) {
        log.info("임시 비밀번호 템플릿 적용 - ID: {}", gdpUser.getId());

        Context context = new Context();
        context.setVariable("username", gdpUser.getName());
        context.setVariable("tempPassword", tempPassword);

        return templateEngine.process("find-password-template", context);
    }

    private String createTempPassword(GdpUser gdpUser) {
        String tempPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        log.info("임시 비밀번호 생성 - ID: {}", gdpUser.getId());

        String encoded = passwordEncoder.encode(tempPassword);
        GdpUser changed = gdpUser.changePassword(encoded);
        userRepository.save(changed);

        log.info("임시 비밀번호 저장 완료 - ID: {}", gdpUser.getId());
        return tempPassword;
    }
}
