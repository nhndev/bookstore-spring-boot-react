package com.nhn.factory.email;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

//@Service
public class SendEmailService {
	
	@Value(value = "${spring.mail.username}")
	private String sender;

	private final String verifyAccountRegisterTemplatePath = "/templates/emails/EmailVerifyAccountRegister.html";

	private final String verifyAccountCreateTemplatePath = "/templates/emails/EmailVerifyAccountCreate.html";

	private final String forgotPasswordTemplatePath = "/templates/emails/EmailForgotPassword.html";
	
	@Autowired
	JavaMailSender javaMailSender;
	
	private String proceedData(final String templatePath, final Map<String, String> properties) throws IOException {

		final Resource resource = new ClassPathResource(templatePath);
		final File file = resource.getFile();
		
		String html = Files.readString(file.toPath());
		
		for(final Entry<String, String> entry : properties.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			html = html.replace("${" + key + "}", value);
		}
		
		return html;
	}

	@Async
	public void sendVerifyAccountRegister(final Map<String, String> properties, final String toAddress) throws MessagingException, IOException {
		
		final MimeMessage message = this.javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
		
		helper.setFrom(this.sender, "BookStore");
		helper.setTo(toAddress);
		helper.setText(this.proceedData(this.verifyAccountRegisterTemplatePath, properties), true);
		helper.setSubject("Xác minh tài khoản");

        this.javaMailSender.send(message);
		
	}

	@Async
	public void sendVerifyAccountCreate(final Map<String, String> properties, final String toAddress) throws MessagingException, IOException {

		final MimeMessage message = this.javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

		helper.setFrom(this.sender, "BookStore");
		helper.setTo(toAddress);
		helper.setText(this.proceedData(this.verifyAccountCreateTemplatePath, properties), true);
		helper.setSubject("Xác minh tài khoản");

        this.javaMailSender.send(message);

	}

	@Async
	public void sendForgotPasswordEmail(final Map<String, String> properties, final String toAddress) throws MessagingException, IOException {

		final MimeMessage message = this.javaMailSender.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

		helper.setFrom(this.sender, "BookStore");
		helper.setTo(toAddress);
		helper.setText(this.proceedData(this.forgotPasswordTemplatePath, properties), true);
		helper.setSubject("Đặt lại mật khẩu");

        this.javaMailSender.send(message);

	}

}