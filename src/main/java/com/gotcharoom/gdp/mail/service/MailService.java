package com.gotcharoom.gdp.mail.service;

import com.gotcharoom.gdp.global.security.SocialType;
import com.gotcharoom.gdp.mail.model.FindIdRequest;
import com.gotcharoom.gdp.mail.model.FindPasswordRequest;
import com.gotcharoom.gdp.mail.model.FindResponse;
import com.gotcharoom.gdp.mail.model.MailRequest;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
public class MailService {

    private static final String FIND_ID_MAIL_SUBJECT = "GDP ID 찾기 메일입니다";

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
        boolean isFound = false;

        GdpUser gdpUser = userRepository.findBySocialTypeAndEmail(SocialType.GDP, request.getEmail())
                .orElse(null);

        if (gdpUser != null) {
            isFound = true;
            String templated = applyTemplateToFindIdMail(gdpUser);

            MailRequest mailRequest = MailRequest.builder()
                    .mailTo(gdpUser.getEmail())
                    .subject(FIND_ID_MAIL_SUBJECT)
                    .htmlContents(templated)
                    .build();
            MimeMessage message = createMail(mailRequest);
            mailSender.send(message);
        }

        return FindResponse.builder()
                .isFound(isFound)
                .build();
    }

    public FindResponse sendGenerateTempPasswordEmail(FindPasswordRequest request) throws MessagingException {
        boolean isFound = false;

        GdpUser gdpUser = userRepository.findBySocialTypeAndIdAndEmail(SocialType.GDP, request.getId(), request.getEmail())
                .orElse(null);

        if (gdpUser != null) {
            isFound = true;
            String tempPassword = createTempPassword(gdpUser);
            String templated = applyTemplateToFindPasswordMail(gdpUser, tempPassword);

            MailRequest mailRequest = MailRequest.builder()
                    .mailTo(gdpUser.getEmail())
                    .subject(FIND_ID_MAIL_SUBJECT)
                    .htmlContents(templated)
                    .build();
            MimeMessage message = createMail(mailRequest);
            mailSender.send(message);
        }

        return FindResponse.builder()
                .isFound(isFound)
                .build();
    }

    private MimeMessage createMail(MailRequest request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(request.getMailFrom());
        helper.setTo(request.getMailTo());
        helper.setSubject(request.getSubject());
        helper.setText(request.getHtmlContents(), true);

        return message;
    }

    private String applyTemplateToFindIdMail(GdpUser gdpUser) {
        Context context = new Context();
        context.setVariable("username", gdpUser.getName());
        context.setVariable("userId", gdpUser.getId());

        return templateEngine.process("find-id-template", context);
    }

    private String applyTemplateToFindPasswordMail(GdpUser gdpUser, String tempPassword) {
        Context context = new Context();
        context.setVariable("username", gdpUser.getName());

        context.setVariable("tempPassword", tempPassword);

        return templateEngine.process("find-password-template", context);
    }

    private String createTempPassword(GdpUser gdpUser) {
        String tempPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 10);;

        String encoded = passwordEncoder.encode(tempPassword);
        gdpUser.changePassword(encoded);
        userRepository.save(gdpUser);

        return tempPassword;
    }
}
