package com.nhn.service;

import java.io.File;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nhn.properties.MailSetting;
import com.nhn.util.VelocityUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final MailSetting mailSetting;

    private final JavaMailSender mailSender;

    public void sendHtmlMail(final String to, final String subject,
                             final String content) throws MessagingException {
        final MimeMessage       message       = this.mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(message,
                                                                      true);
        messageHelper.setFrom(this.mailSetting.getUsername());
        messageHelper.setTo(to);

        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        this.mailSender.send(message);
        log.info("sendHtmlMail to={}", to);
    }

    @Async
    public void sendTemplateMail(final String to, final String subject,
                                 final Map<String, Object> paramMap,
                                 final String vmFile) throws Exception {
        final VelocityContext context = new VelocityContext();
        for (final Map.Entry<String, Object> entry : paramMap.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        final String vmFolder = new File(this.mailSetting.getTemplate()).getAbsolutePath();

        // template file path
        final String filePath = vmFolder + File.separator + vmFile;
        log.info("sendTemplateMail fileOriginalPath={}", filePath);

        final String emailContent = VelocityUtil.generate(vmFolder, vmFile,
                                                          context);
        this.sendHtmlMail(to, subject, emailContent);
        log.info("sendTemplateMail paramsMap={}，template={}", paramMap, vmFile);
    }
}
